package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.revpay.dto.InvoiceItemRequest;
import com.revpay.dto.InvoiceSummaryResponse;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.dto.CreateInvoiceRequest;
import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;
import com.revpay.repository.InvoiceItemRepository;
import com.revpay.repository.InvoiceRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.InvoiceService;
import com.revpay.service.NotificationService;
import com.revpay.service.TransactionService;
import com.revpay.service.AuthService;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LogManager.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final AuthService authService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceItemRepository invoiceItemRepository,
                              UserRepository userRepository,
                              TransactionService transactionService,
                              NotificationService notificationService,
                              AuthService authService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @Override
    public Invoice createInvoice(CreateInvoiceRequest request) {

        log.info("Creating invoice for businessId={} customerId={}",
                request.getBusinessId(), request.getCustomerId());

        // Fetch business user
        User business = userRepository.findById(request.getBusinessId())
                .orElseThrow(() -> {
                    log.warn("Invoice creation failed: business not found businessId={}",
                            request.getBusinessId());
                    return new IllegalArgumentException("Business not found");
                });

        if (business.getUserType() != UserType.BUSINESS) {
            log.warn("Invoice creation failed: user is not business userId={}",
                    business.getUserId());
            throw new IllegalStateException("Only business users can create invoices");
        }

        // Fetch customer user
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Invoice creation failed: customer not found customerId={}",
                            request.getCustomerId());
                    return new IllegalArgumentException("Customer not found");
                });

        // Create Invoice (DRAFT)
        Invoice invoice = new Invoice();
        invoice.setBusiness(business);
        invoice.setCustomerId(customer.getUserId());
        invoice.setCustomerName(request.getCustomerName());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setDueDate(request.getDueDate());
        invoice.setCreatedAt(LocalDateTime.now());

        // Calculate total amount (BigDecimal ONLY)
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InvoiceItemRequest itemReq : request.getItems()) {

            BigDecimal itemTotal =
                    itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }

        invoice.setTotalAmount(totalAmount);

        // Save invoice first (to generate invoiceId)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice created successfully invoiceId={} totalAmount={}",
                savedInvoice.getInvoiceId(), savedInvoice.getTotalAmount());

        // Save invoice items
        for (InvoiceItemRequest itemReq : request.getItems()) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setItemName(itemReq.getItemName());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());

            invoiceItemRepository.save(item);
        }

        return savedInvoice;
    }

    @Override
    public void sendInvoice(Long invoiceId) {

        log.info("Sending invoice invoiceId={}", invoiceId);

        if (invoiceId == null) {
            throw new IllegalArgumentException("InvoiceId is required");
        }

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.warn("Send invoice failed: invoice not found invoiceId={}", invoiceId);
                    return new IllegalArgumentException("Invoice not found");
                });

        // Validate current status
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            log.warn("Send invoice failed: invoice not in DRAFT state invoiceId={}", invoiceId);
            throw new IllegalStateException("Only DRAFT invoices can be sent");
        }

        // Mark invoice as SENT
        invoice.setStatus(InvoiceStatus.SENT);
        invoiceRepository.save(invoice);

        log.info("Invoice marked as SENT invoiceId={}", invoiceId);

        // Notify customer
        notificationService.sendNotification(
                invoice.getCustomerId(),
                "Invoice #" + invoice.getInvoiceId() + " has been sent",
                NotificationType.INVOICE
        );
    }

    @Override
    public void payInvoice(Long invoiceId, Long customerId, String transactionPin) {

        log.info("Invoice payment attempt invoiceId={} customerId={}",
                invoiceId, customerId);

        // Basic validation
        if (invoiceId == null || customerId == null) {
            throw new IllegalArgumentException("InvoiceId and CustomerId are required");
        }

        if (transactionPin == null || transactionPin.isBlank()) {
            throw new IllegalArgumentException("Transaction PIN is required");
        }

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.warn("Invoice payment failed: invoice not found invoiceId={}", invoiceId);
                    return new IllegalArgumentException("Invoice not found");
                });

        // Validate invoice status
        if (invoice.getStatus() != InvoiceStatus.SENT) {
            log.warn("Invoice payment failed: invoice not in SENT state invoiceId={}", invoiceId);
            throw new IllegalStateException("Only SENT invoices can be paid");
        }

        // Validate customer
        if (!invoice.getCustomerId().equals(customerId)) {
            log.warn("Invoice payment failed: customer mismatch invoiceId={} customerId={}",
                    invoiceId, customerId);
            throw new IllegalStateException("This invoice does not belong to the customer");
        }

        // Fetch customer & business
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Invoice payment failed: customer not found customerId={}", customerId);
                    return new IllegalArgumentException("Customer not found");
                });

        User business = invoice.getBusiness();

        // Verify transaction PIN (customer pays)
        boolean pinValid = authService.verifyTransactionPin(customer, transactionPin);
        if (!pinValid) {
            log.warn("Invoice payment failed: invalid transaction PIN customerId={}", customerId);
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Perform money transfer (creates TRANSACTION internally)
        transactionService.sendMoney(
                customer.getUserId(),          // sender
                business.getUserId(),          // receiver
                invoice.getTotalAmount(),
                transactionPin,
                "Invoice Payment (Invoice #" + invoice.getInvoiceId() + ")"
        );

        // Mark invoice as PAID
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        log.info("Invoice paid successfully invoiceId={} amount={}",
                invoiceId, invoice.getTotalAmount());

        // Notify business
        notificationService.sendNotification(
                business.getUserId(),
                "Invoice #" + invoice.getInvoiceId() + " has been paid",
                NotificationType.INVOICE
        );
    }

    @Override
    public void cancelInvoice(Long invoiceId) {

        log.info("Cancelling invoice invoiceId={}", invoiceId);

        if (invoiceId == null) {
            throw new IllegalArgumentException("InvoiceId is required");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.warn("Cancel invoice failed: invoice not found invoiceId={}", invoiceId);
                    return new IllegalArgumentException("Invoice not found");
                });

        if (invoice.getStatus() != InvoiceStatus.DRAFT
                && invoice.getStatus() != InvoiceStatus.SENT) {
            log.warn("Cancel invoice failed: invalid state invoiceId={} status={}",
                    invoiceId, invoice.getStatus());
            throw new IllegalStateException(
                    "Only DRAFT or SENT invoices can be cancelled"
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);

        log.info("Invoice cancelled invoiceId={}", invoiceId);
    }

    @Override
    public List<Invoice> getInvoicesForBusiness(Long businessId) {

        log.info("Fetching invoices for businessId={}", businessId);

        if (businessId == null) {
            throw new IllegalArgumentException("BusinessId is required");
        }

        User business = userRepository.findById(businessId)
                .orElseThrow(() -> {
                    log.warn("Business invoices fetch failed: business not found businessId={}", businessId);
                    return new IllegalArgumentException("Business not found");
                });

        if (business.getUserType() != UserType.BUSINESS) {
            log.warn("Business invoices fetch failed: user is not business userId={}", businessId);
            throw new IllegalStateException("User is not a business");
        }

        return invoiceRepository.findByBusiness(business);
    }

    @Override
    public List<Invoice> getInvoicesForCustomer(Long customerId) {

        log.info("Fetching invoices for customerId={}", customerId);

        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId is required");
        }

        userRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer invoices fetch failed: customer not found customerId={}", customerId);
                    return new IllegalArgumentException("Customer not found");
                });

        return invoiceRepository.findByCustomerId(customerId);
    }

    @Override
    public InvoiceSummaryResponse getInvoiceSummary(Long businessId) {

        log.info("Generating invoice summary for businessId={}", businessId);

        InvoiceSummaryResponse response = new InvoiceSummaryResponse();

        response.setTotalPaid(
                invoiceRepository.getTotalPaid(businessId)
        );

        response.setTotalPending(
                invoiceRepository.getTotalPending(businessId)
        );

        response.setTotalOverdue(
                invoiceRepository.getTotalOverdue(businessId)
        );

        return response;
    }
}
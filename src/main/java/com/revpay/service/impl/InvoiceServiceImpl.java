package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.revpay.dto.InvoiceItemRequest;
import com.revpay.dto.InvoiceSummaryResponse;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Basic validations
        if (request.getBusinessId() == null) {
            throw new IllegalArgumentException("BusinessId is required");
        }

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("CustomerId is required");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one item");
        }

        // Fetch business user
        User business = userRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (business.getUserType() != UserType.BUSINESS) {
            throw new IllegalStateException("Only business users can create invoices");
        }

        // Fetch customer user
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

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

            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be greater than zero");
            }

            if (itemReq.getPrice() == null
                    || itemReq.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Item price must be greater than zero");
            }

            BigDecimal itemTotal =
                    itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);
        }

        invoice.setTotalAmount(totalAmount);

        // Save invoice first (to generate invoiceId)
        Invoice savedInvoice = invoiceRepository.save(invoice);

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

        if (invoiceId == null) {
            throw new IllegalArgumentException("InvoiceId is required");
        }

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        // Validate current status
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be sent");
        }

        // Mark invoice as SENT
        invoice.setStatus(InvoiceStatus.SENT);
        invoiceRepository.save(invoice);

        // Notify customer
        notificationService.sendNotification(
                invoice.getCustomerId(),
                "Invoice #" + invoice.getInvoiceId() + " has been sent",
                NotificationType.INVOICE
        );
    }

    @Override
    public void payInvoice(Long invoiceId, Long customerId, String transactionPin) {

        // Basic validation
        if (invoiceId == null || customerId == null) {
            throw new IllegalArgumentException("InvoiceId and CustomerId are required");
        }

        if (transactionPin == null || transactionPin.isBlank()) {
            throw new IllegalArgumentException("Transaction PIN is required");
        }

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        // Validate invoice status
        if (invoice.getStatus() != InvoiceStatus.SENT) {
            throw new IllegalStateException("Only SENT invoices can be paid");
        }

        // Validate customer
        if (!invoice.getCustomerId().equals(customerId)) {
            throw new IllegalStateException("This invoice does not belong to the customer");
        }

        // Fetch customer & business
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        User business = invoice.getBusiness();

        // Verify transaction PIN (customer pays)
        boolean pinValid = authService.verifyTransactionPin(customer, transactionPin);
        if (!pinValid) {
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

        // Notify business
        notificationService.sendNotification(
                business.getUserId(),
                "Invoice #" + invoice.getInvoiceId() + " has been paid",
                NotificationType.INVOICE
        );
    }

    @Override
    public void cancelInvoice(Long invoiceId) {

        if (invoiceId == null) {
            throw new IllegalArgumentException("InvoiceId is required");
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT
                && invoice.getStatus() != InvoiceStatus.SENT) {
            throw new IllegalStateException(
                    "Only DRAFT or SENT invoices can be cancelled"
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getInvoicesForBusiness(Long businessId) {

        if (businessId == null) {
            throw new IllegalArgumentException("BusinessId is required");
        }

        User business = userRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (business.getUserType() != UserType.BUSINESS) {
            throw new IllegalStateException("User is not a business");
        }

        return invoiceRepository.findByBusiness(business);
    }

    @Override
    public List<Invoice> getInvoicesForCustomer(Long customerId) {

        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId is required");
        }

        userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return invoiceRepository.findByCustomerId(customerId);
    }

    @Override
    public InvoiceSummaryResponse getInvoiceSummary(Long businessId) {

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
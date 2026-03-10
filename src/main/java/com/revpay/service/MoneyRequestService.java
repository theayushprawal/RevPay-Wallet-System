package com.revpay.service;

import com.revpay.model.MoneyRequest;

import java.math.BigDecimal;
import java.util.List;

public interface MoneyRequestService {

    /**
     * Create a money request from sender to receiver
     */
    void createRequest(Long senderId,
                       Long receiverId,
                       BigDecimal amount,
                       String note);

    /**
     * Receiver accepts the request
     * → money is transferred
     */
    void acceptRequest(Long requestId,
                       String transactionPin);

    /**
     * Receiver declines the request
     */
    void declineRequest(Long requestId);

    /**
     * Sender cancels the request
     */
    void cancelRequest(Long requestId);

    List<MoneyRequest> getIncomingRequests(Long userId);
    List<MoneyRequest> getOutgoingRequests(Long userId);
}
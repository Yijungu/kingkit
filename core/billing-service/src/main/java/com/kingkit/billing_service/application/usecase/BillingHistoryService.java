package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.dto.response.PaymentHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BillingHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponse> getUserBillingHistory(Long userId, Pageable pageable) {
        return paymentHistoryRepository.findBySubscription_UserId(userId, pageable)
                .map(PaymentHistoryResponse::from);
    }
}

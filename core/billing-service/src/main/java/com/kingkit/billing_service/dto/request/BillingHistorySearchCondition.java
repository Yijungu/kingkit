package com.kingkit.billing_service.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
public class BillingHistorySearchCondition {

    private int page = 0;  // 기본값: 0
    private int size = 10; // 기본값: 10

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}

package com.kingkit.billing_service.api.webhook;

import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.application.usecase.WebhookHandlerService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookHandlerService webhookHandlerService;

    @PostMapping("/toss")
    public ResponseEntity<Void> handleTossWebhook(
            @RequestBody TossWebhookRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("1 : {}", request);
        try {
            webhookHandlerService.handle(request);
        } catch (Exception e) {
            // Toss 측엔 항상 200 OK 응답 (재시도 유도)
            log.error("🔥 [Webhook] 처리 중 예외 발생 - {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }
}

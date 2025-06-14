-- 💳 결제 수단 테이블
CREATE TABLE payment_methods (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    billing_key VARCHAR(100) NOT NULL,
    card_company VARCHAR(30) NOT NULL,
    card_number_masked VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL,
    CONSTRAINT uk_user_billing_key UNIQUE (user_id, billing_key)
);

CREATE INDEX idx_user_active ON payment_methods (user_id, is_active);
CREATE INDEX idx_billing_key ON payment_methods (billing_key);

-- 📦 구독 상품 플랜 테이블
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    plan_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    price BIGINT NOT NULL,
    duration_days INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_is_active ON subscription_plans (is_active);

-- 📬 구독 인스턴스 테이블
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    payment_method_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    next_billing_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_user_id
    ON subscriptions(user_id);

CREATE INDEX idx_next_billing_at ON subscriptions(next_billing_at);

-- 💰 결제 성공 기록 테이블
CREATE TABLE payment_histories (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    payment_key VARCHAR(255) NOT NULL UNIQUE,
    order_id VARCHAR(255) NOT NULL,
    paid_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    description TEXT,
    pg_response_raw TEXT,
    retry_count INTEGER NOT NULL,

    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_histories_subscription_id
    ON payment_histories(subscription_id);
CREATE INDEX idx_order_id ON payment_histories(order_id);
CREATE INDEX idx_paid_at ON payment_histories(paid_at);

-- ❌ 결제 실패 기록 테이블
CREATE TABLE payment_failures (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    failed_at TIMESTAMP NOT NULL,
    reason VARCHAR(500) NOT NULL,
    retry_count INTEGER NOT NULL,
    retry_scheduled_at TIMESTAMP NOT NULL,
    resolved BOOLEAN NOT NULL,

    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_failures_subscription_id
    ON payment_failures(subscription_id);
CREATE INDEX idx_retry_scheduled_at ON payment_failures(retry_scheduled_at);
CREATE INDEX idx_resolved ON payment_failures(resolved);

-- ⏱️ 정기 트리거 로그 테이블
CREATE TABLE scheduled_billing_triggers (
    id BIGSERIAL PRIMARY KEY,
    trigger_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    result VARCHAR(10) NOT NULL,
    failure_reason VARCHAR(1000),
    triggered_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_trigger_date ON scheduled_billing_triggers(trigger_date);
CREATE INDEX IF NOT EXISTS idx_triggers_user_id
    ON scheduled_billing_triggers(user_id);


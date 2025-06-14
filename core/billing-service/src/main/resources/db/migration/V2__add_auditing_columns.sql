-- payment_histories 감사 컬럼
ALTER TABLE payment_histories
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- (필요하다면 다른 테이블도 동일하게 추가)
-- ALTER TABLE payment_failures ...

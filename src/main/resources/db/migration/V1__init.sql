CREATE TABLE mpesa_transactions (
                                    id                    UUID PRIMARY KEY,
                                    phone_number          VARCHAR(15) NOT NULL,
                                    amount                NUMERIC(12,2) NOT NULL,
                                    account_reference     VARCHAR(50),
                                    status                VARCHAR(20) NOT NULL,   -- PENDING, SUCCESS, FAILED
                                    merchant_request_id   VARCHAR(100),
                                    checkout_request_id   VARCHAR(100) UNIQUE,
                                    mpesa_receipt_number  VARCHAR(50),
                                    result_code           INTEGER,
                                    result_desc           VARCHAR(255),
                                    callback_payload      JSONB,
                                    created_at            TIMESTAMPTZ NOT NULL,
                                    updated_at            TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_mpesa_checkout_request ON mpesa_transactions(checkout_request_id);
CREATE TABLE ledger_entries (
    id                  BIGSERIAL PRIMARY KEY,
    subscription_id     BIGINT         NOT NULL,
    user_id             VARCHAR(255)   NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    status              VARCHAR(20)    NOT NULL,   -- SUCCEEDED / DECLINED / REFUNDED
    provider_reference  VARCHAR(255),              -- charge ref (null if declined)
    created_at          TIMESTAMP      NOT NULL
);
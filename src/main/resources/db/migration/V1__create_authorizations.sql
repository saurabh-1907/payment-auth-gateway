CREATE TABLE authorizations (
    id UUID PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL,
    amount_minor BIGINT NOT NULL CHECK (amount_minor > 0),
    currency CHAR(3) NOT NULL,
    pan_last4 CHAR(4) NOT NULL,
    status VARCHAR(20) NOT NULL,
    issuer_code VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_authorizations_client_id ON authorizations(client_id);

CREATE TABLE authorization_events (
    id BIGSERIAL PRIMARY KEY,
    authorization_id UUID NOT NULL REFERENCES authorizations(id),
    event_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_authorization_events_auth_id ON authorization_events(authorization_id);

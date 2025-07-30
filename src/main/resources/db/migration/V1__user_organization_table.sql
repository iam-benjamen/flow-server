CREATE TABLE organizations
(
    id         UUID         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    logo_url   VARCHAR(255),
    is_active  BOOLEAN      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              UUID         NOT NULL,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    is_active       BOOLEAN      NOT NULL,
    email_verified  BOOLEAN      NOT NULL,
    role            VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    organization_id UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);
CREATE SCHEMA sso;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create sequence sso.system_oauth2_clients_sq START 1;

CREATE TABLE sso.system_oauth2_clients
(
    system_client_id              BIGINT        NOT NULL DEFAULT nextval('sso.system_oauth2_clients_sq'),
    client_id                     VARCHAR(100)  NOT NULL,
    client_id_issued_at           TIMESTAMP              DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 VARCHAR(200),
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(200)  NOT NULL,
    client_authentication_methods VARCHAR(1000) NOT NULL,
    authorization_grant_types     VARCHAR(1000) NOT NULL,
    redirect_uris                 VARCHAR(1000),
    scopes                        VARCHAR(1000) NOT NULL,
    client_settings               VARCHAR(2000),
    token_settings                VARCHAR(2000),
    constraint system_oauth2_clients_pk primary key (system_client_id)
);

COMMENT ON table sso.system_oauth2_clients IS 'OAuth2 клиенты системы';
COMMENT ON column sso.system_oauth2_clients.client_id IS 'ID клиента';
COMMENT ON column sso.system_oauth2_clients.client_id_issued_at IS 'Дата создания записи';
COMMENT ON column sso.system_oauth2_clients.client_secret IS 'Пароль';
COMMENT ON column sso.system_oauth2_clients.client_secret_expires_at IS 'Срок действия пароля';
COMMENT ON column sso.system_oauth2_clients.client_name IS 'Наименование клиента';
COMMENT ON column sso.system_oauth2_clients.client_authentication_methods IS 'Доступные методы аутентификации';
COMMENT ON column sso.system_oauth2_clients.authorization_grant_types IS 'Типы доступа';
COMMENT ON column sso.system_oauth2_clients.redirect_uris IS 'Доступные URL-ы перенаправления';
COMMENT ON column sso.system_oauth2_clients.scopes IS 'Области доступа';
COMMENT ON column sso.system_oauth2_clients.client_settings IS 'Дополнительные настройки клиента';
COMMENT ON column sso.system_oauth2_clients.token_settings IS 'Дополнительные настройки токена';

CREATE UNIQUE INDEX idx_system_oauth2_clients_n1 ON sso.system_oauth2_clients (client_id);

CREATE TABLE IF NOT EXISTS sso.users
(
    user_id               UUID                        NOT NULL DEFAULT uuid_generate_v4(),
    email                 VARCHAR(100)                NOT NULL,
    password_hash         VARCHAR(500),
    first_name            varchar(100)                NOT NULL,
    last_name             varchar(100)                NOT NULL,
    middle_name           varchar(100),
    birthday              date,
    avatar_url            varchar(255),
    active                boolean                     not null default false,

    created_by            VARCHAR(50)                 NOT NULL DEFAULT 'system',
    created_date          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    last_updated_by       VARCHAR(50)                 NOT NULL DEFAULT 'system',
    last_updated_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    object_version_number INTEGER                     NOT NULL DEFAULT 0,
    constraint users_pk PRIMARY KEY (user_id)
);

COMMENT ON TABLE sso.users IS 'Пользователи';
COMMENT ON COLUMN sso.users.user_id IS 'УИ пользователя';
COMMENT ON COLUMN sso.users.email IS 'Логин пользователя';
COMMENT ON COLUMN sso.users.password_hash IS 'Хэш пароля';
COMMENT ON COLUMN sso.users.first_name IS 'Начало действия учетной записи';
COMMENT ON COLUMN sso.users.last_name IS 'Окончание действия учетной записи';
COMMENT ON COLUMN sso.users.middle_name IS 'Статус пользователя, активен или неактивен';
COMMENT ON COLUMN sso.users.birthday IS 'Пользователь, создавший текущую запись';
COMMENT ON COLUMN sso.users.avatar_url IS 'Ссылка на аватар';

COMMENT ON column sso.users.created_by IS 'Логин пользователя, создавшего запись';
COMMENT ON column sso.users.created_date IS 'Дата создания записи';
COMMENT ON column sso.users.last_updated_by IS 'Логин пользователя, изменившего запись';
COMMENT ON column sso.users.last_updated_date IS 'Дата последнего обновления записи';
COMMENT ON column sso.users.object_version_number IS 'Номер версии записи в БД';

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_u1 ON sso.users (email);


INSERT INTO sso.users (email, password_hash, first_name, last_name, middle_name, birthday, avatar_url, active)
VALUES ('admin@example.com', '$2a$10$VUqrcPxSpEhmYjIZ5zbygu3bEf1KHw8A8Vm4agZwh061SVFGr2OUG', 'Иван', 'Иванов',
        'Иванович', '1978-03-12', null, true);

INSERT INTO sso.system_oauth2_clients(client_id, client_secret,
                                      client_secret_expires_at,
                                      client_name, client_authentication_methods,
                                      authorization_grant_types, redirect_uris,
                                      scopes, client_settings, token_settings)
VALUES ('test-client', '$2a$10$sfxLgyYcbf5BK9CWXwOoGuxYM5ASuowBDlg5ca.M/wwy1LUImA35a',
        to_timestamp('2072-01-01', 'YYYY-MM-DD'), 'Тестовый клиент системы',
        'client_secret_basic', 'authorization_code,refresh_token',
        'http://localhost:8080/code', 'read.scope,write.scope', null, null);
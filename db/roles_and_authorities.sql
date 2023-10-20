CREATE TABLE IF NOT EXISTS sso.authorities
(
    id                    UUID                        NOT NULL default uuid_generate_v4(),
    code                  VARCHAR(100)                NOT NULL,
    description           VARCHAR(500)                NOT NULL,
    system_code           VARCHAR(50)                 NOT NULL,
    active                boolean                     not null default true,
    created_by            VARCHAR(50)                 NOT NULL DEFAULT 'system',
    created_date          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    last_updated_by       VARCHAR(50)                 NOT NULL DEFAULT 'system',
    last_updated_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    object_version_number INTEGER                     NOT NULL DEFAULT 0,
    constraint authorities_pk PRIMARY KEY (id)
);

COMMENT ON TABLE sso.authorities IS 'Справочник привилегий';
COMMENT ON COLUMN sso.authorities.id IS 'Уникальный идентификатор привилегии';
COMMENT ON COLUMN sso.authorities.code IS 'Код привилегии';
COMMENT ON COLUMN sso.authorities.description IS 'Описание привилегии';
COMMENT ON COLUMN sso.authorities.system_code IS 'Код системы, к которой принадлежит привилегия';
COMMENT ON COLUMN sso.authorities.active IS 'Флаг активности';
COMMENT ON column sso.authorities.created_by IS 'Логин пользователя, создавшего запись';
COMMENT ON column sso.authorities.created_date IS 'Дата создания записи';
COMMENT ON column sso.authorities.last_updated_by IS 'Логин пользователя, изменившего запись';
COMMENT ON column sso.authorities.last_updated_date IS 'Дата последнего обновления записи';
COMMENT ON column sso.authorities.object_version_number IS 'Номер версии записи в БД';

CREATE UNIQUE INDEX idx_authorities_u1 ON sso.authorities (code, system_code);

CREATE TABLE IF NOT EXISTS sso.roles
(
    id                    UUID                        NOT NULL default uuid_generate_v4(),
    code                  VARCHAR(50)                 NOT NULL,
    description           VARCHAR(500)                NOT NULL,
    system_code           VARCHAR(50),
    active                boolean                     not null default true,
    created_by            VARCHAR(50)                 NOT NULL DEFAULT 'system',
    created_date          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    last_updated_by       VARCHAR(50)                 NOT NULL DEFAULT 'system',
    last_updated_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    object_version_number INTEGER                     NOT NULL DEFAULT 0,
    constraint roles_pk PRIMARY KEY (id),
    unique (code)
);

COMMENT ON TABLE sso.roles IS 'Справочник ролей';
COMMENT ON COLUMN sso.roles.id IS 'Уникальный идентификатор роли';
COMMENT ON COLUMN sso.roles.code IS 'Код роли';
COMMENT ON COLUMN sso.roles.description IS 'Описание роли';
COMMENT ON COLUMN sso.roles.system_code IS 'Код системы, к которой принадлежит привилегия';
COMMENT ON COLUMN sso.roles.active IS 'Флаг активности';
COMMENT ON column sso.roles.created_by IS 'Логин пользователя, создавшего запись';
COMMENT ON column sso.roles.created_date IS 'Дата создания записи';
COMMENT ON column sso.roles.last_updated_by IS 'Логин пользователя, изменившего запись';
COMMENT ON column sso.roles.last_updated_date IS 'Дата последнего обновления записи';
COMMENT ON column sso.roles.object_version_number IS 'Номер версии записи в БД';

CREATE UNIQUE INDEX idx_roles_u1 ON sso.roles (code, system_code);

CREATE TABLE IF NOT EXISTS sso.role_authorities
(
    id           UUID                        NOT NULL DEFAULT uuid_generate_v4(),
    role_id      UUID                        NOT NULL,
    authority_id UUID                        NOT NULL,
    created_by   VARCHAR(50)                 NOT NULL DEFAULT 'system',
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    constraint role_authorities_pk PRIMARY KEY (id),
    constraint role_authorities_fk_1 foreign key (authority_id) references sso.authorities (id),
    constraint role_authorities_fk_2 foreign key (role_id) references sso.roles (id)
);

COMMENT ON TABLE sso.role_authorities IS 'Маппинг ролей и привилегий';
COMMENT ON COLUMN sso.role_authorities.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN sso.role_authorities.role_id IS 'Уникальный код привилегии';
COMMENT ON COLUMN sso.role_authorities.authority_id IS 'Уникальный код привилегии';
COMMENT ON column sso.role_authorities.created_by IS 'Логин пользователя, создавшего запись';
COMMENT ON column sso.role_authorities.created_date IS 'Дата создания записи';

CREATE UNIQUE INDEX idx_role_authorities_u1 ON sso.role_authorities (role_id, authority_id);

CREATE SEQUENCE IF NOT EXISTS sso.user_roles_sq START 1;

CREATE TABLE IF NOT EXISTS sso.user_roles
(
    id           UUID                        NOT NULL DEFAULT uuid_generate_v4(),
    user_id      UUID                        NOT NULL,
    role_id      UUID                        NOT NULL,
    created_by   VARCHAR(50)                 NOT NULL DEFAULT 'system',
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT current_timestamp,
    constraint user_roles_pk PRIMARY KEY (id),
    constraint user_roles_fk_1 foreign key (user_id) references sso.users (id),
    constraint user_roles_fk_2 foreign key (role_id) references sso.roles (id)
);

COMMENT ON TABLE sso.user_roles IS 'Маппинг пользователей и ролей';
COMMENT ON COLUMN sso.user_roles.id IS 'УИ записи';
COMMENT ON COLUMN sso.user_roles.user_id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN sso.user_roles.role_id IS 'Уникальный идентификатор роли';
COMMENT ON column sso.user_roles.created_by IS 'Логин пользователя, создавшего запись';
COMMENT ON column sso.user_roles.created_date IS 'Дата создания записи';

CREATE UNIQUE INDEX idx_user_roles_u1 ON sso.user_roles (user_id, role_id);

INSERT INTO sso.authorities(code, description, system_code)
VALUES ('GET_OWN_DATA', 'Привилегия позволяет получить данные текущего пользователя', 'SSO');

INSERT INTO sso.authorities(code, description, system_code)
VALUES ('CHANGE_OWN_DATA', 'Привилегия позволяет изменять данные текущего пользователя', 'SSO');

INSERT INTO sso.authorities(code, description, system_code)
VALUES ('CHANGE_OWN_PASSWORD', 'Привилегия позволяет изменять пароль текущего пользователя', 'SSO');

INSERT INTO sso.authorities(code, description, system_code)
VALUES ('DELETE_OWN_ACCOUNT', 'Привилегия позволяет удалять аккаунт текущего пользователя', 'SSO');

INSERT INTO sso.roles(code, description)
VALUES ('USER_SSO', 'Роль обычного пользователя DL-SSO');

INSERT INTO sso.role_authorities(role_id, authority_id)
VALUES ((SELECT id FROM sso.roles WHERE code = 'USER_SSO'),
        (SELECT id FROM sso.authorities WHERE code = 'GET_OWN_DATA'));

INSERT INTO sso.role_authorities(role_id, authority_id)
VALUES ((SELECT id FROM sso.roles WHERE code = 'USER_SSO'),
        (SELECT id FROM sso.authorities WHERE code = 'CHANGE_OWN_DATA'));

INSERT INTO sso.role_authorities(role_id, authority_id)
VALUES ((SELECT id FROM sso.roles WHERE code = 'USER_SSO'),
        (SELECT id FROM sso.authorities WHERE code = 'CHANGE_OWN_PASSWORD'));

INSERT INTO sso.role_authorities(role_id, authority_id)
VALUES ((SELECT id FROM sso.roles WHERE code = 'USER_SSO'),
        (SELECT id FROM sso.authorities WHERE code = 'DELETE_OWN_ACCOUNT'));

INSERT INTO sso.user_roles(user_id, role_id)
SELECT id, (SELECT id FROM sso.roles WHERE code = 'USER_SSO')
FROM sso.users
ON CONFLICT DO NOTHING;
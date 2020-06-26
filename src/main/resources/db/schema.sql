CREATE TABLE IF NOT EXISTS sys_user
(
    id          varchar(36) not null
        constraint sys_user_pk
            primary key,
    username    varchar(50),
    password    varchar(100),
    mail        varchar(100),
    create_time timestamp,
    update_time timestamp,
    status      integer
);

CREATE TABLE IF NOT EXISTS instance
(
    id              varchar(36) not null
        constraint instance_pk
            primary key,
    user_id         varchar(36),
    name            varchar(100),
    description     text,
    create_time     timestamp,
    update_time     timestamp,
    status          integer,
    app_id          varchar(36),
    additional_info varchar
);


CREATE TABLE IF NOT EXISTS app
(
    id              varchar(36) not null
        constraint app_pk
            primary key,
    author          varchar(36),
    type            varchar(50),
    name            varchar(100),
    description     text,
    create_time     timestamp,
    update_time     timestamp,
    status          integer,
    additional_info varchar,
    logo            bytea,
    logo_type       varchar(10)
);


CREATE TABLE IF NOT EXISTS file
(
    id          varchar(36) not null
        constraint file_pk
            primary key,
    name        varchar(200),
    type        varchar(10),
    data        bytea,
    create_time timestamp,
    update_time timestamp,
    status      integer
);








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


CREATE TABLE IF NOT EXISTS app
(
    name            varchar(50) not null
        constraint app_pk
            primary key,
    author          varchar(36),
    type            varchar(50),
    description     text,
    create_time     timestamp,
    update_time     timestamp,
    status          integer,
    additional_info varchar,
    logo binary
);


CREATE TABLE IF NOT EXISTS app_version
(
    app_name        varchar(50) not null,
    version         varchar(50) not null,
    additional_info varchar,
    create_time     timestamp,
    update_time     timestamp,
    status          integer,
    download_status integer,
    constraint app_version_pk
        primary key (app_name, version)
);

CREATE TABLE IF NOT EXISTS instance
(
    id              varchar(36) not null
        constraint instance_pk
            primary key,
    user_id         varchar(36),
    name            varchar(100),
    description     text,
    app_name        varchar(50),
    app_version     varchar(50),
    additional_info varchar,
    running_status  integer,
    create_time     timestamp,
    update_time     timestamp,
    status          integer
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


CREATE TABLE IF NOT EXISTS app_source
(
    id         varchar(36) not null
        constraint app_source_pk
            primary key,
    version    varchar(50),
    repository varchar(100),
    create_time timestamp,
    update_time timestamp,
    status      integer
);










create table sys_user
(
	id varchar(36) not null
		constraint user_pk
			primary key,
	username varchar(50),
	password varchar(100),
	mail varchar(100),
	create_time timestamp,
	update_time timestamp,
	status integer
);

alter table sys_user owner to postgres;

create table instance
(
	id varchar(36) not null
		constraint instance_pk
			primary key,
	user_id varchar(36),
	name varchar(100),
	description text,
	create_time timestamp,
	update_time timestamp,
	status integer,
	app_id varchar(36),
	additional_info varchar
);

alter table instance owner to postgres;

create table app
(
	id varchar(36) not null
		constraint app_pk
			primary key,
	author varchar(36),
	type varchar(50),
	name varchar(100),
	description text,
	create_time timestamp,
	update_time timestamp,
	status integer,
	additional_info varchar,
	logo bytea,
	logo_type varchar(10)
);

alter table app owner to postgres;

create unique index app_id_uindex
	on app (id);


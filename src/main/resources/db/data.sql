--table user
INSERT INTO sys_user (id, username, password, mail, create_time, update_time, status)
VALUES ('00000000-0000-0000-0000-000000000000', 'admin', null, null, null, null, null);


--table app source
INSERT INTO app_source (id, version, repository)
values ('00000000-0000-0000-0000-000000000000', 'v0.0.1', null);
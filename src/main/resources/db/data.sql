-- table app
INSERT INTO app (name, author, type, description, create_time, update_time, status, additional_info) VALUES ('metabase', 'admin', 'BI', 'Metabase is the easy, open source way for everyone in your company to ask questions and learn from data.', '2020-06-28 14:36:57.581000', '2020-06-28 14:36:59.919000', 0, '{"imageName":"metabase/metabase","ports":[{"port":3000,"type":"http","entrance":true}]}');
INSERT INTO app (name, author, type, description, create_time, update_time, status, additional_info) VALUES ('nginx', 'admin', 'Web', 'nginx [engine x] is an HTTP and reverse proxy server, a mail proxy server, and a generic TCP/UDP proxy server, originally written by Igor Sysoev.', '2020-06-28 14:36:57.581000', '2020-06-28 14:36:59.919000', 0, '{"imageName":"nginx","ports":[{"port":80,"type":"http","entrance":true}]}');
-- table app source
INSERT INTO app_source (id, version, repository) VALUES ('00000000-0000-0000-0000-000000000000', 'v0.0.0', null);
-- table app version
INSERT INTO app_version (app_name, version, additional_info, create_time, update_time, status, download_status) VALUES ('metabase', 'v0.35.4', null, '2020-06-28 14:42:39.298000', '2020-06-28 14:42:40.695000', 0, 1);
INSERT INTO app_version (app_name, version, additional_info, create_time, update_time, status, download_status) VALUES ('nginx', '1.19.2', null, '2020-06-28 14:42:39.298000', '2020-06-28 14:42:40.695000', 0, 1);
-- sys user
INSERT INTO sys_user (id, username, password, mail, create_time, update_time, status) VALUES ('00000000-0000-0000-0000-000000000000', 'admin', '$2a$10$vFOaYBYNk3tkFOrZ2y5NMerpt2SuXZeshfOHiKvyEh4PSI9EseWzC', null, null, null, null);
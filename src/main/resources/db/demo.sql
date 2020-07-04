

-- table app
INSERT INTO app (name, author, type, description, create_time, update_time, status, additional_info) VALUES ('metabase', 'admin', 'BI', 'Metabase is the easy, open source way for everyone in your company to ask questions and learn from data.', '2020-06-28 14:36:57.581000', '2020-06-28 14:36:59.919000', 0, '{"imageName":"metabase/metabase:v0.35.4","ports":[3000]}');


-- table app version
INSERT INTO app_version (app_name, version, additional_info, create_time, update_time, status) VALUES ('metabase', 'v0.35.4
', '', '2020-06-28 14:42:39.298000', '2020-06-28 14:42:40.695000', 0);

-- table instance version
INSERT INTO instance (id, user_id, name, description, create_time, update_time, status, app_name, additional_info, app_version) VALUES ('b66188e0-ca2e-4afa-9a9f-a17553f9020e', '205635b9-ab37-43cb-82c2-811a58880fa1', 'my_metabase', null, '2020-07-02 15:52:54.215000', '2020-07-02 15:52:56.585000', 0, 'metabase', '{"id":null,"name":"b66188e0-ca2e-4afa-9a9f-a17553f9020e","imageName":"metabase/metabase","status":null,"ports":["31000:3000"],"labels":[],"envs":[]}', 'v0.35.4');
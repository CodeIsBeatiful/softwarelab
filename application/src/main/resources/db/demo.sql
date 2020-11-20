-- table app
--INSERT INTO app (name, author, type, description, additional_info, create_time, update_time, status)
--VALUES ('metabase', 'admin', 'BI',
--         'Metabase is the easy, open source way for everyone in your company to ask questions and learn from data.',
--         '{"imageName":"metabase/metabase:v0.35.4","ports":[3000]}', '2020-06-28 14:36:57.581000',
--         '2020-06-28 14:36:59.919000', 0);

-- table app version
--INSERT INTO app_version (app_name, version, additional_info, create_time, update_time, status)
-- VALUES ('metabase', 'v0.35.4', null, '2020-06-28 14:42:39.298000', '2020-06-28 14:42:40.695000', 0);

-- table instance version
-- INSERT INTO instance (id, user_id, name, description, app_name, app_version, running_status, additional_info,
--                       create_time, update_time, status)
-- VALUES ('b66188e0-ca2e-4afa-9a9f-a17553f9020e', '205635b9-ab37-43cb-82c2-811a58880fa1', 'my_metabase', null, 'metabase',
--         'v0.35.4', null,
--         '{"id":null,"name":"b66188e0-ca2e-4afa-9a9f-a17553f9020e","imageName":"metabase/metabase","status":null,"ports":["31000:3000"],"labels":[],"envs":[]}',
--         '2020-07-02 15:52:54.215000', '2020-07-02 15:52:56.585000', 0);

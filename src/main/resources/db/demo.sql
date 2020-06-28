

-- table app
INSERT INTO app (name, author, type, description, create_time, update_time, status, additional_info, image_path) VALUES ('metabase', 'admin', 'BI', 'Metabase is the easy, open source way for everyone in your company to ask questions and learn from data.', '2020-06-28 14:36:57.581000', '2020-06-28 14:36:59.919000', 0, '{"ports":[3000]}', null);


-- table app version
INSERT INTO app_version (app_name, version, additional_info, create_time, update_time, status) VALUES ('metabase', 'v0.35.4
', '', '2020-06-28 14:42:39.298000', '2020-06-28 14:42:40.695000', 0);
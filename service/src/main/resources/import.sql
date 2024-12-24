INSERT INTO alert_setting (coin, threshold, notify_by_email) VALUES ('BTC', 10000, TRUE);
INSERT INTO alert_setting (coin, threshold, notify_by_email) VALUES ('ETH', 15000, FALSE);

INSERT INTO post (title, content) VALUES ('Test1', 'Test1 content');
INSERT INTO post (title, content) VALUES ('Test2', 'Test2 content');

INSERT INTO app_transaction (hash, coin, amount) VALUES ('0xabc123', 'BTC', 20000);
INSERT INTO app_transaction (hash, coin, amount) VALUES ('0xdef456', 'ETH', 15000);

INSERT INTO app_user (email, username, password) VALUES ('test1@test.com', 'test1', 'pass1');
INSERT INTO app_user (email, username, password) VALUES ('test2@test.com', 'test2', 'pass2');

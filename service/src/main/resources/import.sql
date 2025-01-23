-- AlertSetting 데이터 삽입
INSERT INTO alert_setting (user_id, coin, threshold, notify_by_email) VALUES (NULL, 'KBW-BTC', 10.0, TRUE);
INSERT INTO alert_setting (user_id, coin, threshold, notify_by_email) VALUES (NULL, 'KBW-ETH', 10.0, FALSE);

-- Post 데이터 삽입
INSERT INTO post (title, content) VALUES ('Test1', 'Test1 content');
INSERT INTO post (title, content) VALUES ('Test2', 'Test2 content');

-- Transaction 데이터 삽입
INSERT INTO app_transaction (coin, trade_price, trade_volume, ask_bid, trade_timestamp) VALUES ('KBW-BTC', 20000, 0.5, 'BID', 1672531200000);
INSERT INTO app_transaction (coin, trade_price, trade_volume, ask_bid, trade_timestamp) VALUES ('KBW-ETH', 15000, 1.0, 'ASK', 1672534800000);

-- AppUser 데이터 삽입
INSERT INTO app_user (email, username, password) VALUES ('test1@test.com', 'test1', 'pass1');
INSERT INTO app_user (email, username, password) VALUES ('test2@test.com', 'test2', 'pass2');

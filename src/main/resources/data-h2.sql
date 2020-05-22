INSERT INTO sys_transaction_data (transaction_id, operation_type, quantity, security_code, trade_id, trade_status, version) VALUES(nextval('seq_sys_transaction_id'), 'INSERT', 20, 'REA', 1, 'BUY', 1);
INSERT INTO sys_transaction_data (transaction_id, operation_type, quantity, security_code, trade_id, trade_status, version) VALUES(nextval('seq_sys_transaction_id'), 'INSERT', 20, 'REL', 2, 'BUY', 1);
INSERT INTO sys_transaction_data (transaction_id, operation_type, quantity, security_code, trade_id, trade_status, version) VALUES(nextval('seq_sys_transaction_id'), 'UPDATE', 30, 'REL', 1, 'SELL', 2);
INSERT INTO sys_transaction_data (transaction_id, operation_type, quantity, security_code, trade_id, trade_status, version) VALUES(nextval('seq_sys_transaction_id'), 'UPDATE', 30, 'REL', 1, 'BUY', 3);
INSERT INTO sys_transaction_data (transaction_id, operation_type, quantity, security_code, trade_id, trade_status, version) VALUES(nextval('seq_sys_transaction_id'), 'CANCEL', 30, 'REL', 1, 'BUY', 4);

INSERT INTO sys_position_data (security_code, position_value) VALUES('REA', 0);
INSERT INTO sys_position_data (security_code, position_value) VALUES('REL', 20);

INSERT INTO sys_trade_sequence (trade_id) VALUES(nextval('seq_sys_trade_id'));
INSERT INTO sys_trade_sequence (trade_id) VALUES(nextval('seq_sys_trade_id'));
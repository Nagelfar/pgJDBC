CREATE TABLE IF NOT EXISTS test_table
(
    required_int    INT,
    optional_int    INT     NULL,
    required_string varchar,
    optional_string varchar NULL,
    required_uuid   uuid,
    optional_uuid   uuid    NULL
);


INSERT INTO test_table
VALUES (1, 1, 'first', 'first', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001'),
       (2, null, 'second', NULL, '00000000-0000-0000-0000-000000000002', NULL);
UPDATE samply.configuration SET setting = {siteid} WHERE name = 'ID_MANAGER_NETWORK_ID';
UPDATE samply.configuration SET setting = {siteid} WHERE name = 'QUALITY_REPORT_LOCATION';
UPDATE samply.configuration SET setting = {mds-path} WHERE name = 'CENTRAL_MDS_DATABASE_PATH';
UPDATE samply.configuration SET setting = {mds-url} WHERE name = 'CENTRAL_MDS_DATABASE_BASE_URL';
INSERT INTO the_table (id, column_1, column_2)
VALUES (1, 'A', 'X'), (2, 'B', 'Y'), (3, 'C', 'Z')
    ON CONFLICT (id) DO UPDATE
                            SET column_1 = excluded.column_1,
                            column_2 = excluded.column_2;

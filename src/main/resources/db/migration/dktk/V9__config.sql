INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_GENERAL_REHEARSAL_PRIORITATION_FILE', 'general_rehearsal_priorization.csv', false);
INSERT INTO samply.configuration(name, setting, visible) values('EXPORT_MDR_BLACKLIST', 'urn:dktk:dataelement:43:*;urn:dktk:dataelement:45:*;urn:dktk:dataelement:46:*;urn:dktk:dataelement:54:*;urn:dktk:dataelement:84:*;urn:dktk:dataelement:47:*;urn:dktk:dataelement:41:*;urn:dktk:dataelement:71:* ', false);
INSERT INTO samply.configuration(name, setting, visible) values('ID_CONNECTOR_SOCKET_TIMEOUT', '180000', false);
INSERT INTO samply.configuration(name, setting, visible) values('ID_CONNECTOR_CONNECT_TIMEOUT', '180000', false);
INSERT INTO samply.configuration(name, setting, visible) values('ID_CONNECTOR_CONNECTION_REQUEST_TIMEOUT', '180000', false);
INSERT INTO samply.configuration(name, setting, visible) values('ID_CONNECTOR_MAX_NUMBER_OF_CONNECTION_ATTEMPTS', '10', false);
INSERT INTO samply.configuration(name, setting, visible) values('ID_CONNECTOR_TIME_TO_WAIT_IN_SECONDS_BETWEEN_CONNECTION_ATTEMPTS', '60', false);
INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_TIMEOUT_IN_MINUTES', '1440', false);

INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_SHOW_INFO_SHEET', 'true', false);
INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_SHOW_FILTERED_ELEMENTS_SHEET', 'true', false);
INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_SHOW_ALL_ELEMENTS_SHEET', 'true', false);
INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_SHOW_PATIENT_IDS_SHEET', 'true', false);
INSERT INTO samply.configuration(name, setting, visible) values('QUALITY_REPORT_SHOW_STATISTICS_SHEET', 'true', false);


UPDATE samply.configuration SET setting='' WHERE name='QUALITY_REPORT_IGNORED_DATAELEMENTS';
UPDATE samply.job_schedule SET cron_expression='0/50 * * * * ?'  WHERE job_key='DecentralSearchGroup.CollectInquiriesJob';
UPDATE samply.job_schedule SET paused = false WHERE job_key='MaintenanceGroup.ReportToMonitoringJob';
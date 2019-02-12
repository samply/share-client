INSERT INTO requested_entity(name)
SELECT unnest(enum_range(NULL ::entity_type));

INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_BASE_URL', 'https://centralsearch-test.ccpit.dktk.dkfz.de');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_PATH', '');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_AUTOMATIC_UPLOAD', 'false');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_AUTOMATIC_UPLOAD_AT', '03:00');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_UPLOAD_PATIENTS_WITH_LOCAL_CONSENT', 'false');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_UPLOAD_RANDOMIZE_EXPORT_IDS', 'false');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_DELETE_BEFORE_UPLOAD', 'false');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_NEXT_UPLOAD_FULL', 'false');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_SHOW_UPLOAD_PATIENTS_WITH_LOCAL_CONSENT', '');
INSERT INTO samply.configuration(name, setting)
VALUES ('DECENTRAL_SEARCH_MAIL_RECEIVER_ADDRESS', '');
INSERT INTO samply.configuration(name, setting)
VALUES ('DECENTRAL_SEARCH_MAIL_SHARE_URL', '');
INSERT INTO samply.configuration(name, setting)
VALUES ('MDR_URL', 'https://mdr.ccp-it.dktk.dkfz.de/v3/api/mdr');
INSERT INTO samply.configuration(name, setting)
VALUES ('LDM_URL', 'http://localhost:8080/centraxx/');
INSERT INTO samply.configuration(name, setting)
VALUES ('ID_MANAGER_URL', 'http://localhost:8180/ID-Manager');
INSERT INTO samply.configuration(name, setting)
VALUES ('ID_MANAGER_NETWORK_ID', 'DKTK');
INSERT INTO samply.configuration(name, setting)
VALUES ('ID_MANAGER_INSTANCE_ID', '');
INSERT INTO samply.configuration(name, setting)
VALUES ('CENTRAL_MDS_DATABASE_ANONYMIZED_PATIENTS_PREFIX', 'TESTANO_');
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_GRP_MDSK', 'urn:dktk:dataelementgroup:7:latest', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_GRP_MDSB', 'urn:dktk:dataelementgroup:8:latest', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_UPLOAD_FROM', 'urn:dktk:dataelement:64:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_UPLOAD_TO', 'urn:dktk:dataelement:63:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_CONSENT_DKTK', 'urn:dktk:dataelement:62:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_DKTK_GLOBAL_ID', 'urn:dktk:dataelement:54:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_GENDER', 'urn:dktk:dataelement:1:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_AGE_AT_DIAGNOSIS', 'urn:dktk:dataelement:28:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_CASE_DATE', 'urn:dktk:dataelement:27:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_CENTRAXX_MAPPING_VERSION', 'urn:dktk:dataelement:108:', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('MDR_KEY_CENTRAXX_MAPPING_DATE', 'urn:dktk:dataelement:109:', false);

-- Quality Report Parameters
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_NAMESPACE', 'dktk', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_AUTH_USER_ID', '20', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_AUTH_KEY_ID', '2', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_AUTH_URL', 'https://auth.ccp-it.dktk.dkfz.de', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_AUTH_PRIVATE_KEY_BASE_64',
        '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',
        false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_MAX_ATTEMPTS', '54', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_MAX_TIME_TO_WAIT_IN_MILLIS', '300000', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_ONLY_STATISTICS_AVAILABLE', 'true', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_MDR_LINK_PREFIX', 'https://mdr.ccp-it.dktk.dkfz.de/detail.xhtml?urn=', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_MAX_NUMBER_OF_PATIENT_IDS_TO_BE_SHOWN', '200', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_CENTRAXX_DATAELEMENTS_FILE', 'centraxx.dataelements.txt', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_CENTRAXX_ATTRIBUTES_FILE', 'centraxx.attributes.txt', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_IGNORED_DATAELEMENTS',
        'urn:dktk:dataelement:41:3, urn:dktk:dataelement:47:3, urn:dktk:dataelement:71:2, urn:dktk:dataelement:84:2, urn:dktk:dataelement:20:3',
        false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_DIRECTORY', 'C:\ProgramData\dktk\reports', true);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_LOCATION', 'LocalDev', true);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_BASIC_FILENAME', 'quality-report', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_SCHEDULER_FORMAT', 'by-year', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_SCHEDULER_YEARS', '20', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_GROUP_MODUL', '5', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_SCHEDULER_BY_YEAR', '', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_SCHEDULER_BY_MONTH', '', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_STATISTICS_FILENAME', 'quality-report-statistics.txt', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_EXCEL_INFO_FILENAME', 'quality-report-info.xlsx', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_EXCEL_INFO_URL',
        'https://deployment.ccp-it.dktk.dkfz.de/static/qualityreport/quality-report-info.xlsx', false);
INSERT INTO samply.configuration(name, setting, visible)
values ('QUALITY_REPORT_LANGUAGE_CODE', 'de', false);

-- Configuration for Report to Monitoring
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('MONITORING_REPORT_COUNT_TOTAL', 'false', true);
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('MONITORING_REPORT_COUNT_DKTKFLAG', 'false', true);
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('MONITORING_REPORT_COUNT_REFERENCEQUERY', 'false', true);
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('MONITORING_REPORT_TIME_REFERENCEQUERY', 'false', true);
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('MONITORING_REPORT_CENTRAXX_MAPPING_INFORMATION', 'false', true);

-- Configuration for Job Parameters
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_COLLECT_INQUIRIES_INTERVAL_SECONDS', 5, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS', 5, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_INTERVAL_SECONDS', 10, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_ATTEMPTS', 6, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS', 60, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS', 10, false);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('JOB_MOVE_INQUIRIES_TO_ARCHIVE_AFTER_DAYS', 28, true);

-- Configuration for Upload
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('UPLOAD_RETRY_PATIENT_UPLOAD_ATTEMPTS', 6, true);
INSERT INTO samply.configuration_timings(name, setting, visible)
VALUES ('UPLOAD_RETRY_PATIENT_UPLOAD_INTERVAL', 10, true);

-- Configuration for Caching on LDM connection
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('LDM_CACHING_ENABLED', 'false', true);
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('LDM_CACHING_MAX_SIZE', '1000', true);

-- Configuration for misc entries
INSERT INTO samply.configuration(name, setting, visible)
VALUES ('DECENTRAL_SEARCH_MAIL_INCLUDE_EMPTY_RESULTS', 'true', true);

-- Set the cron expressions for some of the jobs
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('DecentralSearchGroup.CollectInquiriesJob', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('DecentralSearchGroup.ExecuteInquiriesJob', '0/5 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('DecentralSearchGroup.SendNotificationsJob', '0 0 * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('CentralSearchGroup.UploadToCentralMdsDbJobDktkFlag', '0 0 23 1/1 * ? *', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('CentralSearchGroup.UploadToCentralMdsDbJobNoDktkFlag', '0 0 2 ? 1/1 SAT#1 *', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('MaintenanceGroup.CheckLocalComponentsJob', '0 0 0/1 1/1 * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('MaintenanceGroup.DbCleanupJob', '0 30 0 1/1 * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused)
VALUES ('MaintenanceGroup.ReportToMonitoringJob', '0 0 1 1/1 * ? *', true);

-- Add additional keys for the viewfields when posting to local datamanagement
INSERT INTO samply.configuration(name, setting, visible)
values ('INQUIRY_ADDITIONAL_MDRKEYS',
        'urn:adt:dataelement:77:*;urn:adt:dataelement:78:*;urn:adt:dataelement:90:*;urn:adt:dataelement:93:*;urn:adt:dataelement:89:*;urn:adt:dataelement:91:*',
        false);

with
tmp_token
as
(
INSERT
INTO
Token
(
apikey
)
VALUES
(
'ZvDKWpgCXSeed23MfbHs'
)
RETURNING
id
)
INSERT
INTO
samply
.
user
(
username,
password_hash,
token_id,
admin_privilege
)
VALUES
(
'admin',
'$2a$10$nMAQW9ZUf8evd2KUU4Op.eWHK18hkiBDKYxpWnp6oxxQY8yApK4CS',
(
SELECT
id
from
tmp_token
),
true
);
-- adminpass
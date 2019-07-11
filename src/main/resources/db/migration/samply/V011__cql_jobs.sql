-- noinspection SqlNoDataSourceInspectionForFile

-- Set the cron expressions for some of the jobs
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.CollectInquiriesJobCentraxx', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.CollectInquiriesJobCql', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.CollectInquiriesJobView', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.ExecuteInquiriesJobCentraxx', '0/5 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.ExecuteInquiriesJobCql', '0/5 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DecentralSearchGroup.ExecuteInquiriesJobView', '0/5 * * * * ?', false);

DELETE FROM samply.job_schedule WHERE job_key = 'DecentralSearchGroup.CollectInquiriesJob';
DELETE FROM samply.job_schedule WHERE job_key = 'DecentralSearchGroup.ExecuteInquiriesJob';

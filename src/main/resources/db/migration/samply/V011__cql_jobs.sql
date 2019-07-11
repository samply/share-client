-- noinspection SqlNoDataSourceInspectionForFile

-- Set the cron expressions for some of the jobs
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.CheckInquiryStatusJobCentraxx', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.CheckInquiryStatusJobSamplystoreBiobanks', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.CheckInquiryStatusJobCql', '0/10 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.ExecuteInquiryJobCentraxx', '0/5 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.ExecuteInquiryJobSamplystoreBiobanks', '0/5 * * * * ?', false);
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('InquiryGroup.ExecuteInquiryJobCql', '0/5 * * * * ?', false);

DELETE FROM samply.job_schedule WHERE job_key = 'InquiryGroup.CheckInquiryStatusJob';
DELETE FROM samply.job_schedule WHERE job_key = 'InquiryGroup.ExecuteInquiryJob';

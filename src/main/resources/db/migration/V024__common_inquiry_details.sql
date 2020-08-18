ALTER TABLE samply.inquiry_details ADD COLUMN scheduled_at timestamp;
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('MaintenanceGroup.CheckScheduledInquiriesJob', '0 0 3 ? * * *', false);
INSERT INTO samply.configuration_timings(name, setting, visible) VALUES ('JOB_CHECK_SCHEDULED_INQUIRY_INTERVAL_MINUTES', '5', true);

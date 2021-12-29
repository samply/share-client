INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('CentralSearchGroup.ReportToMonitoringJobLongFrequence', '0 0 3 ? * * *', false);
UPDATE samply.job_schedule SET job_key ='MaintenanceGroup.ReportToMonitoringJobShortFrequence' where job_key = 'MaintenanceGroup.ReportToMonitoringJob';

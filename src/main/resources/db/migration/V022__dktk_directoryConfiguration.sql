INSERT INTO samply.configuration(name,setting,visible) VALUES ('DIRECTORY_URL','http://localhost:9999','true');
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DirectoryGroup.DirectorySyncJob', '0 0 3 ? * * *', false);
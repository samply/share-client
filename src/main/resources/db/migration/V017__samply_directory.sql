INSERT INTO samply.configuration(name,setting,visible) VALUES ('DIRECTORY_URL','http://localhost:9999','true');
INSERT INTO pg_enum (enumtypid, enumlabel, enumsortorder)
    SELECT 'target_type'::regtype::oid, 'TT_DIRECTORY', ( SELECT MAX(enumsortorder) + 1 FROM pg_enum WHERE enumtypid = 'target_type'::regtype );
INSERT INTO samply.job_schedule(job_key, cron_expression, paused) VALUES ('DirectoryGroup.DirectorySyncJob', '0 0 3 ? * * *', false);
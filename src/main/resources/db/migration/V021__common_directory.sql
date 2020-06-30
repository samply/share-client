--INSERT INTO pg_enum (enumtypid, enumlabel, enumsortorder)
--    SELECT 'target_type'::regtype::oid, 'TT_DIRECTORY', ( SELECT MAX(enumsortorder) + 1 FROM pg_enum WHERE enumtypid = 'target_type'::regtype );
ALTER TYPE target_type ADD VALUE 'TT_DIRECTORY';
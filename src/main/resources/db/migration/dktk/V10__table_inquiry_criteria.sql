CREATE TYPE inquiry_criteria_status_type AS ENUM (
	'IS_NEW',
	'IS_PROCESSING',
	'IS_READY',
	'IS_LDM_ERROR',
	'IS_ABANDONED'
);

CREATE TYPE query_language_type AS ENUM (
	'QUERY',
	'CQL'
);

CREATE TABLE inquiry_criteria (
	id                   SERIAL PRIMARY KEY,
	details_id           integer  NOT NULL,
	status               inquiry_criteria_status_type  NOT NULL,
	criteria_original    text  NOT NULL,
	criteria_modified    text,
	query_language       query_language_type   NOT NULL,
	entity_type          text
 );

CREATE INDEX idx_inquiry_criteria ON inquiry_criteria ( details_id );

ALTER TABLE inquiry_details DROP COLUMN criteria_original;
ALTER TABLE inquiry_details DROP COLUMN criteria_modified;

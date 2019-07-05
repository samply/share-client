----------------------------------------
-- CREATE TYPES
----------------------------------------
CREATE TYPE inquiry_criteria_status_type AS ENUM (
	'ICS_NEW',
	'ICS_PROCESSING',
	'ICS_READY',
	'ICS_LDM_ERROR',
	'ICS_ABANDONED',
	'ICS_UNKNOWN'
);

CREATE TYPE query_language_type AS ENUM (
	'QUERY',
	'CQL'
);

----------------------------------------
-- CREATE TABLES + INDEX
----------------------------------------
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

--------------------------------------------------------
-- MOVE COLUMNS from InquiryDetails to InquiryCriteria
--------------------------------------------------------
INSERT INTO samply.inquiry_criteria (details_id,
                                     status,
                                     criteria_original,
                                     criteria_modified,
                                     query_language,
                                     entity_type)
    (SELECT details.id,
            -- Translate samply.inquiry_status_type to samply.inquiry_criteria_status_type
            -- Order of values in types must agree
            (SELECT (enum_range(null :: samply.inquiry_criteria_status_type))) [
            array_position(enum_range(null :: samply.inquiry_status_type), details.status) ],
            details.criteria_original,
            details.criteria_modified,
            (SELECT (enum_range(null :: samply.query_language_type))) [ 1 ],
            'Patient + Donor'
     FROM samply.inquiry_details AS details);

ALTER TABLE inquiry_details DROP COLUMN criteria_original;
ALTER TABLE inquiry_details DROP COLUMN criteria_modified;

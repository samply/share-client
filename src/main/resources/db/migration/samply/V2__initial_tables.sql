-- noinspection SqlNoDataSourceInspectionForFile

CREATE TABLE configuration
(
  name    text PRIMARY KEY,
  setting text,
  visible boolean DEFAULT true NOT NULL
);

CREATE TABLE configuration_timings
(
  name    text PRIMARY KEY,
  setting integer DEFAULT 0    NOT NULL,
  visible boolean DEFAULT true NOT NULL
);

CREATE TABLE credentials
(
  id          SERIAL PRIMARY KEY,
  auth_scheme auth_scheme_type,
  target      target_type NOT NULL,
  username    text,
  passcode    text        NOT NULL,
  workstation text,
  "domain"    text
);

CREATE TABLE requested_entity
(
  id   SERIAL PRIMARY KEY,
  name entity_type UNIQUE NOT NULL
);

CREATE TABLE token
(
  id           SERIAL PRIMARY KEY,
  apikey       text UNIQUE NOT NULL,
  signin_token text,
  expires_at   timestamp
);

CREATE TABLE upload
(
  id              SERIAL PRIMARY KEY,
  status          upload_status_type DEFAULT 'US_NEW'          NOT NULL,
  triggered_at    TIMESTAMP          DEFAULT current_timestamp NOT NULL,
  triggered_by    text               DEFAULT 'scheduled'       NOT NULL,
  is_dryrun       boolean            DEFAULT FALSE,
  is_full_upload  boolean            DEFAULT FALSE,
  dktk_flagged    boolean,
  success_count   integer,
  failure_count   integer,
  failed_patients text,
  time_to_set     TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE "user"
(
  id              SERIAL PRIMARY KEY,
  token_id        integer UNIQUE,
  username        text UNIQUE           NOT NULL,
  password_hash   text                  NOT NULL,
  real_name       text,
  email           text,
  admin_privilege boolean DEFAULT FALSE NOT NULL
);

CREATE
INDEX
idx_user
ON
"user"
(
token_id
);

CREATE TABLE user_notification
(
  user_id             integer NOT NULL,
  requested_entity_id integer NOT NULL,
  PRIMARY KEY ("user_id", "requested_entity_id")
);

CREATE TABLE broker
(
  id             SERIAL PRIMARY KEY,
  address        text NOT NULL,
  name           text,
  last_checked   timestamp,
  credentials_id integer,
  status         broker_status_type
);

CREATE
INDEX
idx_broker
ON
broker
(
credentials_id
);

CREATE TABLE inquiry_handling_rule
(
  id              SERIAL PRIMARY KEY,
  broker_id       integer,
  full_result     boolean         DEFAULT true                     NOT NULL,
  automatic_reply reply_rule_type DEFAULT 'RR_NO_AUTOMATIC_ACTION' NOT NULL
);

CREATE
INDEX
idx_inquiry_handling_rule
ON
inquiry_handling_rule
(
broker_id
);

CREATE TABLE event_log
(
  id                SERIAL PRIMARY KEY,
  event_type        event_message_type,
  inquiry_id        integer,
  upload_id         integer,
  user_id           integer,
  quality_report_id integer,
  event_time        timestamp DEFAULT current_timestamp NOT NULL,
  show_in_global    boolean   DEFAULT true              NOT NULL,
  entry             text
);

CREATE
INDEX
idx_event_log
ON
event_log
(
inquiry_id
);
CREATE
INDEX
idx_event_log_0
ON
event_log
(
upload_id
);
CREATE
INDEX
idx_event_log_1
ON
event_log
(
user_id
);

CREATE TABLE inquiry
(
  id                SERIAL PRIMARY KEY,
  upload_id         integer,
  source_id         integer NOT NULL,
  label             text,
  description       text,
  broker_id         integer,
  latest_details_id integer,
  archived_at       timestamp,
  deleted_at        timestamp
);

CREATE
INDEX
idx_inquiry
ON
inquiry
(
broker_id
);
CREATE
INDEX
idx_inquiry_0
ON
inquiry
(
latest_details_id
);

CREATE TABLE inquiry_answer
(
  id                 SERIAL PRIMARY KEY,
  inquiry_details_id integer UNIQUE                      NOT NULL,
  sent_at            timestamp DEFAULT current_timestamp NOT NULL,
  content            text                                NOT NULL
);

CREATE
INDEX
idx_inquiry_answer
ON
inquiry_answer
(
inquiry_details_id
);

CREATE TABLE inquiry_details
(
  id                SERIAL PRIMARY KEY,
  inquiry_id        integer                             NOT NULL,
  contact_id        integer,
  revision          integer                             NOT NULL,
  received_at       timestamp DEFAULT current_timestamp NOT NULL,
  status            inquiry_status_type                 NOT NULL,
  criteria_original text                                NOT NULL,
  criteria_modified text,
  expose_location   text
);

CREATE
INDEX
idx_inquiry_details
ON
inquiry_details
(
inquiry_id
);

CREATE TABLE inquiry_requested_entity
(
  inquiry_id          integer NOT NULL,
  requested_entity_id integer NOT NULL,
  PRIMARY KEY ("inquiry_id", "requested_entity_id")
);

CREATE TABLE inquiry_result
(
  id                 SERIAL PRIMARY KEY,
  inquiry_details_id integer                             NOT NULL,
  is_error           boolean   DEFAULT false             NOT NULL,
  "location"         text,
  "size"             integer,
  executed_at        timestamp DEFAULT current_timestamp NOT NULL,
  valid_until        timestamp,
  statistics_only    boolean   DEFAULT false             NOT NULL,
  notification_sent  boolean   DEFAULT false             NOT NULL,
  "error_code"       text
);

CREATE
INDEX
idx_inquiry_result
ON
inquiry_result
(
inquiry_details_id
);

CREATE TABLE inquiry_result_stats
(
  id                SERIAL PRIMARY KEY,
  inquiry_result_id INTEGER UNIQUE NOT NULL,
  stats_gender      TEXT,
  stats_age         TEXT
);

CREATE
INDEX
idx_inquiry_result_stats
ON
inquiry_result_stats
(
inquiry_result_id
);

CREATE TABLE user_seen_inquiry
(
  user_id    integer NOT NULL,
  inquiry_id integer NOT NULL,
  PRIMARY KEY ("user_id", "inquiry_id")
);

CREATE TABLE contact
(
  id                SERIAL PRIMARY KEY,
  title             text,
  first_name        text,
  last_name         text NOT NULL,
  phone             text,
  email             text,
  organization_name text
);

CREATE TABLE job_schedule
(
  id              SERIAL PRIMARY KEY,
  job_key         text UNIQUE NOT NULL,
  cron_expression text        NOT NULL,
  paused          boolean
);

CREATE TABLE document
(
  id          SERIAL PRIMARY KEY,
  inquiry_id  INTEGER NOT NULL,
  user_id     INTEGER NOT NULL,
  uploaded_at date    NOT NULL DEFAULT CURRENT_DATE,
  filetype    TEXT,
  filename    TEXT,
  data        BYTEA
);

CREATE
INDEX
idx_document
ON
document
(
inquiry_id
);


ALTER TABLE broker
  ADD CONSTRAINT broker_credentials_id_fkey FOREIGN KEY (credentials_id) REFERENCES credentials (id) ON DELETE SET NULL;
ALTER TABLE event_log
  ADD CONSTRAINT event_log_inquiry_id_fkey FOREIGN KEY (inquiry_id) REFERENCES inquiry (id) ON DELETE CASCADE;
ALTER TABLE event_log
  ADD CONSTRAINT event_log_upload_id_fkey FOREIGN KEY (upload_id) REFERENCES upload (id) ON DELETE CASCADE;
ALTER TABLE event_log
  ADD CONSTRAINT event_log_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;
ALTER TABLE inquiry
  ADD CONSTRAINT inquiry_upload_id_fkey FOREIGN KEY (upload_id) REFERENCES upload (id) ON DELETE CASCADE;
ALTER TABLE inquiry
  ADD CONSTRAINT inquiry_broker_id_fkey FOREIGN KEY (broker_id) REFERENCES broker (id) ON DELETE CASCADE;
ALTER TABLE inquiry
  ADD CONSTRAINT inquiry_broker_xor_upload CHECK ((upload_id IS NULL AND broker_id IS NOT NULL) OR
                                                  (broker_id IS NULL AND upload_id IS NOT NULL));
ALTER TABLE inquiry
  ADD CONSTRAINT inquiry_latest_details_id_fkey FOREIGN KEY (latest_details_id) REFERENCES inquiry_details (id) ON DELETE SET NULL;
ALTER TABLE inquiry_answer
  ADD CONSTRAINT inquiry_answer_inquiry_details_id_fkey FOREIGN KEY (inquiry_details_id) REFERENCES inquiry_details (id) ON DELETE CASCADE;
ALTER TABLE inquiry_details
  ADD CONSTRAINT inquiry_details_inquiry_id_fkey FOREIGN KEY (inquiry_id) REFERENCES inquiry (id) ON DELETE CASCADE;
ALTER TABLE inquiry_details
  ADD CONSTRAINT inquiry_details_contact_id_fkey FOREIGN KEY (contact_id) REFERENCES contact (id) ON DELETE SET NULL;
ALTER TABLE inquiry_handling_rule
  ADD CONSTRAINT inquiry_handling_rule_broker_id_fkey FOREIGN KEY (broker_id) REFERENCES broker (id) ON DELETE CASCADE;
ALTER TABLE inquiry_requested_entity
  ADD CONSTRAINT inquiry_requested_entity_inquiry_id_fkey FOREIGN KEY (inquiry_id) REFERENCES inquiry (id) ON DELETE CASCADE;
ALTER TABLE inquiry_requested_entity
  ADD CONSTRAINT inquiry_requested_entity_requested_entity_id_fkey FOREIGN KEY (requested_entity_id) REFERENCES requested_entity (id) ON DELETE CASCADE;
ALTER TABLE inquiry_result
  ADD CONSTRAINT inquiry_result_inquiry_details_id_fkey FOREIGN KEY (inquiry_details_id) REFERENCES inquiry_details (id) ON DELETE CASCADE;
ALTER TABLE inquiry_result_stats
  ADD CONSTRAINT inquiry_result_stats_inquiry_result_id_fkey FOREIGN KEY (inquiry_result_id) REFERENCES inquiry_result (id) ON DELETE CASCADE;
ALTER TABLE "user"
  ADD CONSTRAINT user_token_id_fkey FOREIGN KEY (token_id) REFERENCES token (id) ON DELETE SET NULL;
ALTER TABLE user_notification
  ADD CONSTRAINT user_notification_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;
ALTER TABLE user_notification
  ADD CONSTRAINT user_notification_requested_entity_id_fkey FOREIGN KEY (requested_entity_id) REFERENCES requested_entity (id) ON DELETE CASCADE;
ALTER TABLE user_seen_inquiry
  ADD CONSTRAINT user_seen_inquiry_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE;
ALTER TABLE user_seen_inquiry
  ADD CONSTRAINT user_seen_inquiry_inquiry_id_fkey FOREIGN KEY (inquiry_id) REFERENCES inquiry (id) ON DELETE CASCADE;
ALTER TABLE document
  ADD CONSTRAINT document_inquiry_id_fkey FOREIGN KEY (inquiry_id) REFERENCES inquiry (id) ON DELETE CASCADE;


COMMENT
ON
COLUMN
configuration
.
visible
IS
'Is this visible on the web interface?';
COMMENT
ON
TABLE
configuration_timings
IS
'Use an extra table for timing parameters for the sake of simply using integers';
COMMENT
ON
TABLE
credentials
IS
'Stored credentials for central search,  searchbrokers, local datamanagement...';
COMMENT
ON
COLUMN
credentials
.
target
IS
'proxy? central search? broker? user?';
COMMENT
ON
COLUMN
credentials
.
passcode
IS
'May be a hashed password for the user login or a plain password for central search or maybe just an apikey';
COMMENT
ON
COLUMN
credentials
.
workstation
IS
'Currently not used. Might be relevant for other auth schemes';
COMMENT
ON
COLUMN
credentials
.
"domain"
IS
'Currently not used. Might be relevant for other auth schemes';
COMMENT
ON
TABLE
requested_entity
IS
'An inquirer can request different types of entities (biomaterial, clinical data...)';
COMMENT
ON
TABLE
upload
IS
'Uploads to central search.';
COMMENT
ON
COLUMN
upload
.
triggered_by
IS
'Who triggered the upload? Was it automatically by the scheduler, or did a user do it?';
COMMENT
ON
COLUMN
upload
.
success_count
IS
'How many upload datasets were acknowledged with a 2xx status code';
COMMENT
ON
COLUMN
upload
.
failure_count
IS
'How many upload datasets were finally denied';
COMMENT
ON
COLUMN
upload
.
failed_patients
IS
'A JSON array of DKTK site ids of the failed patients';
COMMENT
ON
COLUMN
upload
.
time_to_set
IS
'The timestamp that will be set at the central mds db at the end of the upload';
COMMENT
ON
COLUMN
"user"
.
token_id
IS
'The user might have a login token assigned to himself in order to allow logging in from other components (e.g. OSSE.EDC)';
COMMENT
ON
COLUMN
"user"
.
password_hash
IS
'bcrypt encoded password';
COMMENT
ON
COLUMN
"user"
.
real_name
IS
'The real name(s) of the user, may include title(s)';
COMMENT
ON
TABLE
user_notification
IS
'Which user shall receive which notifications?';
COMMENT
ON
COLUMN
broker
.
address
IS
'The base URL of the searchbroker';
COMMENT
ON
COLUMN
broker
.
name
IS
'A self-assigned name of the searchbroker (e.g. "Decentral Searchbroker for DKTK")';
COMMENT
ON
COLUMN
broker
.
last_checked
IS
'When was this searchbroker queries the last time?';
COMMENT
ON
TABLE
inquiry_handling_rule
IS
'Incoming inquiries may be handled differently, depending on several criteria. Allow to define an "if" part which has to be handled in the application, a link to a specific broker (may be null), a link to a certain result type (may also be null) as well as an action to take (e.g. instantly reply a number)';
COMMENT
ON
COLUMN
inquiry_handling_rule
.
full_result
IS
'Should the full result be generated here?';
COMMENT
ON
CONSTRAINT
inquiry_handling_rule_broker_id_fkey
ON
inquiry_handling_rule
IS
'May be linked to a certain broker (but does not have to)';
COMMENT
ON
TABLE
event_log
IS
'Log certain events that happen during job execution. E.g. Upload was triggered or inquiry was received/executed';
COMMENT
ON
COLUMN
event_log
.
event_time
IS
'when did the logged event occur?';
COMMENT
ON
COLUMN
event_log
.
entry
IS
'Either a message or a set of parameters. As a JSON String';
COMMENT
ON
CONSTRAINT
inquiry_broker_xor_upload
ON
inquiry
IS
'Uploads can also spawn inquiries. In this case, set the upload id. upload_id XOR broker_id have to be set';
COMMENT
ON
COLUMN
inquiry
.
archived_at
IS
'if this inquiry was archived...set the timestamp here. if it is not archived, this shall be null';
COMMENT
ON
COLUMN
inquiry
.
deleted_at
IS
'allow to mark an inquiry as deleted. set to null if it is not deleted';
COMMENT
ON
COLUMN
inquiry_answer
.
inquiry_details_id
IS
'which revision of the inquiry was answered?';
COMMENT
ON
COLUMN
inquiry_answer
.
content
IS
'What was sent? This may contain different types of data...use json to keep it generic';
COMMENT
ON
COLUMN
inquiry_details
.
criteria_original
IS
'the criteria xml snippet as received from source';
COMMENT
ON
COLUMN
inquiry_details
.
criteria_modified
IS
'if the original criteria had unknown keys which were removed...keep the modified one as well';
COMMENT
ON
COLUMN
inquiry_result
.
"location"
IS
'The URL where the result can be found';
COMMENT
ON
COLUMN
inquiry_result
.
notification_sent
IS
'has the user been notified about this inquiry?';
COMMENT
ON
TABLE
job_schedule
IS
'For regularly executed tasks, set the cron schedule expression here';
COMMENT
ON
TABLE
document
IS
'Users can upload (modified) export files in order to track what they may have sent to other people'
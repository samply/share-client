# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [7.2.1 - 2021-02-04]
## Fixed
- send pseudonymised bundle as JSON only
- renamed some methods

## [7.2.0 - 2021-02-03]
## Added
- added process 9b for CTS 
- added JSONPath
- encryption/decryption of FHIR references 
- read localIds from Mainzelliste

## [7.1.0 - 2021-02-02]
## Added
- Percentage Logger
- Java Doc
- Integrate projectpseudonymization and patientlist

## [7.0.0 - 2020-11-27]
- Github release
### Changed
- Samply parent 11.1.0
## Added
- Github Actions
- Google Code Style
### Bugfix
- fixed functional tests for BLAZE

## [6.13.0 - 2020-10-14]
### Changed
- changed locale default from de to en

## [6.12.0 - 2020-10-14]
### Added
- Added features.properties to Dockerfile

## [6.11.1 - 2020-10-13]
### Bugfix
- Fixed job config report

## [6.11.0 - 2020-09-21]
### Added
- Added open api

## [6.10.0 - 2020-09-14]
### Added
- Report inquiry count

## [6.9.0 - 2020-09-08]
### Added
- CheckScheduledInquiryJob

# [6.8.0 - 2020-09-07]
### Added
- Report job config
- Report job error message

## [6.7.0 - 2020-09-03]
### Added
- Added cts audit trail

## [6.6.0 - 2020-08-12]
### Changed
- Changed some UI representation

## [6.5.0 - 2020-08-10]
### Added
- Add tests inquiry for CQL

## [5.8.0 - 2020-05-26]
### Added
- Sync between directory and bridgehead

## [5.7.0 - 2020-04-24]
### Change
- Add stratification to InquiryResult
- Add stratifications to reply (for Searchbroker)
- Update several dependencies share-dto (4.8.0) share-common (3.4.0) common-ldmclient.cql (1.3.0) common-ldmclient.centraxx (5.4.0) common-ldmclient.samplystore-biobank (2.4.0)
### Bugfix
- Use QueryRTesultStatistic from common (not CentraXX)

## [5.6.4 - 2020-01-28]
### Change
- Adopt interval for collecting inquiries to 1 second
- Reduce log level of Utils class to disable proxy warnings

## [5.6.3 - 2020-01-10]
### Change
- Adopt interval for collecting inquiries to 10 seconds

## [5.6.2 - 2019-09-30]
### Bugfix
- Update share-dto (Switch back to former xml format)

## [5.6.1 - 2019-09-26]
### Bugfix
- Update share-dto (avoid using namespace)

## [5.6.0 - 2019-09-02]
### Changed
- Use correct entity type for CQL queries

## [5.5.0 - 2019-08-26]
### Changed
- Update dependencies
- cleaned up the config files

## [5.4.0 - 2019-07-10]
### Changed
- Unify flyway scripts for projects DKTK and Samply. 
CAUTION: Breaking change for Samply (not DKTK) and should have been a new major version

## [5.3.0 - 2019-07-05]
### Changed
- Introduce InquiryCriteria to allow 1-n relation for Inquiry to criteria

## [5.2.0 - 2019-07-04]
### Changed
- Introduce LdmQueryResult as return value in LdmClient-API (replacing Object as return value)

## [5.1.0 - 2019-06-24]
### Changed
- changed xsd form common_urls.xhtml
- changed startpage text

### Fixed
- stop query request if LDM is not available

## [5.0.0 - 2019-06-24]
### Changed
- Use HttpConnector without LDM-Client credentials when credentials are missing
- Moved functionality to superclass AbstractLdmConnector
- Cleaner code

## [4.0.0 - 2018-11-29]
### Added
- [Icinga](https://icinga.com/) to monitoring components
- [FHIR](https://www.hl7.org/fhir/) support
- [Docker](https://www.docker.com/) support
- MDR-Url config in common_url.xhtml

### Security
- secure all APIs with basic Auth

### Changed
- Anonymization implemented in connector by adding random number and rounding up number of donors and samples plus testing

## [3.0.0 - 2018-05-30]
### Changed
- samply parent 10.1 (Java 8 )
- Diverse Versionsupdates von Libraries (e.g. JQuery 1.12.4 => 3.3.1-1)
 
## [Unreleased 2.0.0 - 201y-mm-dd]
### Added
- [Quartz](http://www.quartz-scheduler.org) to schedule Jobs
- [Flyway](https://flywaydb.org/) for database setup and migration
- [Webjars](http://webjars.org)
- README.md
- CHANGELOG.md

### Changed
- Codebase re-written
- Database re-designed
- Use jsf2 [templates](http://docs.oracle.com/javaee/6/tutorial/doc/giqxp.html) and 
[composite components](http://docs.oracle.com/javaee/6/tutorial/doc/giqzr.html) consistently

### Deprecated
- n/a

### Removed
- [samply.common.upgrade](https://code.mitro.dkfz.de/projects/COM/repos/samply.common.upgrade/browse)
    due to the change to Flyway
- Job/Task management via custom [Future Tasks](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html)
- necessity for config file

### Fixed
- n/a

### Security
- n/a

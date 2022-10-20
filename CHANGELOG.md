# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [7.9.0 - 2022-10-19]
### Added
- Add Rest api for the XML specification of nNGM
- Delete methods of the nNGM option 2 from the FHIR api

## [7.8.0 - 2022-08-08]
### Added
- create sublist for cql results
- changed NumberDisguiser to LaplaceMechanism
- init resources for directory sync

## [7.7.2 - 2022-07-27]
### Fixed
- Translate German remaining sentences for English version of main site

## [7.7.1 - 2022-06-15]
### Fixed
- Check Pseudonymisation GUI
- increased Directory Sync version
- Json-Smart 1.3.3

## [7.7.0 - 2022-05-19]
### Added
- Send ldm certificate date to monitoring 
- Configuration site: Autosave
- Update Button CollectInquiries in Inquiries List
 
### Fixed
- Missing icons
- Jsonproperty in UploadStats

## [7.6.2 - 2022-04-11]
### Added
- Use log4j implementation of slf4j-api (exclude all slf4j-simple dependencies)
- Solving the problem with the nNGM POST request
- Adaptations to the new MS-nNGM profiles
- Removal of the composition profiles
- Test HttpConnector Http Clients (Jersey 3 und Apache)
- Allow false value configuration for bypass proxy for private networks property


## [7.6.1 - 2022-02-28]
### Fixed
- Add Jersey 1 libraries for JSF 2

## [7.6.0 - 2022-02-25]
### Changed
- Use api key for the communication with cts instead of username and password

## [7.5.3 - 2022-01-31]
### Fixed
- Bug at the Searchbroker registration when typing two times unreachable urls
- Repair proxy for jersey 3

## [7.5.2 - 2022-01-24]
### Fixed
- Read log4j2.xml config

## [7.5.1 - 2022-01-20]
### Changed
- Quality report scheduler format simplified
### Added
- New value for quality report scheduler format: All

## [7.5.0 - 2022-01-12]
### Security
- Jersey 3
- Frontend Updates
- Jackson for serialization and deserialization

## [7.4.6 - 2022-01-03]
### Security
- log4j 2.17.1

## [7.4.5 - 2021-12-23]
### Security
- switch from log4j to slf4j
- logged many Exceptions
- removed log4j Configurator

## [7.4.4 - 2021-12-20]
### Security
- log4j version 2.17.0

## [7.4.3 - 2021-12-16]
### Security
- log4j version 2.16.0

## [7.4.2 - 2021-12-13]
### Changed
- hapi version 5.6.1
### Fixed
- no class def error found for jacksonStructure

## [7.4.1 - 2021-10-18]
### Fixed
- Quality Report for large amount of data
- Log4j version to 2.15.0

## [7.4.0 - 2021-09-13]
### Added
- New frequence for ReportToMonitoringJob: long frequence, only once a day
- Redirect url for the negotiator
### Changed
- Share-dto version 5.1.0
### Fixed
- Referencequerry: Executiontime in dktk

## [7.3.3 - 2021-06-19]
### Changed
- Quality Report Info Sheet: CCP-Office as contact
### Fixed
- Fixed statistics diagrams in inquery view
- enhanced export button in inquery view


## [7.3.2 - 2021-06-24]
### Added
- Workbook Window configurable in config database
- Export in Thread
- converter.dktk.patient 2.2.2


## [7.3.1 - 2021-06-18]
### Fixed
- Add timestamp to last export filename (correct table)

## [7.3.0 - 2021-06-02]
### Added
- set site name when registering at the searchbroker
- delete searchbroker registration in the searchbroker database
- feature property to activate or deactivate "set site name" feature
- CentraXX Job logs
- Apache POI 5.0.0
- Migrate Percentage Logger to share-common
- Add timestamp to last export filename

### Changed
- share-common version 4.1.0
- parent pom 11.1.1
- improve detection of FHIR ids that have to be encrypted 
- make nNGM encryption key configurable

### Fixed
- Bug 1: Check connection to the broker(s): Retrieve and execute test inquiry
- Bug 2: Check connection to the broker(s): Check reachability
- Bug 3: Check connection to the ID-Manager: Retrieve export ID
- Bug 4: InquiryBean.loadSelectedInquiry() handle if inquiryCriteria is null
- Code Style Fix

## [7.2.5 - 2021-05-14]
## Changed
- Updated swagger-ui plugin


## [7.2.4 - 2021-02-25]
### Fixed
- share-common 4.0.1 fix for icinga

## [7.2.3 - 2021-02-12]
### Fixed
- load last inquiry result

## [7.2.2 - 2021-02-10]
### Fixed
- read EncId instead of returning jsonString
- check the headers jsonpaths and target-url before using it

## [7.2.1 - 2021-02-04]
### Added
- github actions for automatic docker image builds in different configuration (currently samply, c4 and dktk)
- Environment Variables for the configuration file [_cts_info.xml](./src/docker/_cts_info.xml).
- Environment Variables for the new patientlistUrl and projectPseudonymisationUrl. [see this file](./src/docker/samply_common_urls.xml)
###  Changed
- Removed hard coded urls from [samply_common_urls](./src/docker/samply_common_urls.xml)
- Moved JMX Exporter and start.sh to "/docker" in the container
- Base image of the container from 8.5.32-jre8-alpine to 9-jdk8-openjdk-slim
### Fixed
- send pseudonymised bundle as JSON only
- renamed some methods
- Automatic generation of java classes from xsd files

## [7.2.0 - 2021-02-03]
### Added
- added process 9b for CTS 
- added JSONPath
- encryption/decryption of FHIR references 
- read localIds from Mainzelliste

## [7.1.0 - 2021-02-02]
### Added
- Percentage Logger
- Java Doc
- Integrate projectpseudonymization and patientlist

## [7.0.0 - 2020-11-27]
- Github release
### Changed
- Samply parent 11.1.0
### Added
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

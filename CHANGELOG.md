# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [5.3.0 - 2019-09-02]
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
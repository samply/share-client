# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [5.0.0. - 2019-06-24]
### Changed
- Use HttpConnector without LDM-Client credentials when credentials are missing
- Moved functionality to superclass AbstractLdmConnector
- Cleaner code

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
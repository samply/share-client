-- noinspection SqlNoDataSourceInspectionForFile
This is a dummy script and is used only for convenience and to explane the flyway folder structure.

To add a new flyway script copy this file and rename it according to one of the following conventions (case-sensitive)

  Vxyz__table_... .sql      <-- Changes to tables, types, ... everything that affects the generated sourcecode

                                DO NOT ADD any changes that affecta the SOURCE CODE GENERATION to one of the following files
  Vxyz__dktk_... .sql       <-- DKTK configurations
  Vxyz__samply_... .sql     <-- SAMPLY configurations
  Vxyz__common_... .sql     <-- Configurations common to all projects

All flyway-scripts with pattern

    *__table*.*
    *__common*.*

and depending on the project (DKTK or Samply)

    *__dktk*.*    OR   *__samply*.*

are copied to the flyway migration folder (independent of project)

    target/classes/db/migration_generated

This folder is used for the actual migration by flyway.
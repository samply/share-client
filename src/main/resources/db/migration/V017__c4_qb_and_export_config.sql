UPDATE samply.configuration SET setting='urn:adt:dataelementgroup:17:latest;urn:dktk:dataelementgroup:8:latest' WHERE name='QUALITY_REPORT_MDR_GROUPS';
UPDATE samply.configuration SET setting='' WHERE name='QUALITY_REPORT_ADDITIONAL_MDR_DATA_ELEMENTS';
UPDATE samply.configuration SET setting='adt, dktk' WHERE name='QUALITY_REPORT_MDR_NAMESPACE';
UPDATE samply.configuration SET setting='' WHERE name='QUALITY_REPORT_IGNORED_DATAELEMENTS';
UPDATE samply.configuration SET setting='urn:adt:dataelementgroup:17:latest;urn:dktk:dataelementgroup:8:latest' WHERE name='MDR_SOURCE_GROUPS';
UPDATE samply.configuration SET setting='' WHERE name='INQUIRY_ADDITIONAL_MDRKEYS';
UPDATE samply.configuration SET setting='urn:adt:dataelementgroup:17:latest' WHERE name='MDR_GRP_MDSK';
UPDATE samply.configuration SET setting='urn:dktk:dataelementgroup:8:latest' WHERE name='MDR_GRP_MDSB';
UPDATE samply.configuration SET setting='' WHERE name='EXPORT_MDR_BLACKLIST';
UPDATE samply.configuration SET setting='/etc/dktk/reports' WHERE name='QUALITY_REPORT_DIRECTORY';
UPDATE samply.configuration SET setting='' WHERE name='LDM_URL_BASE';


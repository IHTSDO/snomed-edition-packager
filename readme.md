SNOMED Edition Packager
======================

An Open Source Toolkit to package any extension as an edition

This command line tool will allow the creation of an edition from an extension plus the international edition. Provide the packages for repackaging - Edition validation - simple, in the software - merge full files, and use this to generate the snapshot.


Development Environment
-----------------------
Build the project using maven:

`mvn clean install`

Start the application using the standalone executable jar which includes an embedded tomcat:

`java -jar target/snomed-edition-packager-executable.jar`

After the application starts up, run this following command to package the new edition

`package config.json package1.zip package2.zip`

For example:
`package config.json xSnomedCT_ManagedServiceFR_PREPRODUCTION_FR1000315_20240621T120000Z.zip SnomedCT_InternationalRF2_PRODUCTION_20240501T120000Z.zip`

Note: 
- You can run the command `package` without any parameters if the "config.json" file and all zip files are in the same directory.
- The sample of config.json can be found here: https://github.com/IHTSDO/snomed-edition-packager/blob/develop/src/main/resources/config.json
- In the config.json file, if there is no configuration for Release Package Information JSON file, then the edition package will take the existing one from extension.
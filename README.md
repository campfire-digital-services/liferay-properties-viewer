# Liferay Properties Viewer Portlet

*liferay-properties-viewer-portlet*

This project provides an improved view for searching and browsing system and portal properties, provides export, and helps in maintenance of configuration changes.


## Supported Products

* Liferay Portal 6.1 CE GA2, GA3 (6.1.1+)
* Liferay Portal 6.1 EE GA2, GA3 (6.1.20+)
* Liferay Portal 6.2 CE GA1 (6.2.0+)
* Liferay Portal 6.2 EE GA1 (6.2.10+)


## Downloads

The latest releases are available from [SourceForge](http://sourceforge.net/projects/permeance-apps/files/liferay-properties-viewer/ "Liferay Properties Viewer").

Liferay instances can also download the app from [Liferay Marketplace](http://www.liferay.com/marketplace/-/mp/application/22320020 "Liferay Properties Viewer").

## Usage

Administrators will see a "Properties Viewer" portlet in the Server area of the Control Panel in 6.1.
In 6.2, the portlet is under the Apps section of the Control Panel.
Other users can also be assigned permissions to see the Properties Viewer Portlet.

![Properties Viewer Portlet](/doc/images/properties-viewer-screenshot.png "Properties Viewer Portlet")

![Properties Viewer Portlet](/doc/images/properties-searcher-6.2.png "Properties Viewer Portlet")

The view is similar to the "properties" tab under Server Administration, with two main differences:
* users can search the properties by key and value, to quickly browse to a known property or find available properties without browsing the portal.properties file
* users can view the full value of a property. If the value is large, the value displayed will still be truncated but a "show" javascript link is available which will display the full value in a resizable AUI textarea.

Export is also available:
* users can export either the full set of system/portal properties, or only the current search results
* the exported properties file has the properties listed in key alphabetical order
* users can select a "password safe" option - which will obfuscate any property with the key ending in "password".

The export feature may assist administrators who need to:
* perform versioning or change management on Liferay system or portal properties.
* manage multiple Liferay instances - exported properties can help quickly narrow down configuration differences between instances
* attach the Liferay portal properties in a support ticket

If you need to compare an exported properties file with a normal properties file, simply upload the normal properties file into the Format feature. 
This returns the same set of properties, but in the same key alphabetical sort order as the export.
You can then load the formatted properties and the exported properties in the diff tool of your choice to find the differences.


## Building

Step 1. Checkout source from GitHub project

    % git  clone  https://github.com/permeance/liferay-properties-viewer

Step 2. Build and package

    % mvn  -U  clean  package

This will build "liferay-properties-viewer-portlet-XXX.war" in the targets tolder.

NOTE: You will require JDK 1.6+ and Maven 3.

Branch 6.1.x contains code for Liferay 6.1. 
Branch 6.2.x contains code for Liferay 6.2.
Master is sync'ed with branch 6.2.x.

## Installation

### Liferay Portal + Apache Tomcat Bundle

eg.

Deploy "liferay-properties-viewer-portlet-LPX.X-X.X.X.X.war" to "LIFERAY_HOME/deploy" folder.

## License

Liferay Properties Viewer is available under GNU Public License version 3.0 (GPLv3). A copy of the license is attached in the package.

## Project Team

* Chun Ho - chun.ho@permeance.com.au

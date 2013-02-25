# Liferay Properties Viewer Portlet

*liferay-properties-viewer-portlet*

This project provides an improved view for searching and browsing system and portal properties.


## Supported Products

* Liferay Portal 6.1 CE GA2 (6.1.1+)
* Liferay Portal 6.1 EE GA2 (6.1.20+)


## Downloads

The latest releases are available from [SourceForge](http://sourceforge.net/projects/permeance-apps/files/liferay-properties-viewer/ "Liferay Properties Viewer").


## Usage

Administrators will see a "Properties Viewer" portlet in the Server area of the Control Panel.
Other users can also be assigned permissions to see the Properties Viewer Portlet.

![Properties Viewer Portlet](/doc/images/properties-viewer-screenshot.png "Properties Viewer Portlet")

The view is similar to the "properties" tab under Server Administration, with two main differences:
* users can search the properties by key and value, to quickly browse to a known property or find available properties without browsing the portal.properties file
* users can view the full value of a property. If the value is large, the value displayed will still be truncated but a "show" javascript link is available which will display the full value in a resizable AUI textarea.



## Building

Step 1. Checkout source from GitHub project

    % git  clone  https://github.com/permeance/liferay-properties-viewer

Step 2. Build and package

    % mvn  -U  clean  package

This will build "liferay-properties-viewer-portlet-XXX.war" in the targets tolder.

NOTE: You will require JDK 1.6+ and Maven 3.


## Installation

### Liferay Portal + Apache Tomcat Bundle

eg.

Deploy "liferay-properties-viewer-portlet-1.0.0.0.war" to "LIFERAY_HOME/deploy" folder.


## Project Team

* Chun Ho - chun.ho@permeance.com.au

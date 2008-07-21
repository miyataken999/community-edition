## This script will execute a full build of the Alfresco installer
## which includes the JDK and OpenOffice components.

## Command-line Example:
##
## ./installjammer --build --control-script <this script> <project file>
##

## Activate the OOo file group that are disabled in the default project.
::BuilderAPI::ModifyObject -object "OpenOffice File" -active Yes
::BuilderAPI::ModifyObject -object "OpenOffice" -active Yes

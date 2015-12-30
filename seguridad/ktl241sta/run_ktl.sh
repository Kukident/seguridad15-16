#!/bin/sh
clear

# --- BEG MACHINE-DEPENDENT ----
HOME_J2RE=/usr/java/jdk1.6.0/jre
# --- END MACHINE-DEPENDENT ----

HOME_JAVA=${HOME_J2RE}/bin/java
ARG_MEMORY="-Xms128m -Xmx196m"
MAIN_JAR=rc15ktl.jar

_CMD_="${HOME_JAVA} ${ARG_MEMORY} -jar ${MAIN_JAR}"
echo ${_CMD_}

${_CMD_}

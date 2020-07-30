#!/bin/sh

# project name
APPLICATION="@project.artifactId@"

# java file name
APPLICATION_JAR="@build.finalName@.jar"

# stop app
echo stop ${APPLICATION} Application...
sh shutdown.sh

# start app
echo start ${APPLICATION} Application...
sh startup.sh
#!/bin/sh

# project name
APPLICATION="@pkg.name@"

# java file name
APPLICATION_JAR="@build.finalName@.jar"

# bin directory absolute path
cd `dirname $0`
cd ..

# stop app
echo stop ${APPLICATION} Application...
sh shutdown.sh

# start app
echo start ${APPLICATION} Application...
sh startup.sh
#!/bin/sh

# project name
APPLICATION="@project.artifactId@"

# jar file name
APPLICATION_JAR="@build.finalName@.jar"

PID=$(ps -ef | grep "${APPLICATION_JAR}" | grep -v grep | awk '{ print $2 }')
if [[ -z "$PID" ]]
then
    echo ${APPLICATION} is already stopped
else
    echo kill ${PID}
    kill -9 ${PID}
    echo ${APPLICATION} stopped successfully
fi
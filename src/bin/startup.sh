#!/bin/sh

# project name
APPLICATION="@project.artifactId@"

# java file name
APPLICATION_JAR="@build.finalName@.jar"

# bin directory absolute path
BIN_PATH=$(cd `dirname $0`; pwd)
# cd
cd `dirname $0`
cd ..
# base path
BASE_PATH=`pwd`

# jar directory
JAR_DIR=${BASE_PATH}"/lib/"
# config directory
CONFIG_DIR=${BASE_PATH}"/config/"

# log dir
LOG_DIR=${BASE_PATH}"/logs"

# create logs directory
if [[ ! -d "${LOG_DIR}" ]]; then
  mkdir "${LOG_DIR}"
fi

# JVM Configuration
JAVA_OPT="-server -Xms1g -Xmx1g"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BASE_PATH}/"

# start app
nohup java ${JAVA_OPT} -jar ${JAR_DIR}/${APPLICATION_JAR} --spring.config.location=${CONFIG_DIR} --logging.config=${CONFIG_DIR}/logback.xml > /dev/null 2>&1 &

echo ${APPLICATION} start successfully





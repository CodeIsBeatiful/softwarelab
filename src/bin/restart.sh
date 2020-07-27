#!/bin/sh

# 项目名称
APPLICATION="software-lab"

# 项目启动jar包名称
APPLICATION_JAR="@build.finalName@.jar"

# 停服
echo stop ${APPLICATION} Application...
sh shutdown.sh

# 启动服务
echo start ${APPLICATION} Application...
sh startup.sh
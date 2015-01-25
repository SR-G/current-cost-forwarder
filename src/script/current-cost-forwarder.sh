#!/bin/sh
SCRIPT_NAME=$0
CURRENT_PATH=`dirname "${SCRIPT_NAME}"`
LIB_PATH="${CURRENT_PATH}/lib/"
MAIN_JAR=`ls -1 "${LIB_PATH}" 2>/dev/null | grep -v "plugin" | grep "current-cost-forwarder"`
java -Xms92M -Xmx128M -jar "${LIB_PATH}/${MAIN_JAR}" --pid ${CURRENT_PATH}/current-cost-forwarder.pid --broker-url "tcp://192.168.8.40:1883" --broker-topic "metrics/current-cost" $*
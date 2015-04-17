#!/usr/bin/env bash

SELENIUM_VERSION='2.45.0'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
HUB_REG_URL='http://localhost:4444/grid/register'
SHUTDOWN_SERVLET='com.moraustin.NodeShutdownServlet'
NODE_CONFIG='config/nodeConfig.json'
SLEEP_INTERVAL='10'


function usage() {
	echo "${1} usage:
	-c: node config file (default: ${NODE_CONFIG})
	-v: selenium standalone jar version (default: ${SELENIUM_VERSION})
	-u: selenium hub registration url (default: ${HUB_REG_URL})
	-h: show this message and exit
	"
}

while getopts c:u:v:h opt
do
	case ${opt} in
		c) NODE_CONFIG="${OPTARG}";;
		u) HUB_REG_URL="${OPTARG}";;
		v) SELENIUM_VERSION="${OPTARG}";;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

SELENIUM_BINARY="selenium-server-standalone-${SELENIUM_VERSION}.jar"
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"


SHOULD_RUN=true

kill_node() {
	SHOULD_RUN=false
	kill 0
	wait
}

trap kill_node TERM INT

while ${SHOULD_RUN}; do
	java -cp ${CLASSPATH} ${MAIN_CLASS} -role node -hub ${HUB_REG_URL} -servlets ${SHUTDOWN_SERVLET} -nodeConfig ${NODE_CONFIG} &
	wait
	sleep ${SLEEP_INTERVAL}
done

#!/usr/bin/env bash

SELENIUM_VERSION='2.45.0'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
HUB_REG_URL='http://localhost:4444/grid/register'
SHUTDOWN_SERVLET='com.moraustin.NodeShutdownServlet'
STATUS_SERVLET='com.moraustin.NodeStatusServlet'
NODE_CONFIG='config/nodeConfig.json'
SLEEP_INTERVAL='10'
XVFB_CMD=''
LOG_LEVEL='INFO'
RUN_ONCE=false

function getPwd() {
	SOURCE="${BASH_SOURCE[0]}"
	# resolve $SOURCE until the file is no longer a symlink
	while [ -h "${SOURCE}" ]; do
		DIR="$(cd -P "$(dirname "${SOURCE}")" && pwd)"
		SOURCE="$(readlink "${SOURCE}")"
		# if $SOURCE was a relative symlink, we need to resolve it
		# relative to the path where the symlink file was located
		[[ ${SOURCE} != /* ]] && SOURCE="${DIR}/${SOURCE}"
	done
	echo "$(cd -P "$(dirname "${SOURCE}")" && pwd)"
}

# set working directory to script directory to support relative classpaths
cd $(getPwd)

printf 'Working directory is %s\n' $(pwd)

function usage() {
	echo "${1} usage:
	-c: node config file (default: ${NODE_CONFIG})
	-l: logging level (default: ${LOG_LEVEL})
	-v: selenium standalone jar version (default: ${SELENIUM_VERSION})
	-u: selenium hub registration url (default: ${HUB_REG_URL})
	-h: show this message and exit
	"
}

while getopts c:l:ou:v:h opt
do
	case ${opt} in
		c) NODE_CONFIG="${OPTARG}";;
		l) LOG_LEVEL="${OPTARG}";;
		o) RUN_ONCE=true;;
		u) HUB_REG_URL="${OPTARG}";;
		v) SELENIUM_VERSION="${OPTARG}";;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

SELENIUM_BINARY="selenium-server-standalone-${SELENIUM_VERSION}.jar"
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"

if [[ $(uname) == 'Linux' ]]; then
	XVFB_CMD='xvfb-run'
fi

SHOULD_RUN=true

kill_node() {
	SHOULD_RUN=false
	kill 0
	wait
}

trap kill_node TERM INT

echo 'starting node'

if ${RUN_ONCE}; then
	echo 'only running once'
	${XVFB_CMD} java -Dselenium.LOGGER.level="${LOG_LEVEL}" -cp ${CLASSPATH} ${MAIN_CLASS} \
		-role node -hub ${HUB_REG_URL} -servlets "${SHUTDOWN_SERVLET},${STATUS_SERVLET}" -nodeConfig ${NODE_CONFIG}
	else
		while ${SHOULD_RUN}; do
			${XVFB_CMD} java -Dselenium.LOGGER.level="${LOG_LEVEL}" -cp ${CLASSPATH} ${MAIN_CLASS} \
				-role node -hub ${HUB_REG_URL} -servlets ${SHUTDOWN_SERVLET} -nodeConfig ${NODE_CONFIG} &
			wait
			sleep ${SLEEP_INTERVAL}
		done
fi

echo 'node has stopped'

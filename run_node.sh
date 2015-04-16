#!/usr/bin/env bash

SELENIUM_BINARY='selenium-server-standalone-2.45.0.jar'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
HUB_REG_URL='http://localhost:4444/grid/register'
SHUTDOWN_SERVLET='com.moraustin.NodeShutdownServlet'
NODE_CONFIG='config/nodeConfig.json'
SLEEP_INTERVAL='10'

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

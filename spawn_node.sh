#!/usr/bin/env bash

MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
HUB_REG_URL='http://localhost:4444/grid/register'
SHUTDOWN_SERVLET='com.moraustin.NodeShutdownServlet'
NODE_CONFIG='../config/nodeConfig.json'
SLEEP_INTERVAL='10'

SHOULD_RUN=true

kill_node() {
  SHOULD_RUN=false
  kill 0
  wait
}

trap kill_node TERM INT

cd bin

while ${SHOULD_RUN}; do
    java -cp *:. ${MAIN_CLASS} -role node -hub ${HUB_REG_URL} -servlets ${SHUTDOWN_SERVLET} -nodeConfig ${NODE_CONFIG} &
    wait
    sleep ${SLEEP_INTERVAL}
done

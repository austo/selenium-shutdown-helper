#!/usr/bin/env bash

MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
HUB_REG_URL='http://localhost:4444/grid/register'
SHUTDOWN_SERVLET='com.moraustin.NodeShutdownServlet'

cd bin

while true; do
    java -cp *:. ${MAIN_CLASS} -role node -hub ${HUB_REG_URL} -servlets ${SHUTDOWN_SERVLET} -nodeConfig ../config/nodeConfig.json
    sleep 20
done

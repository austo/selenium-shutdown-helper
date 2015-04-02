#!/bin/bash

export GEOMETRY="${SCREEN_WIDTH}""x""${SCREEN_HEIGHT}""x""${SCREEN_DEPTH}"

SELENIUM_BIN_DIR='/opt/selenium'
SELENIUM_BINARY='selenium-server-standalone.jar'
HELPER_BINARY='selenium-shutdown-helper.jar'
CLASSPATH="${SELENIUM_BIN_DIR}/${SELENIUM_BINARY}:${SELENIUM_BIN_DIR}/${HELPER_BINARY}"
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
REMOTE_HOST_PARAM=''

function shutdown {
  kill -s SIGTERM ${NODE_PID}
  wait ${NODE_PID}
}

if [ ! -e /opt/selenium/config.json ]; then
  echo No Selenium Node configuration file, the node-base image is not intended to be run directly. 1>&2
  exit 1
fi

if [ -z "${HUB_PORT_4444_TCP_ADDR}" ]; then
  echo Not linked with a running Hub container 1>&2
  exit 1
fi

if [ -z "${HUB_PORT_4444_TCP_PORT}" ]; then
  echo Not linked with a running Hub container 1>&2
  exit 1
fi

if [ ! -z "${REMOTE_HOST}" ]; then
  REMOTE_HOST_PARAM="-remoteHost ${REMOTE_HOST}"
  echo "REMOTE_HOST variable is set, appending \"${REMOTE_HOST_PARAM}\""
fi

# TODO: Look into http://www.seleniumhq.org/docs/05_selenium_rc.jsp#browser-side-logs
xvfb-run --server-args="${DISPLAY} -screen 0 ${GEOMETRY} -ac +extension RANDR" \
  sudo -E -i -u seluser \
    java -cp ${CLASSPATH} ${MAIN_CLASS} \
      -role node \
      -hub http://${HUB_PORT_4444_TCP_ADDR}:${HUB_PORT_4444_TCP_PORT}/grid/register \
      ${REMOTE_HOST_PARAM} \
      -nodeConfig /opt/selenium/config.json &
NODE_PID=$!

trap shutdown SIGTERM SIGINT
wait ${NODE_PID}
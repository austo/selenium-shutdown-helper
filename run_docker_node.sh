#!/bin/bash

CONTAINER_NAME='ssi-selenium-node'

function usage() {
	echo "${1} usage:
	-r: ip of docker host to which selenium hub will communicate
	-m: ip of selenium hub
	-h: show this message and exit
	"
}

while getopts r:m: opt
do
	case ${opt} in
		r) DOCKER_NODE_IP=${OPTARG};;
		m) SELENIUM_HUB_IP=${OPTARG};;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

if [ -z "${DOCKER_NODE_IP}" ]; then
  echo no docker node ip 1>&2
  exit 1
fi

if [ -z "${SELENIUM_HUB_IP}" ]; then
  echo no selenium hub ip 1>&2
  exit 1
fi

docker run -p 5555:5555 \
	-e "HUB_PORT_4444_TCP_ADDR=${SELENIUM_HUB_IP}" \
	-e 'HUB_PORT_4444_TCP_PORT=4444' \
	-e "REMOTE_HOST=http://${DOCKER_NODE_IP}:5555" \
	${CONTAINER_NAME} >${CONTAINER_NAME}.log 2>&1 &


#!/usr/bin/env bash

SELENIUM_VERSION='2.45.0'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
PROPERTIES_ARG=''

function usage() {
	echo "${1} usage:
	-p: proxy properties file
	-v: selenium standalone jar version
	-h: show this message and exit
	"
}

while getopts p:v:h opt
do
	case ${opt} in
		p) PROPERTIES_ARG="-DproxyPropertiesPath=${OPTARG}";;
		v) SELENIUM_VERSION="${OPTARG}";;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

SELENIUM_BINARY="selenium-server-standalone-${SELENIUM_VERSION}.jar"
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"

java ${PROPERTIES_ARG} -cp ${CLASSPATH} ${MAIN_CLASS} -role hub

#!/usr/bin/env bash

SELENIUM_BINARY='selenium-server-standalone-2.45.0.jar'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
PROPERTIES_ARG=''

function usage() {
	echo "${1} usage:
	-p: proxy properties file
	-h: show this message and exit
	"
}

while getopts p: opt
do
	case ${opt} in
		p) PROPERTIES_ARG="-DproxyPropertiesPath=${OPTARG}";;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

java ${PROPERTIES_ARG} -cp ${CLASSPATH} ${MAIN_CLASS} -role hub
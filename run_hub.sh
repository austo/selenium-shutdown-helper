#!/usr/bin/env bash

SELENIUM_VERSION='2.45.0'
HELPER_BINARY='selenium-shutdown-helper-1.0.jar'
MAIN_CLASS='org.openqa.grid.selenium.GridLauncher'
PROPERTIES_ARG=''
PROP_NAME='-DproxyPropertiesPath'
DEFAULT_PROPS='src/main/resources/com/moraustin/NodeShutdownProxy.properties'

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
	-p: proxy properties file (default: ${DEFAULT_PROPS})
	-v: selenium standalone jar version (default: ${SELENIUM_VERSION})
	-h: show this message and exit
	"
}

while getopts p:v:h opt
do
	case ${opt} in
		p) PROPERTIES_ARG="${PROP_NAME}=${OPTARG}";;
		v) SELENIUM_VERSION="${OPTARG}";;
		h) usage ${0}; exit;;
		\?) usage ${0}; exit 1;;
	esac
done

if [[ -z "${PROPERTIES_ARG}" ]]; then
	PROPERTIES_ARG="${PROP_NAME}=${DEFAULT_PROPS}"
fi

echo "${PROPERTIES_ARG}"

SELENIUM_BINARY="selenium-server-standalone-${SELENIUM_VERSION}.jar"
CLASSPATH="bin/${SELENIUM_BINARY}:bin/${HELPER_BINARY}"

java ${PROPERTIES_ARG} -cp ${CLASSPATH} ${MAIN_CLASS} -role hub

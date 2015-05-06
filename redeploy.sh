#!/usr/bin/env bash

# to be run from ctselenium01

case ${1} in
	dev)
		HUB='use-seleniumdev01'
		NODES='10.37.21.32
		10.32.21.33'
		;;
	qa)
		HUB='use-seleniumqa01'
		NODES='10.37.21.52
		10.37.21.53'
		;;
	prod)
		HUB='ctselenium01.surveysampling.com'
		NODES='10.1.21.40
		10.1.21.46
		10.1.21.47
		10.1.21.48
		10.37.21.101
		10.37.21.102
		10.37.21.104
		10.37.21.105
		10.37.21.106
		10.37.21.107
		10.37.21.108
		'
		;;
	*)
		echo 'first arg must be either dev, qa, or prod (case-sensitive)'
		exit 1
esac

generate_update_script() {
	echo "cd selenium;
/etc/init.d/selenium-${1} stop;
rm -fR ssi-selenium;
tar xzvf ssi-selenium.tar.gz;
/etc/init.d/selenium-${1} start;
"
}

for ip in ${HUB} ${NODES}; do
	echo "updating ${ip}...";
	if [[ ${ip} == ${HUB} ]]; then
		UPDATE_SCRIPT=$(generate_update_script 'hub')
	else
		UPDATE_SCRIPT=$(generate_update_script 'node')
	fi
	scp /tmp/ssi-selenium.tar.gz ${ip}:selenium/
	ssh -tt ${ip} ${UPDATE_SCRIPT}
done

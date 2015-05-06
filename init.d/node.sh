#!/bin/sh

# Use this script on the selenium node when running in SSI environments.
# Change HUB_HOST to match location of selenium hub server.
HUB_HOST=use-seleniumdev01.surveysampling.com

PORTS='5555
5556'

for p in ${PORTS}; do
	/home/selenium/selenium/ssi-selenium/run_node.sh -c config/ssiMultiNodeConfig.json -l WARNING \
		-p ${p} -u "http://${HUB_HOST}:4444/grid/register" >> /home/selenium/logger/logs/filtered-${p}.log &
done
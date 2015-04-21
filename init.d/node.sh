#!/bin/sh

# Use this script on the selenium node when running in SSI environments.
# Change HUB_HOST to match location of selenium hub server.
HUB_HOST=use-seleniumdev01.surveysampling.com

/home/selenium/selenium/ssi-selenium/run_node.sh -c config/ssiNodeConfig.json -l WARNING \
	-u "http://${HUB_HOST}:4444/grid/register" >> /home/selenium/logger/logs/filtered.log
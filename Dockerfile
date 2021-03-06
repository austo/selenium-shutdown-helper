FROM selenium/node-firefox:2.45.0

USER root

COPY bin/selenium-shutdown-helper-1.0.jar /opt/selenium/selenium-shutdown-helper.jar
COPY config/nodeConfig.json /opt/selenium/config.json
COPY docker_node_entry_point.sh /opt/bin/entry_point.sh
RUN chmod a+x /opt/bin/entry_point.sh

USER seluser
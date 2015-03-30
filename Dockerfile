FROM selenium/node-firefox:2.45.0

USER root

COPY dockerNodeConfig.json /opt/selenium/config.json
COPY docker_node_entry_point.sh /opt/bin/entry_point.sh
RUN chmod +x /opt/bin/entry_point.sh

USER seluser
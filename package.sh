#!/usr/bin/env bash

DEPLOY_DIR='ssi-selenium'
DEPLOY_ARCHIVE="${DEPLOY_DIR}.tar.gz"

printf "Creating deployment archive at $(tput bold)$(tput setaf 2)%s$(tput sgr0)\n" "${DEPLOY_ARCHIVE}"
rm -f ${DEPLOY_ARCHIVE}
rm -fr ${DEPLOY_DIR}
mkdir -p "${DEPLOY_DIR}/src/main"
cp -fR bin "${DEPLOY_DIR}/"
cp -fR src/main/resources "${DEPLOY_DIR}/src/main"
cp -fR config "${DEPLOY_DIR}/"
cp run_hub.sh run_node.sh "${DEPLOY_DIR}/"
tar czvf ${DEPLOY_ARCHIVE} ${DEPLOY_DIR}
rm -fr ${DEPLOY_DIR}

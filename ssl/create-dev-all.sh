#!/usr/bin/env bash
# Create a new CA and server certificates for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl

./create-dev-ca.sh
./create-dev-cert.sh "${SSL_SERVER_CN}"

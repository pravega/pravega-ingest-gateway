#!/usr/bin/env bash
# Create a new CA and server certificates for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl

# Create CA key pair.
./create-dev-ca.sh

# Create key pair for HTTPS server.
./create-dev-cert.sh "${SSL_SERVER_CN}"

# Create key pair for HTTPS client.
./create-dev-cert.sh "client1"

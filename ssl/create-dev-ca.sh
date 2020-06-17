#!/usr/bin/env bash
# Create a new Certificate Authority for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl/data

openssl genrsa -out ca.key 4096
openssl req -new -x509 -key ca.key -out ca.pem -days 7300 \
  -subj "${SSL_SUBJECT_PREFIX}/CN=${SSL_CA_CN}"
cat ca.key ca.pem > ca_bundle.key
openssl x509 -inform pem -in ca.pem -noout -text >> ca.pem

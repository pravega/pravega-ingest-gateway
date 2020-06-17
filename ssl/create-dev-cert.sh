#!/usr/bin/env bash
# Create a new certificate for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl/data

SSLCN="$1"; shift
openssl genrsa -out ${SSLCN}.key 4096
openssl req -new -key ${SSLCN}.key -out ${SSLCN}.csr \
  -subj "${SSL_SUBJECT_PREFIX}/CN=${SSLCN}"
openssl x509 -req -days 3650 -in ${SSLCN}.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out ${SSLCN}.pem
openssl x509 -inform pem -in ${SSLCN}.pem -noout -text >> ${SSLCN}.pem

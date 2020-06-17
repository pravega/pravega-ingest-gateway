#!/usr/bin/env bash
# Create a new key/certificate pair for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl/data

SSLCN="$1"; shift
# Create private key.
openssl genrsa -out ${SSLCN}.key 4096
# Create cert signing request.
openssl req -new -key ${SSLCN}.key -out ${SSLCN}.csr -subj "${SSL_SUBJECT_PREFIX}/CN=${SSLCN}"
# Sign cert signing request and create public cert.
openssl x509 -req -days 3650 -in ${SSLCN}.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out ${SSLCN}.pem
# Append description to public cert.
openssl x509 -inform pem -in ${SSLCN}.pem -noout -text >> ${SSLCN}.pem
# Create PKCS12 file containing private key and public cert.
openssl pkcs12 -export -in ${SSLCN}.pem -inkey ${SSLCN}.key -out ${SSLCN}.pkcs12 -passout pass:${KEYSTORE_PASSWORD}

#!/usr/bin/env bash
# Create a new Certificate Authority for development/testing purposes only.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
mkdir -p ${ROOT_DIR}/ssl/data
cd ${ROOT_DIR}/ssl/data

# Create private key
openssl genrsa -out ca.key 4096
# Create self-signed public cert.
openssl req -new -x509 -key ca.key -out ca.pem -days 7300 \
  -subj "${SSL_SUBJECT_PREFIX}/CN=${SSL_CA_CN}"
# Create CA bundle file containing private key and public cert.
cat ca.key ca.pem > ca_bundle.key
# Append description to public cert.
openssl x509 -inform pem -in ca.pem -noout -text >> ca.pem
# Create a Java TrustStore containing the public CA certificate.
rm truststore.jks || true
keytool -import -file ca.pem -alias ca -keystore truststore.jks -storepass ${KEYSTORE_PASSWORD} -noprompt

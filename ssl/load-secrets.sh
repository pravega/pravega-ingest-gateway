#!/usr/bin/env bash
# Load server certificates into Kubernetes.
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
cd ${ROOT_DIR}/ssl/data

mkdir -p "secrets/tls/${SSL_SERVER_CN}"
cp "${SSL_SERVER_CN}.pem" "secrets/tls/${SSL_SERVER_CN}/tls.crt"
cp "${SSL_SERVER_CN}.key" "secrets/tls/${SSL_SERVER_CN}/tls.key"

for d in secrets/tls/* ; do
    dirname=$(basename "$d")
    secret_name="${dirname}-tls"
    kubectl delete secret ${secret_name} -n ${NAMESPACE} || true
    kubectl create secret tls ${secret_name} --cert "$d/tls.crt" --key "$d/tls.key" -n ${NAMESPACE}
done

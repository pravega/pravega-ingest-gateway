#!/usr/bin/env bash
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh

export CURL_CA_BUNDLE="${ROOT_DIR}/ssl/data/ca.pem"

curl -v \
--header "Content-Type: application/csv" \
--request POST \
--data \
'"111111","1588203203770","098765","60.96","80.4","my_routing_key_2","1592613114765","2020-06-20T10:31:54.763Z"
"222222","1588203203770","098765","60.96","80.4","my_routing_key_2","1592613114765","2020-06-20T10:31:54.763Z"
"333333","1588203203770","098765","60.96","80.4","my_routing_key_2","1592613114765","2020-06-20T10:31:54.763Z"
"444444","1588203203770","098765","60.96","80.4","my_routing_key_2","1592613114765","2020-06-20T10:31:54.763Z"
"555555","1588203203770","098765","60.96","80.4","my_routing_key_2","1592613114765","2020-06-20T10:31:54.763Z"' \
${GATEWAY_URL}/data/rawData/my_routing_key_2

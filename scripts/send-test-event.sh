#!/usr/bin/env bash
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh

export CURL_CA_BUNDLE="${ROOT_DIR}/ssl/data/ca.pem"

curl -v \
--header "Content-Type: application/json" \
--request POST \
--data '
{
"message_id": "40e4517d-3aff-480a-b872-cb8f039b2807",
"timestamp": 1588203203990,
"device_id": 23456,
"temperature": 30.96,
"humidity": 90.2
}
' \
${GATEWAY_URL}/jsonData/my_routing_key_1
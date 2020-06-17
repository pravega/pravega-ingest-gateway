#! /bin/bash
# Copyright (c) Dell Inc., or its subsidiaries. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
set -ex

: ${1?"You must specify the values.yaml file."}

export ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh
VALUES_FILE="$1"
shift
export RELEASE_NAME=$(basename "${VALUES_FILE}" .yaml)

if [[ "${UNINSTALL}" == "1" ]]; then
    helm del -n ${NAMESPACE} ${RELEASE_NAME} $@ || true
fi

helm upgrade --install --timeout 600s --debug --wait \
    ${RELEASE_NAME} \
    ${ROOT_DIR}/charts/pravega-ingest-gateway \
    --namespace ${NAMESPACE} \
    -f "${VALUES_FILE}" \
    $@

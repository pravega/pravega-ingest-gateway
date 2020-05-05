#! /bin/bash
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh

docker run -d \
  --restart always \
  -e PRAVEGA_CONTROLLER \
  -e pravega_client_auth_method= \
  -e pravega_client_auth_loadDynamic= \
  -p 3000:8080 \
  --name pravega-ingest-gateway \
  ${DOCKER_REPOSITORY}:${IMAGE_TAG}

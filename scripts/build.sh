#! /bin/bash
set -ex
ROOT_DIR=$(dirname $0)/..
source ${ROOT_DIR}/scripts/env-local.sh

: ${DOCKER_REPOSITORY?"You must export DOCKER_REPOSITORY"}
: ${IMAGE_TAG?"You must export IMAGE_TAG"}

docker build -f ${ROOT_DIR}/gateway/Dockerfile ${ROOT_DIR} --tag ${DOCKER_REPOSITORY}:${IMAGE_TAG}

docker push ${DOCKER_REPOSITORY}:${IMAGE_TAG}

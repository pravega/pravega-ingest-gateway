export DOCKER_REPOSITORY=claudiofahey/pravega-ingest-gateway
export IMAGE_TAG=0.0.1

# Below for Kubernetes
export NAMESPACE=examples

CHARTS=${CHARTS:-\
pravega-ingest-gateway \
}

# Below for running in Docker
export HOST_IP=10.246.27.131
export PRAVEGA_CONTROLLER=tcp://${HOST_IP}:9090

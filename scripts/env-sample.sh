export DOCKER_REPOSITORY=claudiofahey/pravega-ingest-gateway
export IMAGE_TAG=0.0.1

# Below for Kubernetes
export NAMESPACE=examples

# Below for running in Docker
export HOST_IP=10.246.27.131
export PRAVEGA_CONTROLLER=tcp://${HOST_IP}:9090

export GATEWAY_URL=https://pravega-ingest-gateway.examples.xorn.em.sdp.hop.lab.emc.com

export SSL_SUBJECT_PREFIX="/O=Acme Co"
export SSL_CA_CN="Pravega Ingest Gateway Fake CA"
export SSL_SERVER_CN="pravega-ingest-gateway.examples.xorn.em.sdp.hop.lab.emc.com"

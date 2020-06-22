# Make sure that charts/pravega-ingest-gateway/values.yaml matches below values.
export DOCKER_REPOSITORY=claudiofahey/pravega-ingest-gateway
export IMAGE_TAG=0.0.2

# Below for Kubernetes
export NAMESPACE=examples

# Below for running in Docker
export HOST_IP=10.246.27.131
export PRAVEGA_CONTROLLER=tcp://${HOST_IP}:9090

export GATEWAY_FQDN=pravega-ingest-gateway.examples.xorn.em.sdp.hop.lab.emc.com

# Use below for a local instance of the gateway.
#export GATEWAY_URL=http://localhost:3000

# Use below for non-TLS HTTP.
export GATEWAY_URL=http://${GATEWAY_FQDN}

# Use below for TLS HTTPS.
#export GATEWAY_URL=https://${GATEWAY_FQDN}

export SSL_SUBJECT_PREFIX="/O=Acme Co"
export SSL_CA_CN="Pravega Ingest Gateway Fake CA"
export SSL_SERVER_CN=${GATEWAY_FQDN}
export KEYSTORE_PASSWORD="changeit"

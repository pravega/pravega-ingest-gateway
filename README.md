# Pravega Ingest Gateway

The Pravega Ingest Gateway is a simple HTTP server that can be used to write
JSON events to a Pravega stream.

# Run Gateway in Dell EMC Streaming Data Platform (SDP)

1. Edit the file charts/pravega-ingest-gateway/values.yaml as needed.

2. Build Docker image. This is only needed if changes are made.

```
scripts/build.sh
```

3. Deploy using Helm.

```
scripts/deploy.sh
```

# Run Gateway in Docker

```
scripts/build.sh
scripts/deploy-docker.sh
```

# Run Gateway Locally

```
export PRAVEGA_CONTROLLER=tcp://localhost:9090
../gradlew run
```

# Send Test Event

```
scripts/send-test-event.sh
```

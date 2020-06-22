# Pravega Ingest Gateway

The Pravega Ingest Gateway is a simple HTTP server that can be used to write
JSON events to a Pravega stream.

### Deploy to SDP using Helm

1. Copy the file `scripts/env-sample.sh` to `scripts/env-local.sh`.
   This script will contain parameters for your environment.
   
2. You can enable TLS (HTTPS) with a private Certificate Authority with these steps.
   This method is intended only for development and testing environments.
   ```shell script
   cd ssl
   ./create-dev-all.sh
   ./load-secrets.sh
   ```

3. Copy the sample values file from `values/samples/pravega-ingest-gateway.yaml` or
   `values/samples/pravega-ingest-gateway-tls.yaml` to
   `values/local/pravega-ingest-gateway.yaml` or any other destination.
   You may name this file anything, but you must use alphanumeric characters and dashes only.

4. Edit this file to use your Pravega stream name and HTTP server FQDN.
   
5. (Optional) If you customized the Java code, compile and deploy the Docker image.

   a. Set DOCKER_REPOSITORY and IMAGE_TAG in scripts/env-local.sh.
   
   b. Add matching values for `image.repository` and `image.tag` in values/local/pravega-ingest-gateway.yaml.
   
   c. Build and push the Docker image.
      ```shell script   
       scripts/build.sh
       ```

6. Deploy the Pravega Ingest Gateway using Helm.
   ```shell script
   scripts/deploy.sh values/local/pravega-ingest-gateway.yaml
   ```

7. Send a test event.
   ```shell script
   scripts/send-test-event.sh
   ```

# Run Gateway in Docker

```shell script
scripts/build.sh
scripts/deploy-docker.sh
```

# Run Gateway Locally

```shell script
export PRAVEGA_CONTROLLER=tcp://localhost:9090
../gradlew run
```

# Send Test Event

```shell script
scripts/send-test-event.sh
```

package io.pravega.example.iot.gateway;

import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.EventStreamWriter;
import io.pravega.client.stream.EventWriterConfig;
import io.pravega.client.stream.ScalingPolicy;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.client.stream.TransactionalEventStreamWriter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.pravega.client.stream.impl.ByteArraySerializer;

import java.net.URI;

class Main {
    private static final Logger Log = LoggerFactory.getLogger(Main.class);
    private static EventStreamWriter<byte[]> writer;
    private static TransactionalEventStreamWriter<byte[]> transWriter;
    private static ByteArraySerializer SERIALIZER = new ByteArraySerializer();

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    private static HttpServer startServer() {
        // Create a resource config that scans for JAX-RS resources and providers.
        final ResourceConfig rc = new ResourceConfig().packages("io.pravega.example.iot.gateway");

        // Create and start a new instance of grizzly http server exposing the Jersey application.
        return GrizzlyHttpServerFactory.createHttpServer(Parameters.getGatewayURI(), rc);
    }

    public static void main(String[] args) throws Exception {
        Log.info("gateway main: BEGIN");
        Log.info("Stream: {}/{}", Parameters.getScope(), Parameters.getStreamName());
        Log.info("RequireDurableWrites: {}", Parameters.getRequireDurableWrites());

        URI controllerURI = Parameters.getControllerURI();
        StreamManager streamManager = StreamManager.create(controllerURI);
        String scope = Parameters.getScope();

        String streamName = Parameters.getStreamName();
        StreamConfiguration streamConfig = StreamConfiguration.builder()
                .scalingPolicy(ScalingPolicy.byEventRate(
                        Parameters.getTargetRateEventsPerSec(), Parameters.getScaleFactor(), Parameters.getMinNumSegments()))
                .build();

        streamManager.createStream(scope, streamName, streamConfig);

        ClientConfig config = ClientConfig.builder().controllerURI(controllerURI)
                .credentials(null).trustStore("").build();

        EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope(scope, config);
        writer = clientFactory.createEventWriter(
                streamName,
                SERIALIZER,
                EventWriterConfig.builder().build());
        transWriter = clientFactory.createTransactionalEventWriter(
                streamName,
                SERIALIZER,
                EventWriterConfig.builder().build());

        final HttpServer server = startServer();
        Log.info("Gateway running at {}", Parameters.getGatewayURI());
        Log.info("gateway main: END");
    }

    public static EventStreamWriter<byte[]> getWriter() {
        return writer;
    }

    public static TransactionalEventStreamWriter<byte[]> getTransactionalWriter() {
        return transWriter;
    }
}

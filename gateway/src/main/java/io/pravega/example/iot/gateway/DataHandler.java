package io.pravega.example.iot.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.glassfish.grizzly.http.server.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

@Path("/data")
public class DataHandler {
    private static final Logger Log = LoggerFactory.getLogger(DataHandler.class);

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Path("/jsonData/{remoteAddr}")
    public String postData(@Context Request request, String data, @PathParam("remoteAddr") String remoteAddr) throws Exception {
        final long ingestTimestamp = System.currentTimeMillis();
        final String ingestTimestampStr = dateFormat.format(new Date(ingestTimestamp));
        try {
            // Deserialize the JSON message.
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode tree = objectMapper.readTree(data);

            final ArrayNode arrayNode;
            if (tree instanceof ArrayNode) {
                arrayNode = (ArrayNode) tree;
            } else if (tree instanceof ObjectNode) {
                arrayNode = objectMapper.createArrayNode();
                arrayNode.add(tree);
            } else {
                throw new java.lang.IllegalArgumentException("Parameter must be a JSON array or object");
            }

            for (JsonNode jsonNode : arrayNode) {
                final ObjectNode message = (ObjectNode) jsonNode;
                // Add the remote IP address to JSON message.
                message.put("RemoteAddr", remoteAddr);

                // Add timestamp to JSON message.
                message.put("IngestTimestamp", ingestTimestamp);
                message.put("IngestTimestampStr", ingestTimestampStr);

                // Get or calculate the routing key.
                final String routingKeyAttributeName = Parameters.getRoutingKeyAttributeName();
                final String routingKey;
                if (routingKeyAttributeName.isEmpty()) {
                    routingKey = Double.toString(Math.random());
                } else {
                    JsonNode routingKeyNode = message.get(routingKeyAttributeName);
                    routingKey = objectMapper.writeValueAsString(routingKeyNode);
                }

                // Write the message to Pravega.
                Log.debug("routingKey={}, message={}", routingKey, message);
                // Writing...
                // Create an immutable writer (in this case using the default settings)
                final ObjectWriter writer = objectMapper.writer();

                // Use the writer for thread safe access.
                final byte[] bytes = writer.writeValueAsBytes(message);


                final CompletableFuture<Void> writeFuture = Main.getWriter().writeEvent(routingKey, bytes);

                // Wait for acknowledgement that the event was durably persisted.
                // This provides at-least-once guarantees.
                if (Parameters.getRequireDurableWrites()) {
                    writeFuture.get();
                }
            }
            return "{}";
        }
        catch (Exception e) {
            Log.error("Error", e);
            throw e;
        }
    }

    @POST
    @Path("/rawData/{remoteAddr}")
    public String postRawData(@Context Request request, byte[] data, @PathParam("remoteAddr") String remoteAddr) throws Exception {
        final long ingestTimestamp = System.currentTimeMillis();
        final String ingestTimestampStr = dateFormat.format(new Date(ingestTimestamp));
        try {
            if(data == null)
                return "{DATA Not Received}";

            // Get or calculate the routing key.
            final String routingKeyAttributeName = Parameters.getRoutingKeyAttributeName();
            final String routingKey;
            if (routingKeyAttributeName.isEmpty()) {
                routingKey = Double.toString(Math.random());
            }else {
                routingKey = "default_routingKey";
            }

            String message = new String(data);
            Log.debug("routingKey={}, message={}", routingKey, message);

            final CompletableFuture<Void> writeFuture = Main.getWriter().writeEvent(routingKey, data);

            // Wait for acknowledgement that the event was durably persisted.
            // This provides at-least-once guarantees.
            if (Parameters.getRequireDurableWrites()) {
                writeFuture.get();
            }

            return "{}";
        }
        catch (Exception e) {
            Log.error("Error", e);
            throw e;
        }
    }
}

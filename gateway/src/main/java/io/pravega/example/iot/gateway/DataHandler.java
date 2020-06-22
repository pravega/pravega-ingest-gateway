package io.pravega.example.iot.gateway;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.glassfish.grizzly.http.server.Request;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

import io.pravega.example.iot.gateway.Durability.Accumulator;

@Path("/data")
public class DataHandler {
    private static final Logger Log = LoggerFactory.getLogger(DataHandler.class);
    private static final ObjectMapper Mapper = new ObjectMapper();
    private static final JsonFactory Factory = new JsonFactory(Mapper);

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Path("/jsonData/{remoteAddr}")
    public String postData(@Context Request request, InputStream data, @PathParam("remoteAddr") String remoteAddr) throws Exception {
        final long ingestTimestamp = System.currentTimeMillis();
        final String ingestTimestampStr = dateFormat.format(new Date(ingestTimestamp));
        final Durability durability = Parameters.getRequireDurableWrites();
        try {
            // Deserialize the JSON message.
            final JsonParser parser = Factory.createParser(data);
            final Function<JsonToken, Boolean> endOfStream;

            JsonToken token = parser.nextToken();
            if (token == JsonToken.START_ARRAY) {
                endOfStream = t -> t == JsonToken.END_ARRAY;
                token = parser.nextToken();
            } else if (token == JsonToken.START_OBJECT) {
                endOfStream = t -> t == null;
            } else {
                throw new java.lang.IllegalArgumentException("Parameter must be a JSON array or object");
            }

            Accumulator accumulator = durability.createAccumulator();
            for (; !endOfStream.apply(token); token = parser.nextToken()) {
                final ObjectNode message = parser.readValueAsTree();
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
                    routingKey = Mapper.writeValueAsString(routingKeyNode);
                }

                // Write the message to Pravega.
                Log.debug("routingKey={}, message={}", routingKey, message);
                accumulator.writeEvent(routingKey, Mapper.writeValueAsBytes(message));
            }
            // Wait for acknowledgement that the event was durably persisted.
            // This provides at-least-once guarantees.
            accumulator.flush();
            return "{}";
        }
        catch (Exception e) {
            Log.error("Error", e);
            throw e;
        }
    }

    @POST
    @Path("/rawData/{routingKey}")
    @Produces({"application/json"})
    public String postRawData(@Context Request request, byte[] data, @PathParam("routingKey") String routingKey) throws Exception {
        try {
            Log.debug("routingKey={}, message length={}", routingKey, data.length);

            // Wait for acknowledgement that the event was durably persisted.
            // This provides at-least-once guarantees.
            Parameters.getRequireDurableWrites().createAccumulator().writeEvent(routingKey, data).flush();

            return "{}";
        }
        catch (Exception e) {
            Log.error("Error", e);
            throw e;
        }
    }
}

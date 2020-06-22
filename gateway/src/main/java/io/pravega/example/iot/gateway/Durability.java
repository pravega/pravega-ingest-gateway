package io.pravega.example.iot.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.tuple.Pair;

import io.pravega.client.stream.Transaction;
import io.pravega.common.concurrent.Futures;

public enum Durability {

    FALSE("false") {
        @Override
        public Accumulator createAccumulator() {
            return new Accumulator() {
                @Override
                public Accumulator writeEvent(String routingKey, byte[] event) throws Exception {
                    Main.getWriter().writeEvent(routingKey, event);
                    return this;
                }

                @Override
                public void flush() throws Exception {
                    // nothing to do...
                }
            };
        }
    },

    TRUE("true") {
        @Override
        public Accumulator createAccumulator() {
            return new Accumulator() {
                private final Collection<CompletableFuture<Void>> futures = new ArrayList<>();

                @Override
                public Accumulator writeEvent(String routingKey, byte[] event) throws Exception {
                    futures.add(Main.getWriter().writeEvent(routingKey, event));
                    return this;
                }

                @Override
                public void flush() throws Exception {
                    Futures.allOf(futures).get();
                }
            };
        }
    },

    ATOMIC("atomic") {
        @Override
        public Accumulator createAccumulator() {
            return new Accumulator() {

                private Pair<String, byte[]> firstEvent = null;

                private Transaction<byte[]> transaction = null;

                @Override
                public Accumulator writeEvent(String routingKey, byte[] event) throws Exception {
                    if (transaction == null && firstEvent == null) {
                        firstEvent = Pair.of(routingKey, event);
                        return this;
                    }

                    if (transaction == null) {
                        transaction = Main.getTransactionalWriter().beginTxn();
                        transaction.writeEvent(firstEvent.getKey(), firstEvent.getValue());
                        firstEvent = null;
                    }

                    transaction.writeEvent(routingKey, event);

                    return this;
                }

                @Override
                public void flush() throws Exception {
                    if (transaction != null) {
                        transaction.commit();
                    } else if (firstEvent != null) {
                        Main.getWriter().writeEvent(firstEvent.getKey(), firstEvent.getValue()).get();
                    }
                }
            };
        }
    };

    private final String value;

    private Durability(String value) {
        this.value = value;
    }

    public abstract Accumulator createAccumulator();

    public static Durability fromValue(String value) {
        if (value == null) {
            return FALSE;
        }
        for (Durability durability : Durability.values()) {
            if (durability.value.equalsIgnoreCase(value)) {
                return durability;
            }
        }
        return FALSE;
    }

    public static interface Accumulator {

        Accumulator writeEvent(String routingKey, byte[] event) throws Exception;

        void flush() throws Exception;

    }

}
package data_management;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.data_management.DataStorage;
import com.data_management.InMemoryDataStorage;
import com.data_management.WebSocketDataReader;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IntegrationWebSocketTest {

    private TestWebSocketServer server;
    private final int port = 8082;
    private DataStorage storage;
    private WebSocketDataReader reader;
    private CountDownLatch messageLatch;

    @BeforeEach
    void setUp() throws Exception {
        // Use the concrete in‑memory implementation.
        DataStorage.setInstance(new InMemoryDataStorage());
        storage = DataStorage.getInstance();
        server = new TestWebSocketServer(new InetSocketAddress(port));
        server.start();
        Thread.sleep(1000);

        messageLatch = new CountDownLatch(1);
        reader = new WebSocketDataReader(new URI("ws://localhost:" + port)) {
            @Override
            public void onMessage(String s) {
                super.onMessage(s);
                messageLatch.countDown();
            }
        };
        reader.readData(storage);
    }

    @AfterEach
    void tearDown() throws IOException {
        reader.close();
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testIntegrationFlow() throws InterruptedException {
        boolean processed = messageLatch.await(5, TimeUnit.SECONDS);
        assertTrue(processed, "Message should be processed by the reader.");
    }

    private static class TestWebSocketServer extends WebSocketServer {

        public TestWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            conn.send("1,1714376789050,HeartRate,75.5");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

        @Override
        public void onMessage(WebSocket conn, String message) {}

        @Override
        public void onError(WebSocket conn, Exception ex) {}

        @Override
        public void onStart() {}
    }
}
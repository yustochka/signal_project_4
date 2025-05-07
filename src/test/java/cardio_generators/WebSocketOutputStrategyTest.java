package cardio_generators;

import static org.junit.jupiter.api.Assertions.*;

import com.cardio_generator.outputs.WebSocketOutputStrategy;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.drafts.Draft;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class WebSocketOutputStrategyTest {

    private static class TestWebSocketServer extends WebSocketServer {
        private final Set<WebSocket> conns = new CopyOnWriteArraySet<>();

        TestWebSocketServer() {
            super(new InetSocketAddress(0));
        }
        @Override public void onOpen(WebSocket conn, ClientHandshake handshake) {}
        @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {}
        @Override public void onMessage(WebSocket conn, String message) {}
        @Override public void onError(WebSocket conn, Exception ex) {}
        @Override public void onStart() {}
        @Override public Set<WebSocket> getConnections() { return conns; }
        void injectConnection(WebSocket ws) { conns.add(ws); }
    }

    /** Full stub of the WebSocket interface—only send()/isOpen() do real work. */
    private static class TestWebSocket implements WebSocket {
        private final StringBuilder buffer = new StringBuilder();
        private boolean open = true;

        @Override public void send(String text) { if (open) buffer.append(text); }
        @Override public boolean isOpen() { return open; }

        void closeConnection() { open = false; }
        String getSent() { return buffer.toString(); }

        //defaults:
        @Override public void send(ByteBuffer bytes) {}
        @Override public void send(byte[] data) {}
        @Override public void sendFrame(Framedata framedata) {}
        @Override public void sendFrame(Collection<Framedata> frames) {}
        @Override public void sendPing() {}
        @Override public void sendFragmentedFrame(Opcode op, ByteBuffer buf, boolean fin) {}
        @Override public void close(int code) { open = false; }
        @Override public void close(int code, String message) { open = false; }
        @Override public void closeConnection(int code, String message) { open = false; }
        @Override public void close() { open = false; }
        @Override public InetSocketAddress getRemoteSocketAddress() { return new InetSocketAddress(0); }
        @Override public InetSocketAddress getLocalSocketAddress()  { return new InetSocketAddress(0); }
        @Override public String getResourceDescriptor() { return ""; }
        @Override public void setAttachment(Object attachment) {}
        @Override public <T> T getAttachment() { return null; }
        @Override public boolean hasSSLSupport() { return false; }
        @Override public SSLSession getSSLSession() { return null; }
        @Override public IProtocol getProtocol() { return null; }
        @Override public boolean hasBufferedData() { return false; }
        @Override public ReadyState getReadyState() { return open ? ReadyState.OPEN : ReadyState.CLOSING; }
        @Override public Draft getDraft() { return null; }
        @Override public boolean isClosing() { return !open; }
        @Override public boolean isFlushAndClose() { return false; }
        @Override public boolean isClosed() { return !open; }
    }

    @Test
    void shouldFormatCsvAndSendToOpenClients() {
        // Prepare stub server and stub clients
        TestWebSocketServer stubServer = new TestWebSocketServer();
        TestWebSocket client1 = new TestWebSocket();
        TestWebSocket client2 = new TestWebSocket();
        stubServer.injectConnection(client1);
        stubServer.injectConnection(client2);

        // Override createServer(...) to return our stub
        WebSocketOutputStrategy strat = new WebSocketOutputStrategy(0) {
            @Override
            protected WebSocketServer createServer(int port) {
                return stubServer;
            }
        };

        strat.output(10, 111L, "T", "9.9");

        assertEquals("10,111,T,9.9", client1.getSent());
        assertEquals("10,111,T,9.9", client2.getSent());
    }


    @Test
    void shouldSkipClosedConnections() {
        TestWebSocketServer stubServer = new TestWebSocketServer();
        TestWebSocket client = new TestWebSocket();
        stubServer.injectConnection(client);
        client.closeConnection();

        WebSocketOutputStrategy strat = new WebSocketOutputStrategy(0) {
            @Override
            protected WebSocketServer createServer(int port) {
                return stubServer;
            }
        };

        strat.output(5, 5L, "L", "1.1");
        assertTrue(client.getSent().isEmpty());
    }
}

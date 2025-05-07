package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

/**
 * WebSocketDataReader is a real-time data reader that connects to a WebSocket server,
 * receives patient data messages, parses them, and stores them in the system's DataStorage.
 * It implements the DataReader interface and extends WebSocketClient to handle WebSocket communication.
 */
public class WebSocketDataReader extends WebSocketClient implements DataReader {
    public DataStorage dataStorage;

    /**
     * Constructs a WebSocketDataReader that connects to the given server URI.
     * @param serverUri URI of the WebSocket server
     */
    public WebSocketDataReader(URI serverUri) {
        super(serverUri);
    }

    /**
     * Initiates a blocking connection to the WebSocket server and links the provided DataStorage instance.
     * @param dataStorage The storage system to store parsed patient data
     * @throws IOException If the connection is interrupted
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            // Connect and wait until the connection is established
            this.connectBlocking();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while connecting to WebSocket server.", e);
        }
    }


    /**
     * Called when the WebSocket connection is successfully opened.
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to WebSocket server.");
    }

    /**
     * Called when a message is received from the WebSocket server.
     * Parses the message and stores it in the DataStorage.
     * @param s The incoming message in CSV format
     */
    @Override
    public void onMessage(String s) {
        // Expected format: patientId,timestamp,label,data
        try {
            String[] parts = s.trim().split(",");
            if (parts.length != 4) {
                System.err.println("Invalid message format: " + s);
                return;
            }
            int patientId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            String label = parts[2];
            String data = parts[3];
            double measurementValue = Double.parseDouble(data);
            dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     * Attempts to reconnect after a short delay.
     */
    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("WebSocket connection closed: " + s);
        attemptReconnect();
    }

    /**
     * Attempts to reconnect to the WebSocket server after a delay.
     */
    private void attemptReconnect() {
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Wait 3 seconds before retry
                System.out.println("Attempting to reconnect...");
                this.reconnect(); // Reconnect is a built-in method from WebSocketClient
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Called when an error occurs in the WebSocket connection.
     * @param e The exception representing the error
     */
    @Override
    public void onError(Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
    }
}

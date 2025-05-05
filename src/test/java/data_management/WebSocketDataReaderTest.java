package data_management;

import com.data_management.DataStorage;
import com.data_management.InMemoryDataStorage;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketDataReaderTest {

    private DataStorage storage;
    private WebSocketDataReader reader;

    @BeforeEach
    void setUp() throws Exception {
        // Use the concrete in-memory implementation.
        DataStorage.setInstance(new InMemoryDataStorage());
        storage = DataStorage.getInstance();
        // Create a WebSocketDataReader that overrides connectBlocking to avoid real connection
        reader = new WebSocketDataReader(new URI("ws://localhost:12345")) {
            @Override
            public boolean connectBlocking() {
                return true;
            }
        };
        reader.readData(storage);
    }

    @Test
    void testValidMessageParsing() {
        // Simulate a valid message in the expected format.
        String message = "1,1714376789050,HeartRate,75.5";
        reader.onMessage(message);
        // Instead of checking for an exact timestamp, verify that at least one record exists
        assertFalse(storage.getRecords(1, 0L, System.currentTimeMillis()).isEmpty(),
                "Data should be stored for a valid message.");
    }

    @Test
    void testInvalidMessageHandling() {
        // Simulate an invalid message (with missing tokens)
        String message = "1,invalidMessage";
        reader.onMessage(message);
        // Check that no record was added for the patient.
        assertTrue(storage.getRecords(1, 0L, System.currentTimeMillis()).isEmpty(),
                "No data should be stored for an invalid message.");
    }

    @Test
    void testWrongNumberFormatMessage() {
        // Simulate a message with a wrong number format in patientId.
        String message = "abc,1714376789050,HeartRate,75.5";
        reader.onMessage(message);
        // Verify that no records exist in the storage at all.
        assertTrue(storage.getAllPatients().isEmpty(),
                "No data should be stored when a wrong number format is provided.");
    }
}
package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.io.IOException;
import java.util.List;

class WebSocketDataReaderTest2 {
    static class StubStorage extends DataStorage
    {
        final java.util.List<String> received = new java.util.ArrayList<>();
        @Override
        public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
            received.add(patientId+","+timestamp+","+recordType+","+measurementValue);
        }
    }
    /**
     * Test that the WebSocketDataReader correctly parses a CSV message and stores it in the DataStorage.
     */

    @Test
    void onMessageValidCsvShouldStoreData() throws Exception {
        StubStorage storage = new StubStorage();
        WebSocketDataReader reader = new WebSocketDataReader(new URI("ws://localhost:0"));
        reader.dataStorage = storage;

        reader.onMessage("1,200,Label,3.14");

        List<String> calls = storage.received;
        assertEquals(1, calls.size());
        assertEquals("1,200,Label,3.14", calls.get(0));
    }
    /**
     * Test that the WebSocketDataReader ignores messages that are not in CSV format.
     */
    @Test
    void onMessageInvalidCsvShouldBeIgnored() throws Exception {
        StubStorage storage = new StubStorage();
        WebSocketDataReader reader = new WebSocketDataReader(new URI("ws://localhost:0"));
        reader.dataStorage = storage;

        reader.onMessage("bad,data");
        assertTrue(storage.received.isEmpty());
    }
}


package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.List;

class DataReaderTest {

    private Path tempFilePath;
    private DataStorage dataStorage;
    private DataReader dataReader;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        // Simulated output from HealthDataSimulator
        tempFilePath = tempDir.resolve("simulator_output.txt");

        Files.write(tempFilePath, List.of(
                "1001,98.0,Saturation,1714376000000",
                "1001,85.0,HeartRate,1714376060000",
                "1001,190.0,SystolicBP,1714376120000"
        ));

        dataStorage = new DataStorage() {};
        dataReader = new FileDataReader(tempFilePath.toString()); // You'll implement this
    }

    @Test
    void testReadDataStoresCorrectNumberOfRecords() throws IOException {
        dataReader.readData(dataStorage);
        List<PatientRecord> records = dataStorage.getRecords(1001, 1714375999000L, 1714376121000L);
        assertEquals(3, records.size(), "Should store 3 records for patient 1001");
    }

    @Test
    void testReadDataParsesCorrectValues() throws IOException {
        dataReader.readData(dataStorage);
        List<PatientRecord> records = dataStorage.getRecords(1001, 1714375999000L, 1714376121000L);

        PatientRecord systolic = records.stream()
                .filter(r -> "SystolicBP".equals(r.getRecordType()))
                .findFirst()
                .orElse(null);

        assertNotNull(systolic);
        assertEquals(190.0, systolic.getMeasurementValue(), 0.01);
        assertEquals(1714376120000L, systolic.getTimestamp());
    }
}
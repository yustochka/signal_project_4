package data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @BeforeEach
    void setUp() {
        // Reset instance by creating an anonymous subclass to access the protected constructor
        DataStorage.setInstance(new DataStorage() {});
    }

    @Test
    void testSingletonInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2, "getInstance should always return the same object");
    }

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size(), "There should be 2 records for patient 1");
        assertEquals(100.0, records.get(0).getMeasurementValue(), "The first record should have value 100.0");
    }

    @Test
    void testGetAllPatients() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(2, 150.0, "HeartRate", 1714376789051L);
        assertEquals(2, storage.getAllPatients().size(), "There should be 2 patients in storage");
    }
}

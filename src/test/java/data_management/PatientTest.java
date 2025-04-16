package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient(1234);
        patient.addRecord(80.0, "HeartRate", 1714384800000L); // 10:00
        patient.addRecord(85.0, "HeartRate", 1714385100000L); // 10:05
        patient.addRecord(90.0, "HeartRate", 1714385400000L); // 10:10
    }

    @Test
    void testGetRecordsInTimeRange() {
        // Get records between 10:02 and 10:08
        List<PatientRecord> filtered = patient.getRecords(1714384920000L, 1714385280000L);
        assertEquals(1, filtered.size());
        assertEquals(85.0, filtered.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsAllMatching() {
        // Range includes all
        List<PatientRecord> filtered = patient.getRecords(1714384800000L, 1714389060000L);
        assertEquals(3, filtered.size());
    }

    @Test
    void testGetRecordsNoneMatching() {
        // Range with no records
        List<PatientRecord> filtered = patient.getRecords(1714389060000L, 1714389060000L);
        assertTrue(filtered.isEmpty());
    }
}

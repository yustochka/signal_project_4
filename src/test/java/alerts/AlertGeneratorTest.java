package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertGeneratorTest {
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        dataStorage = new InMemoryDataStorage();
        alertGenerator = new AlertGenerator(dataStorage);
    }

    @Test
    void testEvaluateData_IncreasingBloodPressureTrendAlert() {
        Patient patient = new Patient(1);
        dataStorage.addPatientData(1, 100, "SystolicBP", 1000);
        dataStorage.addPatientData(1, 115, "SystolicBP", 2000);
        dataStorage.addPatientData(1, 130, "SystolicBP", 3000);

        alertGenerator.evaluateData(patient);


        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Increasing Blood Pressure Trend", alerts.get(0).getCondition());;
    }
    @Test
    void testEvaluateData_DecreasingBloodPressureTrendAlert() {
        Patient patient = new Patient(7);
        dataStorage.addPatientData(7, 130, "SystolicBP", 1000);
        dataStorage.addPatientData(7, 115, "SystolicBP", 2000);
        dataStorage.addPatientData(7, 100, "SystolicBP", 3000);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Decreasing Blood Pressure Trend", alerts.get(0).getCondition());
    }

    @Test
    void testEvaluateData_BloodPressureCriticalThresholdAlert() {
        Patient patient = new Patient(2);
        dataStorage.addPatientData(2, 190, "SystolicBP", 1000);


        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Critical Systolic Blood Pressure Threshold", alerts.get(0).getCondition());
    }

    @Test
    void testEvaluateData_BloodSaturationLowAlert() {
        Patient patient = new Patient(3);
        dataStorage.addPatientData(3, 91, "Saturation", 1000); // Below threshold

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Low Blood Saturation", alerts.get(0).getCondition());
    }
    @Test
    void testEvaluateData_RapidSaturationDropAlert() {
        Patient patient = new Patient(6);
        dataStorage.addPatientData(6, 97, "Saturation", 1000);
        dataStorage.addPatientData(6, 91, "Saturation", 600000); // Drop of 6% within 10 minutes

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(2, alerts.size());
        assertEquals("Rapid Blood Saturation Drop", alerts.get(1).getCondition());
    }
    @Test
    void testEvaluateData_HypotensiveHypoxemiaAlert() {
        Patient patient = new Patient(4);
        dataStorage.addPatientData(4, 85, "SystolicBP", 1000);
        dataStorage.addPatientData(4, 91, "Saturation", 2000);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(3, alerts.size());
        assertEquals("Hypotensive Hypoxemia", alerts.get(2).getCondition());}

    @Test
    void testEvaluateData_ECGAbnormalDataAlert() {
        Patient patient = new Patient(5);
        dataStorage.addPatientData(5, 1.0, "ECG", 1000);
        dataStorage.addPatientData(5, 1.5, "ECG", 2000);
        dataStorage.addPatientData(5, 3.0, "ECG", 4000);
        alertGenerator.evaluateData(patient);


        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Abnormal ECG Data", alerts.get(0).getCondition());    }

    // In-memory implementation of DataStorage for testing
    static class InMemoryDataStorage extends DataStorage {
        private final List<PatientRecord> records = new ArrayList<>();

        @Override
        public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
            List<PatientRecord> filteredRecords = new ArrayList<>();
            for (PatientRecord record : records) {
                if (record.getPatientId() == patientId &&
                        record.getTimestamp() >= startTime &&
                        record.getTimestamp() <= endTime) {
                    filteredRecords.add(record);
                }
            }
            return filteredRecords;
        }

        public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
            records.add(new PatientRecord(patientId, measurementValue, recordType, timestamp));
        }
    }
}
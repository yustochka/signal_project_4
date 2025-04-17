package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method. This method should define the specific
     * conditions under which an alert will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), 0, System.currentTimeMillis());

        checkBloodPressureAlerts(patient.getPatientId(), records);
        checkBloodSaturationAlerts(patient.getPatientId(), records);
        checkHypotensiveHypoxemiaAlert(patient.getPatientId(), records);
        checkECGDataAlerts(patient.getPatientId(), records);
    }

    private void checkBloodPressureAlerts(int patientId, List<PatientRecord> records) {
        List<PatientRecord> bpRecords = records.stream()
                .filter(r -> r.getRecordType().equals("SystolicBP") || r.getRecordType().equals("DiastolicBP"))
                .collect(Collectors.toList());

        for (PatientRecord record : bpRecords) {
            double value = record.getMeasurementValue();
            if (record.getRecordType().equals("SystolicBP") && (value > 180 || value < 90)) {
                triggerAlert(new Alert(String.valueOf(patientId), "Critical Systolic Blood Pressure Threshold", System.currentTimeMillis()));
            } else if (record.getRecordType().equals("DiastolicBP") && (value > 120 || value < 60)) {
                triggerAlert(new Alert(String.valueOf(patientId), "Critical Diastolic Blood Pressure Threshold", System.currentTimeMillis()));
            }
        }

        for (int i = 2; i < bpRecords.size(); i++) {
            double prev1 = bpRecords.get(i - 2).getMeasurementValue();
            double prev2 = bpRecords.get(i - 1).getMeasurementValue();
            double current = bpRecords.get(i).getMeasurementValue();

            if (Math.abs(current - prev2) > 10 && Math.abs(prev2 - prev1) > 10) {
                if (current > prev2 && prev2 > prev1) {
                    triggerAlert(new Alert(String.valueOf(patientId), "Increasing Blood Pressure Trend", System.currentTimeMillis()));
                } else if (current < prev2 && prev2 < prev1) {
                    triggerAlert(new Alert(String.valueOf(patientId), "Decreasing Blood Pressure Trend", System.currentTimeMillis()));
                }
            }
        }
    }

    private void checkBloodSaturationAlerts(int patientId, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .collect(Collectors.toList());

        for (int i = 0; i < saturationRecords.size(); i++)
        {
            double current = saturationRecords.get(i).getMeasurementValue();

            if (current < 92)
            {
                triggerAlert(new Alert(String.valueOf(patientId), "Low Blood Saturation", System.currentTimeMillis()));
            }

            if (i > 0)
            {
                double previous = saturationRecords.get(i - 1).getMeasurementValue();
                long timeDiff = saturationRecords.get(i).getTimestamp() - saturationRecords.get(i - 1).getTimestamp();
                if (previous - current >= 5 && timeDiff <= 600000) // 10 minutes in milliseconds
                {
                    triggerAlert(new Alert(String.valueOf(patientId), "Rapid Blood Saturation Drop", System.currentTimeMillis()));
                }
            }
        }
    }

    private void checkHypotensiveHypoxemiaAlert(int patientId, List<PatientRecord> records) {
        boolean lowBP = records.stream()
                .filter(r -> r.getRecordType().equals("SystolicBP"))
                .anyMatch(r -> r.getMeasurementValue() < 90);

        boolean lowSaturation = records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .anyMatch(r -> r.getMeasurementValue() < 92);

        if (lowBP && lowSaturation) {
            triggerAlert(new Alert(String.valueOf(patientId), "Hypotensive Hypoxemia", System.currentTimeMillis()));
        }
    }

    private void checkECGDataAlerts(int patientId, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = records.stream()
                .filter(r -> r.getRecordType().equals("ECG"))
                .collect(Collectors.toList());

        double average = ecgRecords.stream()
                .mapToDouble(PatientRecord::getMeasurementValue)
                .average()
                .orElse(0);

        for (PatientRecord record : ecgRecords) {
            if (record.getMeasurementValue() > average * 1.5) {
                triggerAlert(new Alert(String.valueOf(patientId), "Abnormal ECG Data", System.currentTimeMillis()));
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        triggeredAlerts.add(alert);
    }
    /**
     * Retrieves the list of triggered alerts.
     *
     * @return a list of triggered alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

}
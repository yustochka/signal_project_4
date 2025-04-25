package com.alerts;

public class ECGAlertFactory extends AlertFactory
{
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "ECG Alert: " + condition, timestamp);
    }
}

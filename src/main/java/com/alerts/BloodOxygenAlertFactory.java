package com.alerts;

public class BloodOxygenAlertFactory extends AlertFactory
{
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "Blood Oxygen Alert: " + condition, timestamp);
    }
}

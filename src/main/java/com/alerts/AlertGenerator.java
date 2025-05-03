package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class AlertGenerator {
    private final DataStorage dataStorage;
    private final List<AlertStrategy> alertStrategies;
    private final AlertFactory alertFactory;
    private final List<Alert> triggeredAlerts = new ArrayList<>();

    public AlertGenerator(DataStorage dataStorage, List<AlertStrategy> alertStrategies, AlertFactory alertFactory) {
        this.dataStorage = dataStorage;
        this.alertStrategies = alertStrategies;
        this.alertFactory = alertFactory;
    }

    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), 0, System.currentTimeMillis());

        for (AlertStrategy strategy : alertStrategies) {
            if (strategy.checkAlert(records)) {
                Alert alert = alertFactory.createAlert(String.valueOf(patient.getPatientId()), strategy.getCondition(), System.currentTimeMillis());
                triggerAlert(alert);
            }
        }
    }

    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }
}
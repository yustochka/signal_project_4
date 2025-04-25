package com.alerts;

import com.data_management.PatientRecord;

import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(List<PatientRecord> records) {
        return records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .anyMatch(r -> r.getMeasurementValue() < 92);
    }
    public String getCondition() {
        return "Critical Oxygen Saturation Drop";
    }
}
package com.alerts;

import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class RapidSaturationDropStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .collect(Collectors.toList());

        for (int i = 1; i < saturationRecords.size(); i++) {
            double previous = saturationRecords.get(i - 1).getMeasurementValue();
            double current = saturationRecords.get(i).getMeasurementValue();
            long timeDiff = saturationRecords.get(i).getTimestamp() - saturationRecords.get(i - 1).getTimestamp();

            if (previous - current >= 5 && timeDiff <= 600000) { // 10 minutes in milliseconds
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCondition() {
        return "Rapid Blood Saturation Drop";
    }
}
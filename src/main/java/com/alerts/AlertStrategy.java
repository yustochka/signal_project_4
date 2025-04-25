package com.alerts;

import com.data_management.PatientRecord;

import java.util.List;

public interface AlertStrategy
{
    boolean checkAlert(List<PatientRecord> records);
    String getCondition();
}

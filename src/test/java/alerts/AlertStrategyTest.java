package alerts;

import com.alerts.AlertStrategy;
import com.alerts.BloodPressureStrategy;
import com.alerts.HeartRateStrategy;
import com.alerts.OxygenSaturationStrategy;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertStrategyTest {

    @Test
    void testBloodPressureStrategy() {
        AlertStrategy strategy = new BloodPressureStrategy();

        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 120, "SystolicBP", 1000),
                new PatientRecord(1, 190, "SystolicBP", 2000)
        );

        assertTrue(strategy.checkAlert(records));

        records = Arrays.asList(
                new PatientRecord(1, 120, "SystolicBP", 1000),
                new PatientRecord(1, 130, "SystolicBP", 2000)
        );

        assertFalse(strategy.checkAlert(records));
    }

    @Test
    void testHeartRateStrategy() {
        AlertStrategy strategy = new HeartRateStrategy();

        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 50, "HeartRate", 1000),
                new PatientRecord(1, 70, "HeartRate", 2000)
        );

        assertTrue(strategy.checkAlert(records));

        records = Arrays.asList(
                new PatientRecord(1, 65, "HeartRate", 1000),
                new PatientRecord(1, 75, "HeartRate", 2000)
        );

        assertFalse(strategy.checkAlert(records));
    }

    @Test
    void testOxygenSaturationStrategy() {
        AlertStrategy strategy = new OxygenSaturationStrategy();

        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 91, "Saturation", 1000),
                new PatientRecord(1, 95, "Saturation", 2000)
        );

        assertTrue(strategy.checkAlert(records));

        records = Arrays.asList(
                new PatientRecord(1, 93, "Saturation", 1000),
                new PatientRecord(1, 96, "Saturation", 2000)
        );

        assertFalse(strategy.checkAlert(records));
    }
}
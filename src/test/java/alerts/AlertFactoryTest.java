package alerts;

import com.alerts.Alert;
import com.alerts.AlertFactory;
import com.alerts.BloodPressureAlertFactory;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.ECGAlertFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Critical Systolic Blood Pressure Threshold", 1000);

        assertEquals("Blood Pressure Alert: Critical Systolic Blood Pressure Threshold", alert.getCondition());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Critical Oxygen Saturation Drop", 2000);

        assertEquals("Blood Oxygen Alert: Critical Oxygen Saturation Drop", alert.getCondition());
    }

    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal ECG Data", 3000);

        assertEquals("ECG Alert: Abnormal ECG Data", alert.getCondition());
    }
}
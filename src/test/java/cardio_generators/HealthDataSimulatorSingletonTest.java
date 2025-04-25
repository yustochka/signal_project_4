package cardio_generators;


import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthDataSimulatorSingletonTest {

    @Test
    void testSingletonInstance() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        assertSame(instance1, instance2, "getInstance should always return the same HealthDataSimulator instance");
    }
}
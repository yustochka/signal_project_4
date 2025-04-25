package alerts;

import com.alerts.AlertInterface;
import com.alerts.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RepeatedAlertDecoratorTest {

    @Test
    void testGetMessage() {
        // Mock the AlertInterface
        AlertInterface mockAlert = mock(AlertInterface.class);
        when(mockAlert.getMessage()).thenReturn("Original Alert Message");

        // Create the decorator
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(mockAlert, 3, 1000);

        // Verify the message
        String expectedMessage = "[REPEATED 3x] Original Alert Message";
        assertEquals(expectedMessage, decorator.getMessage());
    }

    @Test
    void testCheckConditionRepeatedly() throws InterruptedException {
        // Mock the AlertInterface
        AlertInterface mockAlert = mock(AlertInterface.class);
        when(mockAlert.getCondition()).thenReturn("Condition A");

        // Create the decorator
        RepeatedAlertDecorator decorator = new RepeatedAlertDecorator(mockAlert, 3, 500);

        // Call the method
        decorator.checkConditionRepeatedly();

        // Wait for the task to complete
        Thread.sleep(2000);

        // Verify that the condition was checked 3 times
        verify(mockAlert, times(3)).getCondition();
    }
}
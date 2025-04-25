package alerts;

import com.alerts.AlertInterface;
import com.alerts.PriorityAlertDecorator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PriorityAlertDecoratorTest {

    @Test
    void testGetMessage() {
        // Mock the base alert
        AlertInterface mockAlert = mock(AlertInterface.class);
        when(mockAlert.getMessage()).thenReturn("Critical Heart Rate Detected");

        // Wrap it with the priority decorator
        PriorityAlertDecorator priorityAlert = new PriorityAlertDecorator(mockAlert, 2);

        // Verify the final message format
        String expected = "[PRIORITY: HIGH] Critical Heart Rate Detected";
        assertEquals(expected, priorityAlert.getMessage());

        // Verify the mock interaction
        verify(mockAlert, times(1)).getMessage();
    }

    @Test
    void testSetPriorityIndex() {
        // Mock the base alert
        AlertInterface mockAlert = mock(AlertInterface.class);
        when(mockAlert.getMessage()).thenReturn("Critical Heart Rate Detected");

        // Create the decorator
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(mockAlert, 1);

        // Update the priority index
        decorator.setPriorityIndex(3);

        // Verify the updated priority level
        assertEquals("CRITICAL", decorator.getPriorityLevel());

        // Verify the updated message
        String expected = "[PRIORITY: CRITICAL] Critical Heart Rate Detected";
        assertEquals(expected, decorator.getMessage());
    }
}
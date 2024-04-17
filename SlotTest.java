import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SlotTest {
    private Slot slot;

   @Test
public void testSetState() {
    Slot slot = new Slot(0, 0, "-", true);
    slot.addObserver((o, arg) -> {
        // Observer update logic here
    });
    slot.setState("2");
    assertEquals("2", slot.getState());
    // Additional assertions to check if observers were notified
}

@Test
public void testIsValidState() {
    assertTrue(Slot.isValidState("3"));
    assertTrue(Slot.isValidState("-"));
    assertFalse(Slot.isValidState("invalid"));
}

@Test
public void testStateChangeForUnfillableSlot() {
    Slot slot = new Slot(0, 0, "5", false);
    slot.setState("3");
    assertEquals("5", slot.getState()); // State should not change
}
 @BeforeEach
    void setUp() {
        // Initialize a new Slot for each test case
        slot = new Slot(1, 1, "5");
    }

}

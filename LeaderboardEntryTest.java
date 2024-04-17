import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.Duration;

public class LeaderboardEntryTest {

    @Test
    public void testConstructorWithValidValues() {
        // to test the constructor with valid values
        LeaderboardEntry entry = new LeaderboardEntry(3, Duration.ofMinutes(30));

        // To check if the values are set correctly
        assertEquals(3, entry.getWins());
        assertEquals(Duration.ofMinutes(30), entry.getBestTime());
    }

    @Test
    public void testConstructorWithNullBestTime() {
        // To test the constructor with null best time
        LeaderboardEntry entry = new LeaderboardEntry(5, null);

        // To check if the values are set correctly
        assertEquals(5, entry.getWins());
        assertNull(entry.getBestTime());
    }

    @Test
    public void testSetWins() {
        // To create an entry with initial wins
        LeaderboardEntry entry = new LeaderboardEntry(5, Duration.ofMinutes(45));

        // To set new wins
        entry.setWins(8);

        // To check if wins are updated
        assertEquals(8, entry.getWins());
    }

    @Test
    public void testSetBestTimeWithBetterTime() {
        // To create an entry with initial best time
        LeaderboardEntry entry = new LeaderboardEntry(2, Duration.ofMinutes(45));

        // To set a better best time
        entry.setBestTime(Duration.ofMinutes(35));

        // To check if best time is updated
        assertEquals(Duration.ofMinutes(35), entry.getBestTime());
    }

    @Test
    public void testSetBestTimeWithWorseTime() {
        // To create an entry with initial best time
        LeaderboardEntry entry = new LeaderboardEntry(2, Duration.ofMinutes(45));

        // To set a worse best time (should not update)
        entry.setBestTime(Duration.ofMinutes(50));

        // To check if best time is still the same
        assertEquals(Duration.ofMinutes(45), entry.getBestTime());
    }


}
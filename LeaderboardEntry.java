import java.time.Duration; // Import for representing time durations

/**
 * LeaderboardEntry represents a player's entry in the leaderboard, including their wins and best time.
 */
public class LeaderboardEntry {
    private int wins; // The number of wins for the player
    private Duration bestTime; // The best time achieved by the player

     /**
     * Constructor for LeaderboardEntry.
     * @param wins The number of wins for the player.
     * @param bestTime The best time achieved by the player (can be null).
     */
    public LeaderboardEntry(int wins, Duration bestTime) {
        this.wins = wins;
        this.bestTime = bestTime;
    }

    // Getters and setters
    /**
     * Get the number of wins for the player.
     * @return The number of wins.
     */
    public int getWins() {
        return wins;
    }

    /**
     * Set the number of wins for the player.
     * @param wins The number of wins to set.
     */
    public void setWins(int wins) {
        this.wins = wins;
    }
      
    /**
     * Get the best time achieved by the player.
     * @return The best time as a Duration (can be null).
     */
    public Duration getBestTime() {
        return bestTime;
    }

    /**
     * Set the best time achieved by the player.
     * If the provided best time is better than the current best time (or current best time is null),
     * it will update the best time.
     * @param bestTime The best time to set.
     */
    public void setBestTime(Duration bestTime) {
        if (this.bestTime == null || bestTime.compareTo(this.bestTime) < 0) {
            this.bestTime = bestTime;
        }
    }
}//end of LeaderboardEntry class

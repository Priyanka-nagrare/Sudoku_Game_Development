import java.util.Scanner;  // Import the Scanner class for reading input from the user.
import java.io.FileWriter;// Import the FileWriter class for writing to files.
import java.io.IOException; // Import the IOException class for handling input/output exceptions.
import java.io.File;  // Import the File class for working with files and directories.
import java.io.FileNotFoundException;// Import the FileNotFoundException class for handling file not found exceptions.
import java.util.Stack; // Import the Stack class for working with stacks
import java.io.PrintWriter; // Import the PrintWriter class for writing text to a file.
import java.time.LocalTime;// Import the LocalTime class for working with time.
import java.util.Map; // Import the Map interface for working with key-value mappings.
import java.time.Duration;// Import the Duration class for working with time durations.
import java.util.HashMap; // Import the HashMap class, which implements the Map interface, for key-value storage.

/**
 * This class provides a text based user interface for the player to interact with the game
 * @author Lauren Scott
 * @version student sample code
 */
public class UI {
    private Sudoku thegame;//this is the game model instance
    private String menuChoice;//this is the users choice from the menu
    private Scanner reader;//this scanner is used to read the terminal
    private String gameType;// Stores the type of the Sudoku game 4x4 or 9x9
    private int coins;// Stores the player's current number of coins.
    private String nickname;// Stores the player's nickname.
    private LocalTime startTime; // Stores the start time of the game.
    private Map<String, Duration> leaderboard = new HashMap<>(); // Keeps track of the leaderboard.

    // Stack to keep the move history for undo functionality
    private Stack<Move> moveHistory = new Stack<>();
    private Stack<Move> redoHistory = new Stack<>();

    // Inner class to hold the move details for undo functionality
/**
 * Inner class to hold the details of a move for undo functionality.
 */
    private class Move {
        int row;
        int col;
        String prevValue;
    
        public Move(int row, int col, String prevValue) {
            this.row = row;
            this.col = col;
            this.prevValue = prevValue;
        }
        
    }
    
/**
 * Constructor for the UI class, providing a text-based user interface for the Sudoku game.
 * Initializes the game, handles user interactions, and manages game state.
 */
     public UI() {
        reader = new Scanner(System.in);// Initialize the scanner for user input
        this.gameType = selectGameType();// Select the game type 4*4 or 9*9
        thegame = new Sudoku(gameType); // Initialize the Sudoku game with the selected type
        reader = new Scanner(System.in);// Re-initialize the scanner 
        
        // Display a welcome message and prompt for the player's nickname
        System.out.println("Hello welcome to SUDOKU,");
        System.out.print("Please enter your nickname: ");
        nickname = reader.nextLine().trim();

        // Load or create user data, including coins and game progress
        loadOrCreateUser();
        loadLeaderboard();
        menuChoice="";
            
        // Main game loop that continues until the player quits or wins
        while(!menuChoice.equalsIgnoreCase("Q")&&!thegame.checkWin()) {
            displayGame();// Display the current game state
            menu();// Display the game menu
            menuChoice = getChoice();// Get the player's menu choice
        }
            // Check if the player has won the game and display a winning message if so
        if (thegame.checkWin()) {
            winningAnnouncement();
        }
    }

/**
 * Loads or creates user data, including coins and game progress, based on the user's nickname.
 * If a user file exists for the nickname, it loads the user's data. If not, it creates a new user file
 * with an initial coin bonus for new users.
 */
    private void loadOrCreateUser() {
            // Create a file with the user's nickname as the filename
        File userFile = new File(nickname + ".txt");
        if (userFile.exists()) {
                    // If the user file exists, load the user's data from it
            try (Scanner fileScanner = new Scanner(userFile)) {
                coins = fileScanner.nextInt();
                System.out.println("Welcome back, " + nickname + "! You have " + coins + " coins.");
            } catch (FileNotFoundException e) {
                    // Handle the case where the file is not found
                System.out.println("Error reading user file.");
                e.printStackTrace();
            }
        } else {
            // If the user file doesn't exist, create a new user file with a coin bonus for new users
            try (PrintWriter out = new PrintWriter(userFile)) {
                coins = 100; // New user bonus
                out.println(coins);
                System.out.println("As a new user, you get a bonus of 100 coins!");
            } catch (FileNotFoundException e) {
                System.out.println("Error creating user file.");
                e.printStackTrace();
            }
        }
    }
    
/**
 * Prompts the user to select the game type (4x4 or 9x9) and returns the selected game type as a string.
 *
 * @return The selected game type as a string ("4x4" or "9x9").
 */
    private String selectGameType() {
    System.out.println("Select Game Type:");
    System.out.println("1: 4x4 Game");
    System.out.println("2: 9x9 Game");
    System.out.print("Enter choice (1 or 2): ");
    int choice = reader.nextInt();
    // Check the user's choice and return the corresponding game type
    if (choice==1) {
        return "4x4";
    } else {
        return "9x9";
    }
    
}
    
/**
 * This method outputs a congratulatory announcement when the user has successfully solved the puzzle.
 * This method calculates the time taken to solve the puzzle and displays it in the announcement.
 */
    private void winningAnnouncement() {
        // Calculate the time taken to solve the puzzle
        Duration timeTaken = Duration.between(startTime, LocalTime.now());
        System.out.println("Congratulations, you solved the puzzle in " + timeTaken.toMinutes() + " minutes.");
        updateLeaderboard(timeTaken);  // Update the leaderboard with the user's winning time
    }

/**
 * Updates the leaderboard with the user's winning time and saves it to a file.
 * This method adds the user's nickname and their winning time to the leaderboard, then saves the updated leaderboard to a file.
 * @param timeTaken The duration of time it took for the user to solve the puzzle and win the game.
 */
    private void updateLeaderboard(Duration timeTaken) {
        // Add the user's nickname and winning time to the leaderboard
        leaderboard.put(nickname, timeTaken);
        saveLeaderboard();
        System.out.println("Your time has been recorded in the leaderboard.");
    }
    
/**
 * Loads the leaderboard data.
 * Note: The logic for loading the leaderboard has been updated in the Sudoku GUI file.
 */
    private void loadLeaderboard() {
        // Logic to load leaderboard has been updated in the sudoku GUI file
    }
    
/**
 * Saves the leaderboard data.
 * Note: The logic for saving the leaderboard has been updated in the Sudoku GUI file.
 */
    private void saveLeaderboard() {
        // Logic to save leaderboard has been updated in the sudoku GUI file
    }
    
/**
 * Displays the current state of the Sudoku game to the user.
 * This method prints the game grid to the console, showing the numbers in each cell and separating rows and columns with appropriate formatting.
 * Depending on the game size (4x4 or 9x9), the grid layout and formatting will differ.
 */
    public void displayGame() {
    // Check the game size (4x4 or 9x9) to determine the grid layout
        if (thegame.getGameSize() == 9) {
                    // Display column numbers for a 9x9 game
            System.out.println("Col   0 1 2 3 4 5 6 7 8");
            System.out.println("      - - - - - - - - -");
        } else {
                    // Display column numbers for a 4x4 game
            System.out.println("Col   0 1 2 3 ");
            System.out.println("      - - - - ");
        }
    // Iterate through rows and columns to display the game grid
        for (int i = 0; i < thegame.getGameSize(); i++) {
            System.out.print("Row "+i+"|");
            for (int c = 0; c < thegame.getGameSize(); c++) {
                if (thegame.getGameSize() == 9) {
                    if (c == 2 || c == 5 || c == 8) {
                        // formatting for columns in a 9x9 game
                        if (thegame.getIndividualMove(i,c).contains("-") ){
                            System.out.print(" " + "|");
                        } else{
                            System.out.print(thegame.getIndividualMove(i,c) + "|");
                        }
                    } else {
                        // formatting for columns in a 4x4 game
                        if (thegame.getIndividualMove(i,c).contains("-") ){
                            System.out.print(" " + ".");
                        } else{
                            System.out.print(thegame.getIndividualMove(i,c) + ".");
                        }
                    }

                } else if (thegame.getGameSize() == 4) {
                    if (c == 1 || c == 3) {
                        if (thegame.getIndividualMove(i,c).contains("-") ){
                            System.out.print(" " + "|");
                        } else{
                            System.out.print(thegame.getIndividualMove(i,c) + "|");
                        }
                    } else {
                        if (thegame.getIndividualMove(i,c).contains("-") ){
                            System.out.print(" " + ".");
                        } else{
                            System.out.print(thegame.getIndividualMove(i,c) + ".");
                        }
                    }

                
                }
                // Add horizontal separators based on the game size and current row
            }if (thegame.getGameSize() == 9 && (i == 2 || i == 5|| i == 8)) {
                System.out.println("\n      - - - - - - - - -");

            } else if (thegame.getGameSize() == 9 ){
                System.out.println("\n      .................");

            } else if (thegame.getGameSize() == 4 && (i==1||i==3) ){
                System.out.println("\n      - - - - ");

            } else {
                System.out.println("\n     .........");
            }
        }
    }

/**
 * Displays the menu of options to the user.
 * The menu includes various options such as starting a new game, making a move, saving and loading the game, undoing and redoing moves, clearing the game, and quitting the game.
 * Each option is represented by a letter enclosed in square brackets (e.g., [G] for starting a new game).
 */
    public void menu() {
    System.out.println("Please select an option: \n"
        + "[G] start game\n" // New option to start game
        + "[M] make move\n"
        + "[S] save game\n"
        + "[L] load saved game\n"
        + "[U] undo move\n"
        + "[R] redo move\n" // Add redo option
        + "[C] clear game\n"
        + "[Q] quit game\n");
}

/**
 * Gets the user's choice from the menu and performs the corresponding actions.
 * The method reads the user's choice from the input and handles various game-related actions based on the choice.
 * Actions include starting a new game, making a move, undoing and redoing moves, saving and loading the game, clearing the game, and quitting the game.
 * 
 * @return The choice the user has selected.
 */
    public String getChoice() {
    String choice = reader.next();
    if (choice.equalsIgnoreCase("G")) {
        startGame();
    }

    if (choice.equalsIgnoreCase("M")) {
                // Code for making a move
        System.out.print("Which row is the cell you wish to fill? ");
        int row = Integer.parseInt(reader.next());
        System.out.print("Which column is the cell you wish to fill? ");
        int col = Integer.parseInt(reader.next());

        // Range validation
        int maxNumber = gameType.equals("4x4") ? 4 : 9;
        int number;
        do {
            System.out.print("Which number do you want to enter (1-" + maxNumber + ")? ");
            while (!reader.hasNextInt()) {
                System.out.println("Please enter a valid number!");
                reader.next(); 
            }
            number = reader.nextInt();
            if (number < 1 || number > maxNumber) {
                System.out.println("Please enter a valid number between 1 and " + maxNumber + ".");
            }
        } while (number < 1 || number > maxNumber);

        String numStr = Integer.toString(number);

        // Sudoku rules validation and move
        String currentState = thegame.getIndividualMove(row, col);
        if (isValidMove(row, col, numStr) && thegame.makeMove(Integer.toString(row), Integer.toString(col), numStr)) {
            moveHistory.push(new Move(row, col, currentState));
        } else {
            System.out.println("That move is not valid according to Sudoku rules.");
        }
    } else if (choice.equalsIgnoreCase("U")) {
        undoMove();
    } else if (choice.equalsIgnoreCase("R")) {
        redoMove();
    } else if (choice.equalsIgnoreCase("S")) {
        saveGame();
    } else if (choice.equalsIgnoreCase("L")) {
        loadGame();
    } else if (choice.equalsIgnoreCase("C")) {
        clearGame();
    } else if (choice.equalsIgnoreCase("Q")) {
        System.out.println("Quitting the game.");
        System.exit(0);
    }
    return choice;
}

/**
 * Checks if a move is valid according to Sudoku rules.
 *
 * @param row    The row of the move.
 * @param col    The column of the move.
 * @param number The number to be placed in the cell.
 * @return True if the move is valid, false otherwise.
 */
private boolean isValidMove(int row, int col, String number) {
    // Check row
    for (int i = 0; i < thegame.getGameSize(); i++) {
        if (thegame.getIndividualMove(row, i).equals(number)) {
            return false;// Found the same number in the same row
        }
    }

    // Check column
    for (int i = 0; i < thegame.getGameSize(); i++) {
        if (thegame.getIndividualMove(i, col).equals(number)) {
            return false;// Found the same number in the same column
        }
    }

    // Check block
    int blockSize = (int)Math.sqrt(thegame.getGameSize());
    int blockRowStart = row - row % blockSize;
    int blockColStart = col - col % blockSize;

    for (int i = blockRowStart; i < blockRowStart + blockSize; i++) {
        for (int j = blockColStart; j < blockColStart + blockSize; j++) {
            if (thegame.getIndividualMove(i, j).equals(number)) {
                return false; // Found the same number in the same block
            }
        }
    }

    return true;// Move is valid
}

/**
 * Starts the game and initializes the timer.
 */
private void startGame() {
        System.out.println("Game started! Timer is running.");
        startTime = LocalTime.now();
    }
    
/**
 * Saves the current state of the game to a file.
 */
    public void saveGame() {
        try {
        FileWriter writer = new FileWriter("sudoku_save.txt");
        for (int i = 0; i < thegame.getGameSize(); i++) {
            for (int j = 0; j < thegame.getGameSize(); j++) {
                writer.write(thegame.getIndividualMove(i, j) + " ");// Write the value of each cell to the file
            }
            writer.write("\n");// Add a newline character to separate rows
        }
        writer.close();
        System.out.println("Game saved successfully!");
    } catch (IOException e) {
        System.out.println("An error occurred while saving the game.");
        e.printStackTrace();
    }
}  
    
/**
 * This will undo the previous move made by the player, restoring the previous state of the game board.
 */
     public void undoMove() {
    if (!moveHistory.isEmpty()) {
        Move lastMove = moveHistory.pop();
        redoHistory.push(new Move(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col)));
        thegame.makeMove(Integer.toString(lastMove.row), Integer.toString(lastMove.col), lastMove.prevValue);
        System.out.println("Move undone.");
    } else {
        System.out.println("No moves to undo.");
    }
}

/**
 * redoMove 
 * This method should redo the previously undone move in the game.
 */
public void redoMove() {
    if (!redoHistory.isEmpty()) {
        Move lastMove = redoHistory.pop();
        // Use the prevValue from the lastMove to update the game
        thegame.makeMove(Integer.toString(lastMove.row), Integer.toString(lastMove.col), lastMove.prevValue);
        // Push this move back onto the moveHistory stack
        moveHistory.push(new Move(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col)));
        System.out.println("Move redone.");
    } else {
        System.out.println("No moves to redo.");
    }
}

/**
* loadGame
* To be implemented by student - this method should load a previous saved game
*/
    public void loadGame() {
        try {
        File file = new File("sudoku_save.txt");
        Scanner fileReader = new Scanner(file);
        thegame = new Sudoku(gameType); // Resetting the game to the initial state
        while (fileReader.hasNextLine()) {
            for (int i = 0; i < thegame.getGameSize(); i++) {
                String[] line = fileReader.nextLine().trim().split(" ");
                for (int j = 0; j < thegame.getGameSize(); j++) {
                    if (!line[j].equals("-") && thegame.getMoves()[i][j].getFillable()) {
                        thegame.makeMove(Integer.toString(i), Integer.toString(j), line[j]);
                    }
                }
            }
        }
        fileReader.close();
        System.out.println("Game loaded successfully!");
    } catch (FileNotFoundException e) {
        System.out.println("Saved game file not found.");
    } catch (Exception e) {
        System.out.println("An error occurred while loading the game.");
        e.printStackTrace();
    }
    }

/**
 * Clears the game board and resets any records of moves, effectively resetting the game to its initial state.
 */
    public void clearGame() {
        thegame = new Sudoku(gameType);
        System.out.println("Game has been resetted");

    }

/**
 * Provides a hint to the user if they have enough coins to cover the hint cost.
 * If the user has enough coins, deducts the hint cost from their coins and provides a hint.
 * If the user doesn't have enough coins, informs them that there are not enough coins for a hint.
 */
    public void provideHint() {
        int hintCost = 100; // Cost of a hint
        if (coins >= hintCost) {
            coins -= hintCost;
            saveCoins();
            // Provides the hint to the user
            System.out.println("Providing a hint. It costs you " + hintCost + " coins.");
        } else {
            System.out.println("Not enough coins for a hint.");
        }
    }
    
/**
 * Saves the user's current coin balance to a file named after their nickname.
 * In case of an error while saving, prints an error message and stack trace.
 */
    private void saveCoins() {
        try (PrintWriter out = new PrintWriter(nickname + ".txt")) {
            out.println(coins);
        } catch (FileNotFoundException e) {
            System.out.println("Error saving coins.");
            e.printStackTrace();
        }
    }
    
/**
 * Awards 50 coins to the user as a reward for solving a puzzle and saves the updated coin balance to a file.
 * Prints a congratulatory message to inform the user about their reward.
 */
public void rewardCoinsForSolving() {
        coins += 50; // Reward for solving a puzzle
        saveCoins();
        System.out.println("Congratulations! You earned 50 coins for solving the puzzle.");
    }
    
/**
* The main method within the Java application. 
* It's the core method of the program and calls all others.
*/
    public static void main(String args[]) {
        UI thisUI = new UI();
    }
}//end of class UI
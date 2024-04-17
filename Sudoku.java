import java.io.File; //This package will help in handling the file operations,it defines interfaces and classes for the Java virtual machine to access files, file attributes, and file systems (Tutorialspoint.com, 2024). 
import java.io.FileNotFoundException;//  when a user attempts to open a file with the specified pathname that does not exist
import java.util.Scanner;// This is used for reading text from a file.
import java.util.Observer; // This is used to implement the observer-observable design pattern.
/**
* Sudoku class provides the functionality for this Sudoku game.
* It uses {@link java.io.File} to handle game level and solution files,
* {@link java.io.FileNotFoundException} to manage the errors during file reading,
* {@link java.util.Scanner} to read the game data from files,
* and {@link java.util.Observer} for observing changes in game state.
*
* This is the Sudoku class and it handles the functionality of the main game.
* @author Lauren Scott
* @version Student Sample Code
*/
public class Sudoku {
    private String[][] solution;//This array stores the solution to the game
    private Slot[][] populatedBoard;//This is the board of moves for the game
    private Scanner reader;//This scanner is used to read the game and level files
    private int gameSize;    //This will be the size of the game
    private String level;//This is the level file,changable for 4*4 and 9*9
/**
* This will Construct a new Sudoku game. It initializes the game board and loads the game solution from files.
* The game type determines whether a 4x4 or 9x9 Sudoku game is initialized. Based on the game type, 
* it sets the appropriate level file path. The constructor attempts to read the level file and 
* the corresponding solution file, initializes the game board with slots, and loads the winning solution.
*
* @param gameType The type of Sudoku game to initialize, either "4x4" for a smaller game or any other string for the standard 9x9 game.
* @throws FileNotFoundException if the level file corresponding to the specified game type is not found.
*/
    public Sudoku(String gameType) {
        // This is to the level file based on game type
        if ("4x4".equals(gameType)) {
            this.level = "Levels/esu1.txt"; // Path to 4x4 game file
        } else {
            // Default to 9x9 game
            this.level = "Levels/su1.txt"; // Path to 9x9 game file
        }

        try {
            //The scanner will read from the selected level file
            reader = new Scanner(new File(level));
        } catch (FileNotFoundException e) {
            //It will printthe stack trace to console if the file is not found
            e.printStackTrace();
        }
            // Calculate the size of the game (either 4x4 or 9x9) based on the level file's first entry.
        gameSize = calculateGameSize();
        
        //to initialize he solution and populated array with the calculated game size
        solution = new String[gameSize][gameSize];
        populatedBoard = new Slot[gameSize][gameSize];
        //It will read the level file to set the initial state of the sudoku board
        readLevelFile();
        // It will load the winning solution from the solution file
        loadWinSolution();
}
    
/**
* Adds the specified observer to all slots in the Sudoku board. 
* This allows the observer to be notified of changes in the state of each slot, 
* The observer will be added to every slot in the populatedBoard array.
*
* @param observer The observer to be added to each slot. This should not be null.
*/    
public void addObserverToSlots(Observer observer) {
        // Iterate over each row of the Sudoku board.
        for (int row = 0; row < gameSize; row++) {
                    // Iterate over each column in the current row.
            for (int col = 0; col < gameSize; col++) {
            // Add the passed observer to the Slot at the current row and column.
            // This allows the observer to be notified of changes to the Slot's state.
                populatedBoard[row][col].addObserver(observer);
            }
        }
}
    
/**
 * Retrieves the current state of the Sudoku board. This method returns a two-dimensional array
 * of {@link Slot} objects, representing all the slots (or cells) in the Sudoku game.
 * Each {@link Slot} object contains information about its state, including the value it holds
 * and whether it is fillable or not.
 *
 * @return A two-dimensional array of {@link Slot} objects representing the current state of the Sudoku board.
 */
public Slot[][] getMoves() {
    // Returns the current state of the Sudoku board as a 2D array of Slot objects.
        return populatedBoard;
}

/**
* Retrieves the state of an individual cell on the Sudoku board. 
* This method is used to get the current value or state of a specific cell, 
* identified by its row and column indices. The state is typically a String 
* representing the number in the cell, or some other representation if the cell is empty or has a special state.
*
* @param row The row index of the cell whose state is to be retrieved. Row indices start at 0.
* @param col The column index of the cell whose state is to be retrieved. Column indices start at 0.
* @return The state of the cell at the specified row and column. This is typically the number in the cell as a String.
*/
    public String getIndividualMove(int row, int col) {
    // Returns the state of the Slot at the specified row and column.
        return populatedBoard[row][col].getState();
    }
    
/**
* Calculates the size of the Sudoku game by reading it from the level file. 
* This method is used to determine the dimensions of the Sudoku board. 
* The game size is read from the beginning of the level file and is expected to be 
* an integer representing the number of rows and columns in the Sudoku grid 
* (e.g., 9 for a standard 9x9 Sudoku puzzle).
*
* @return The size of the Sudoku puzzle as an integer, representing both the number of rows and columns.
* @throws NumberFormatException if the value read from the file is not a valid integer.
 */
    public int calculateGameSize() {
    // Reads and returns the next integer from the file, which represents the size of the Sudoku game.
        return Integer.parseInt(reader.next());
    }
    
/**
* Retrieves the size of the Sudoku game. This method provides access to the game size, 
* which is the dimension of the Sudoku grid. It returns an integer representing both 
* the number of rows and columns in the Sudoku puzzle. For example, in a standard 
* 9x9 Sudoku game, this method will return 9.
*
* @return The size of the Sudoku puzzle, indicating the number of rows and columns in the grid.
*/
    public int getGameSize() {
        // Return the size of the Sudoku game (4 for 4x4 grid and 9 for a 9x9 grid).
        return gameSize;
    }
    
/**
* Reads the level file and populates the Sudoku board with initial moves. This method iterates 
* through the contents of the level file, extracting information about the initial state of each cell 
* (or Slot) on the Sudoku board. For each cell, it reads the row and column indices, and the initial value 
* or move, and creates a {@link Slot} object with this information. These {@link Slot} objects are stored 
* in a two-dimensional array representing the Sudoku board.
*
* @return A two-dimensional array of {@link Slot} objects representing the initial state of the Sudoku board as defined in the level file.
* @throws NumberFormatException if the file contains non-integer values where integers are expected for row and column indices.
*/
    public Slot[][] readLevelFile() {
        // This is a Loop where it will go through the file as long as there are more cells to read.
        while (reader.hasNext()) {
             // It will read the row and column indices, and the initial move (value) for each cell.
            int row =Integer.parseInt(reader.next());
            int col =Integer.parseInt(reader.next());
            String move = reader.next();
            
            // It will create a new Slot object with the read values and set it at the corresponding position in the board.
            populatedBoard[row][col] = new Slot(col, row, move, false);
            
        }
            // It will return the fully populated board with initial moves.
        return populatedBoard;
    }
    
/**
* Loads the winning solution for the Sudoku game from a solution file. This method determines 
* the correct solution file based on the current level file's name. It then reads the solution file, 
*
* @throws FileNotFoundException if the solution file corresponding to the level file is not found.
* @throws NumberFormatException if the file contains non-integer values where integers are expected for row and column indices.
*/
    public void loadWinSolution() {
    // Use the level file name to determine the corresponding solution file
    String solutionFile;
    if (this.level.endsWith("esu1.txt")) {
        solutionFile = "Solutions/esu1solution.txt"; // Path to 4x4 solution file
    } else {
        solutionFile = "Solutions/su1solution.txt"; // Path to 9x9 solution file
    }

    Scanner reader = null;
    try {
         // To initialize a new Scanner to read from the solution file.
        reader = new Scanner(new File(solutionFile));
    } catch (FileNotFoundException e) {
        // To print an error trace if the solution file is not found.
        e.printStackTrace();
    }

        // Reads and stores the solution from the file into the 'solution' array.
    while (reader != null && reader.hasNext()) {
        // Parse each line of the solution file to get the row, column, and the correct move.
        int row = Integer.parseInt(reader.next());
        int col = Integer.parseInt(reader.next());
        String move = reader.next();
        // It will store the move in the corresponding location in the solution array.
        solution[row][col] = move;
    }
    // To close the scanner after reading the file.
    if (reader != null) {
        reader.close(); // Close the scanner
    }
}

/**
* Checks whether the current state of the Sudoku game matches the winning solution. 
* This method iterates through each cell (or Slot) of the Sudoku board and compares its current state 
* (the value it holds) with the corresponding value in the winning solution. The game is considered won 
* if all cells match their corresponding values in the solution array.
*
* @return {@code true} if the current state of the game matches the winning solution, indicating that the game has been won; {@code false} otherwise.
*/
    public Boolean checkWin(){
            // The Loop for it to go through each row of the Sudoku board.
        for (int i = 0; i<gameSize; i++) {
                    // The Loop for it to go through each column in the current row.
            for (int c = 0; c <gameSize; c++) {
                // To check if the current cell's state does not match the corresponding cell in the solution.
                if (!populatedBoard[i][c].getState().equals(solution[i][c])) {
                    // If any cell does not match, the game is not yet won.
                    return false;
                }
            }
        }
        // If all cells match their corresponding cells in the solution, the game is won.
        return true;
    }

/**
* This method allows a user to make a move in the game
* @param row - the row of the move
* @param col - the column of the move
* @param number - the number they are wishing to enter in the cell
* @return whether the move was valid
*/
    public Boolean makeMove(String row, String col, String number) {
            // This will convert the row and column strings to integers.
        int enteredRow = Integer.parseInt(row);
        int enteredCol = Integer.parseInt(col);
        
            // It will check if the cell at the entered row and column is fillable.
        if (populatedBoard[enteredRow][enteredCol].getFillable()) {
            // If the cell is fillable, it will set the cell's state to the specified number.
            populatedBoard[enteredRow][enteredCol].setState(number);
            // and it will return true indicating the move was successful.
            return true;
        } else {
            // If the cell is not fillable, it will return false indicating the move failed.
            return false;
        }
    }

}//end of class Sudoku
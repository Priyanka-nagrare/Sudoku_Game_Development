import java.util.Observable; // Importing Observable class for implementing the observer design pattern.

/**
 * This Represents a single slot (cell) in our Sudoku game. This class extends {@link Observable} to notify observers 
 * (such as a GUI component) when the state of the slot changes. Each slot maintains its current state, 
 * its position (row and column) on the board, and whether it is fillable (i.e., whether its state can be changed).
 * 
 * @author Lauren Scott
 * @version Student Sample Code
 */
public class Slot extends Observable{
    private String state;//The current state of the slot
    private int row, col;//The row and column number of the slot 
    private Boolean fillable;//whether that slot can be changed

/**
 * Constructor for the Slot class. This will create a slot and sets its position on the game board.
 * It further Initializes the slot with a specified number (state) and sets it as fillable, indicating 
 * that its state can be changed later in the game.
 *
 * @param col The column number where the slot is located on the Sudoku board.
 * @param row The row number where the slot is located on the Sudoku board.
 * @param number The initial number or state to be set in this slot.
 */
    public Slot(int col, int row, String number) {
        this.row = row;
        this.col = col;
        this.state = number;
        this.fillable = true;// This will initialize the slot as fillable.
    }

/**
* Constructor for the Slot class when importing the level file. 
* It will create a slot and denotes its position on the game board. The fillability of the slot 
* is determined based on the passed number: if the number does not contain a "-", 
* the slot is considered not fillable, implying it's a pre-filled cell in the puzzle.
*
* @param col The column number where the slot is located on the Sudoku board.
* @param row The row number where the slot is located on the Sudoku board.
* @param number The initial number or state to be set in this slot. If this does not contain a "-", the slot is not fillable.
*/
    public Slot (int col, int row, String number, Boolean fillable) {
        this.row = row;
        this.col = col;
        this.state = number;
        // To set the slot as fillable only if the number contains a "-"
        if (!number.contains("-") ){
            this.fillable = false;
        } else {
            this.fillable = true;
        }
    }
    
    // Method to set the state of the slot
/**
* This will set the state of this slot if the new state is valid and the slot is fillable.
* After setting the new state, it marks the Observable (Slot) as changed and notifies 
* all observers, typically triggering an update in the GUI or other observing components.
*
* @param newState The new state to be set for this slot. The method checks if this state is valid before setting it.
*/
    public void setState(String newState) {
    // To check if the new state is valid and the slot is fillable before setting the state.
   if (isValidState(newState) && fillable) { // Ensure the state is valid and the slot is fillable
    this.state = newState; // Set the new state.
    setChanged(); // Mark this Observable(slot) as having been changed
    notifyObservers(); // Notify all observers, this will call update() in SudokuGUI
    }
}
    
/**
* Retrieves the current state of the slot. The state represents the value 
* or number currently assigned to this slot in the Sudoku puzzle.
*
* @return The current state (value or number) of the slot.
*/
    public String getState(){
        return state; // Returns the current state of the slot.
    }
    
/**
* This checks whether the provided state is valid for a slot in the Sudoku game. 
* A valid state is either a numeric string representing a Sudoku number or a string containing a "-" 
* to represent an empty or fillable slot.
*
* @param state The state to be checked for validity.
* @return {@code true} if the state is valid (either a number or "-"); {@code false} otherwise.
*/
    public static boolean isValidState(String state) {
    //To check if the state is not a fillable indicator.
        if (!state.contains("-") ){
        try {
            // Attempts to parse the state as an integer. If successful, the state is valid (a number).
            Integer.parseInt(state);
        } catch (Exception e) {
            // If parsing fails, the state is invalid (not a number).
            return false;            
        }
        
    }   
    // Returns true if the state is either a number or "-".
    return true;
    }  
    
/**
* This determines whether the current slot is fillable by the user. In a Sudoku game, 
* some slots are pre-filled with numbers and cannot be changed, while others are empty 
* and allows user input. This method checks the fillability of this slot.
*
* @return {@code true} if the slot is fillable (empty or allows user input); {@code false} otherwise.
*/
    public Boolean getFillable() {
        return fillable;  // Returns the fillable status of the slot.
    }
     // Getter for row
/**
* Retrieves the row number of this slot on the Sudoku board.
*
* @return The row number of the slot.
*/
     public int getRow() {
        return row; // Returns the row position of this slot.
    }

    // Getter for col
/**
* Retrieves the column number of this slot on the Sudoku board.
*
* @return The column number of the slot.
*/
    public int getCol() {
        return col; // Returns the column position of this slot.
    }
}//End of class Slot


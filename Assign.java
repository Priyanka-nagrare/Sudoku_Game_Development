/**
 * Assign
 * This class handles the creation of all moves in the game
 * @author Lauren Scott
 * @version Student Sample Code
 */
public class Assign {
    private int col, row;//The row and column being assigned
    private Sudoku game;//The game 
    Slot[][] moves;//2D Array to store the game's moves
    /**
     * Constructor for Assign class.
     * This gets the total number of moves and calls methods to determine the row that will be filled,
     * and to set the state of the slot being assigned.
     *
     * @param game    - the game
     * @param col     - the column the user has selected
     * @param player  - a Boolean value that determines whether it is a player/computer move
     */
    public Assign(Sudoku game, int row,int col, String number){
        this.game = game;
        this.col = col;
        this.row = row;
        assignMove(number);
    }
    
    /**
     * assignMove
     * This method assigns the move to the game.
     *
     * @param number The number to be assigned to the slot.
     */
    public void assignMove(String number) {
        // Set the state of the slot at the specified row and column to the provided number.
        moves[row][col].setState(number);
    }
    
    /**
     * getRow
     * This method returns the current row value for this move. It allows another element of the program to access this move's current row.
     * @return the row value
     */
    public int getRow() {
            // Return the current row value for this move.
        return row;
    }
    public int getCol() {
            // Return the current row value for this move.
        return row;
    }
}//End of class Assign

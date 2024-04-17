import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observer;
import static org.junit.jupiter.api.Assertions.*;

public class SudokuTest {
    private Sudoku sudoku;

    @BeforeEach
    void setUp() {
        // Initialize a new Sudoku game for each test case
        sudoku = new Sudoku("4x4");
    }

    @Test
    void testAddObserverToSlots() {
        // Create a mock Observer
        Observer mockObserver = new MockObserver();

        // Add the observer to slots
        sudoku.addObserverToSlots(mockObserver);

        // Assert that the observer was added to all slots
        Slot[][] slots = sudoku.getMoves();
        for (Slot[] row : slots) {
            for (Slot slot : row) {
                assertTrue(slot.countObservers() > 0);
            }
        }
    }

    @Test
    void testGetMoves() {
        Slot[][] moves = sudoku.getMoves();
        assertNotNull(moves);
        assertEquals(4, moves.length); // For a 4x4 game
        assertEquals(4, moves[0].length); // For a 4x4 game
    }

    @Test
    void testGetIndividualMove() {
        String move = sudoku.getIndividualMove(0, 0);
        assertNotNull(move);
    }


    @Test
    void testGetGameSize() {
        int gameSize = sudoku.getGameSize();
        assertEquals(4, gameSize); // For a 4x4 game
    }

    @Test
    void testReadLevelFile() {
        Slot[][] moves = sudoku.readLevelFile();
        assertNotNull(moves);
        assertEquals(4, moves.length); // For a 4x4 game
        assertEquals(4, moves[0].length); // For a 4x4 game
    }

@Test
public void testSudokuInitialization() {
    Sudoku sudoku = new Sudoku("9x9");
    assertNotNull(sudoku.getMoves());
    assertEquals(9, sudoku.getGameSize());
    // Add more assertions for other initial states like solution array, populated board, etc.
}


@Test
public void testCheckWin() {
    Sudoku sudoku = new Sudoku("9x9");
    // Set up the board in a winning state
    // Assert that checkWin returns true
    // Also, test for non-winning states
}

    // MockObserver class for testing addObserverToSlots
    private static class MockObserver implements Observer {
        @Override
        public void update(java.util.Observable o, Object arg) {
            // Do nothing for mock observer
        }
    }
}

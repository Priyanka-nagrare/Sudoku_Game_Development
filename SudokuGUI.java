import javax.swing.*;// Importing the swing components for building the graphical user interface for the Sudoku game.
import java.awt.*;// Importing AWT (Abstract Window Toolkit) for basic GUI operations like handling events and layouts.
import java.awt.event.ActionEvent;// Importing ActionEvent class for handling action events like button clicks.
import java.awt.event.ActionListener; // Importing ActionListener interface for receiving action events.
import java.util.Stack; // Importing Stack class for using stack data structures.
import java.io.IOException; // Importing IOException class for handling exceptions produced by failed or interrupted I/O operations.
import java.io.FileWriter; // Importing FileWriter for writing to files.
import java.io.File; // Importing File class to represent file and directory pathnames.
import java.io.PrintWriter; // Importing PrintWriter for printing formatted representations of objects to a text-output stream.
import java.util.Scanner; // Importing Scanner class for reading input (like files).
import java.io.FileNotFoundException; // Importing FileNotFoundException for handling cases where a file is not found.
import java.time.LocalTime; // Importing LocalTime class for representing time without a date.
import java.util.Map;  // Importing Map interface for using collections that map keys to values.
import java.time.Duration;  // Importing Duration class for handling time-based amount of time.
import java.util.HashMap; // Importing HashMap for using hash table based implementation of the Map interface.
import java.util.Observer; // Importing Observer interface for implementing the observer side of the Observer design pattern.
import java.util.Observable; // Importing Observable class for creating objects that can be observed.

/**
 * A graphical user interface (GUI) for playing Sudoku. This class implements the Observer interface, allowing it
 * to be notified of changes in the game state. It provides a visual representation of the Sudoku game, including
 * the game grid, control buttons, a timer, and various other interactive elements.
 *
 * Features include starting a new game, selecting themes, viewing leaderboards, getting hints, undoing/redoing moves,
 * and tracking game time.
 */
public class SudokuGUI implements Observer {
    private Sudoku thegame; // The Sudoku game logic this GUI interacts with.
    private JFrame frame; // The main window for the Sudoku game.
    private JButton[][] gridButtons; // Buttons representing each cell of the Sudoku grid.
    private JPanel gridPanel; // Panel to hold the Sudoku grid.
    private String gameType; // Stores the type of Sudoku game 4x4, 9x9).
    private JLabel coinLabel; // Label to display the number of coins.
    private int coins; // The number of coins the player has.
    private String nickname;  // The player's nickname.
    private JLabel nameLabel; // Label to display the player's nickname.
    private JComboBox<String> themeSelector; // Dropdown for selecting the game theme.
    private static final String[] THEMES = {"Classic", "Light Mode", "Dark Mode", "Pastel", "High Contrast", "Nature", "Ocean", "Seasonal", "Material Design", "Retro/Vintage"}; // Array of available theme options.
    private JButton btnStartGame; // Button to start a new game.
    private JLabel timerLabel; // Label to display the game timer.
    private LocalTime startTime; // Time when the current game started.
    private Timer gameTimer; // Timer for tracking game duration.
    private Map<String, LeaderboardEntry> leaderboard = new HashMap<>(); // Leaderboard data.
    private int[][] hintCounts; // Stores the number of hints used in each cell.
    private JButton btnHelp; // Button for help or hints.
    private JButton btnInstructions; // Button to show game instructions.
    private boolean gameStarted = false; // Flag to check if a game has started.
    
    private Stack<Move> moveHistory = new Stack<>(); // History of moves for undo functionality.
    private Stack<Move> redoHistory = new Stack<>(); // History of undone moves for redo functionality.

 // Inner class to hold the move details for undo functionality
/**
 * Represents a single move made in the Sudoku game. This class stores the row and column of the move, 
 * as well as the previous value of the cell before the move was made. It is used to facilitate undoing moves,
 * allowing the game state to be reverted to a previous state.
 */
 private class Move {
    int row; // The row number of the cell where the move was made.
    int col; // The column number of the cell where the move was made.
    String prevValue; // The value of the cell before the move.

    /**
     * Constructs a Move object to represent a single Sudoku move.
     *
     * @param row The row number of the cell.
     * @param col The column number of the cell.
     * @param prevValue The previous value of the cell before the move.
     */
    public Move(int row, int col, String prevValue) {
        this.row = row;
        this.col = col;
        this.prevValue = prevValue;
    }
}
   
/**
 * Constructs a new SudokuGUI instance. This constructor initiates several key steps:
 * 1. It prompts the user to select the game type (4x4 or 9x9 Sudoku).
 * 2. It requests the player's nickname for personalized experience and leaderboard tracking.
 * 3. It loads or creates user data, setting up the player's profile.
 * 4. It loads the leaderboard data to display past high scores.
 * 5. It initializes the Sudoku game logic with the selected game type and adds this GUI as an observer to the game slots.
 * 6. It sets up and displays the GUI components and initializes the hint counts array based on the game size.
 *
 * The GUI provides an interactive interface for playing the Sudoku game, including features like hints, undo/redo moves,
 * and a timer, enhancing the gameplay experience.
 */    
public SudokuGUI() {
        this.gameType = selectGameType(); // Prompt for game type
        nicknamePrompt(); // Prompt the user for a nickname.
        loadOrCreateUser(); // Load existing user data or create a new user.
        loadLeaderboard(); // Load leaderboard data
        thegame = new Sudoku(gameType); // Initialize the game with selected game type
        thegame.addObserverToSlots(this); // Add this GUI as an observer to all Slots
        createAndShowGUI();     // Set up and display the GUI components.
            // Initializes the hint counts array based on the size of the game.
        hintCounts = new int[thegame.getGameSize()][thegame.getGameSize()];
    }

    // Method to prompt user to select the game type
/**
 * Prompts the user to select the game type for Sudoku. The options presented are either a 4x4 game
 * or a 9x9 game. This method displays a dialog box with these options and returns the user's choice.
 *
 * @return A string representing the selected game type, either "4x4" or "9x9".
 */
    private String selectGameType() {
            // To define the options for game types.
        Object[] options = {"4x4 Game", "9x9 Game"};
            // It will show a dialog box to the user with the game type options.
        int choice = JOptionPane.showOptionDialog(null, 
                "Choose the Game Type",  // Dialog title.
                "Game Type Selection",// Dialog message.
                JOptionPane.DEFAULT_OPTION,  // Type of options.
                JOptionPane.INFORMATION_MESSAGE, // Type of message to be displayed.
                null, options, options[0]); // Array of options and the default selection.
            
        // Return "4x4" if the first option is chosen, otherwise return "9x9".
                return choice == 0 ? "4x4" : "9x9";
    }
    
     // Method to prompt for nickname and load or initialize user data
/**
 * Prompts the user to enter a nickname for the game session. 
 * If the user does not enter a nickname or clicks cancel, an error message is displayed, 
 * and the application exits. If a valid nickname is entered, a welcome message is displayed.
 */
     private void nicknamePrompt() {
             // Prompt the user to enter a nickname.
        nickname = JOptionPane.showInputDialog("Enter your nickname:");
        
            // Check if the nickname is null (cancel was clicked) or empty.
        if (nickname == null || nickname.trim().isEmpty()) {
            
                    // Display an error message and exit the application if the nickname is not provided.
            JOptionPane.showMessageDialog(null, "A nickname is required to play.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
                    // Display a welcome message if a valid nickname is entered.
            JOptionPane.showMessageDialog(null, "Welcome, " + nickname + "! Let's play Sudoku Vision.");
        }
    }
    
    // Load or create user data
/**
 * Loads the user data from a file specific to the nickname entered. If the user file exists, it reads
 * the user's coins and leaderboard entry. If the file does not exist, it creates new user data with
 * a starting bonus of coins and notifies the user of this bonus. After loading or creating, it updates 
 * the coin display on the GUI.
 */
private void loadOrCreateUser() {
        // To create a file object based on the user's nickname.
    File userFile = new File(nickname + "_data.txt");
        // To check if the user file exists.
    if (userFile.exists()) {
        try (Scanner fileScanner = new Scanner(userFile)) {
                        // To read coins from the file if available.
            if (fileScanner.hasNextInt()) {
                coins = fileScanner.nextInt(); // Load coins
            }
            // It will read additional data like wins and best time, then update leaderboard.
            if (fileScanner.hasNextInt()) {
                int wins = fileScanner.nextInt();
                long bestTimeInSeconds = fileScanner.nextLong();
                Duration bestTime = bestTimeInSeconds == -1 ? null : Duration.ofSeconds(bestTimeInSeconds);
                leaderboard.put(nickname, new LeaderboardEntry(wins, bestTime));
            }
        } catch (FileNotFoundException e) {
            //It will display error message if there is a problem reading the file.
            JOptionPane.showMessageDialog(null, "Error reading user file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // It will set a starting bonus for new users and save their data.
        coins = 100; // New user bonus
        saveUserData();
        JOptionPane.showMessageDialog(null, "As a new user, you get a bonus of 100 coins!");
    }
    updateCoinDisplay(); // Update the coins display after loading or creating the user.
}

    // Save user data to file
/**
 * Saves the user's data, including coins and leaderboard entry, to a file specific to the user's nickname.
 * If the user has a best time in the leaderboard, it is also saved. In case of any errors during the 
 * saving process, an error message is displayed.
 */
    private void saveUserData() {
    try (PrintWriter out = new PrintWriter(nickname + "_data.txt")) {
        // It will save the user's coins to the file.
        out.println(coins); // Save coins
        // It will retrieve the user's leaderboard entry, if available.
        LeaderboardEntry entry = leaderboard.get(nickname);
        if (entry != null) {
            // It will save wins and best time (if exists) to the file.
            long bestTimeInSeconds = entry.getBestTime() != null ? entry.getBestTime().getSeconds() : -1;
            out.printf("%d %d\n", entry.getWins(), bestTimeInSeconds);
        }
    } catch (IOException e) {
        // It will display an error message if there is a problem saving user data.
        JOptionPane.showMessageDialog(null, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Creates and displays the Sudoku game's graphical user interface (GUI) including buttons,
 * labels, and the game grid. It also sets up event listeners for various components.
 */
    private void createAndShowGUI() {
    // Create and set up the window.
    frame = new JFrame("Sudoku Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 500); // Increase width to accommodate the new section
    frame.setLayout(new BorderLayout());

    // Initialize components
    btnStartGame = new JButton("Start Game");
    timerLabel = new JLabel("Timer: 00:00");
    themeSelector = new JComboBox<>(THEMES);
    nameLabel = new JLabel("Nickname: " + nickname);
    coinLabel = new JLabel("Coins: " + coins);
    JButton btnLeaderboard = new JButton("Leaderboard");
    btnInstructions = new JButton("Instructions");
    btnHelp = new JButton("Hint");

    // Set font and add action listener for btnHelp
    btnHelp.setFont(btnHelp.getFont().deriveFont(Font.BOLD));
    btnHelp.addActionListener(e -> useHelp());
    btnHelp.setVisible(false); // Set Help button to be invisible initially

    // Set action listener for btnInstructions
    btnInstructions.setFont(btnInstructions.getFont().deriveFont(Font.BOLD));
    btnInstructions.addActionListener(e -> showInstructions());

    // Set up listeners
    themeSelector.addActionListener(new ThemeChangeListener());
    btnStartGame.addActionListener(e -> startGame());
    timerLabel.setHorizontalAlignment(JLabel.CENTER);
    btnLeaderboard.addActionListener(e -> showLeaderboard());

    // Top panel for nickname and coins
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(nameLabel, BorderLayout.WEST);
    topPanel.add(coinLabel, BorderLayout.EAST);

    // Theme selection panel
    JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    themePanel.add(themeSelector);

    // Combine top and theme panels
    JPanel combinedPanel = new JPanel(new BorderLayout());
    combinedPanel.add(topPanel, BorderLayout.NORTH);
    combinedPanel.add(themePanel, BorderLayout.SOUTH);

    // Create grid panel
    gridPanel = new JPanel(new GridLayout(thegame.getGameSize(), thegame.getGameSize()));
    gridButtons = new JButton[thegame.getGameSize()][thegame.getGameSize()];
    initializeGrid();

    // Right side panel for buttons
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
    rightPanel.add(btnStartGame);
    rightPanel.add(timerLabel);
    rightPanel.add(btnLeaderboard);
    rightPanel.add(btnHelp);
    rightPanel.add(btnInstructions); // Add instructions button

    // Add panels to frame
    frame.add(combinedPanel, BorderLayout.NORTH);
    frame.add(gridPanel, BorderLayout.CENTER);
    frame.add(rightPanel, BorderLayout.EAST);

    // Buttons panel for Save, Undo, Redo, etc.
    JPanel buttonsPanel = createButtonsPanel();
    frame.add(buttonsPanel, BorderLayout.SOUTH);

    // Display the window.
    frame.setVisible(true);
}

/**
 * Creates and returns a JPanel containing buttons for actions such as saving, undoing, redoing,
 * clearing, loading, and quitting the Sudoku game as implemented in the UI class.
 *
 * @return A JPanel containing the action buttons.
 */
private JPanel createButtonsPanel() {
    // Create a JPanel to hold the buttons with a centered flow layout
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // Create buttons for various actions
    JButton btnSave = new JButton("Save");
    JButton btnUndo = new JButton("Undo");
    JButton btnRedo = new JButton("Redo");
    JButton btnClear = new JButton("Clear");
    JButton btnLoad = new JButton("Load");
    JButton btnQuit = new JButton("Quit");

        // Add action listeners to the buttons
    btnSave.addActionListener(e -> saveGame());
    btnUndo.addActionListener(e -> undoMove());
    btnRedo.addActionListener(e -> redoMove());
    btnClear.addActionListener(e -> clearGame());
    btnLoad.addActionListener(e -> loadGame());
    btnQuit.addActionListener(e -> System.exit(0));

        // Add buttons to the buttonsPanel
    buttonsPanel.add(btnSave);
    buttonsPanel.add(btnUndo);
    buttonsPanel.add(btnRedo);
    buttonsPanel.add(btnClear);
    buttonsPanel.add(btnLoad);
    buttonsPanel.add(btnQuit);

    return buttonsPanel; // Return the panel with action buttons
}

/**
 * Initializes the grid of buttons for the Sudoku game. Creates a grid of buttons
 * with the same size as the Sudoku game board and sets up action listeners for each button.
 */
    private void initializeGrid() {
    // To create a new gridPanel with a grid layout matching the game size
    gridPanel = new JPanel(new GridLayout(thegame.getGameSize(), thegame.getGameSize()));
        // To initialize the gridButtons array to hold the buttons
    gridButtons = new JButton[thegame.getGameSize()][thegame.getGameSize()];
        // The Loop such that it can go through rows and columns to create buttons
    for (int row = 0; row < thegame.getGameSize(); row++) {
        for (int col = 0; col < thegame.getGameSize(); col++) {
            JButton button = new JButton();
            gridButtons[row][col] = button;
            // It sets an action listener for each button, passing row and col information
            button.addActionListener(new CellActionListener(row, col));
            // It adds the button to the gridPanel
            gridPanel.add(button);
            // Initialize button text with slot state
            updateButtonDisplay(row, col, thegame.getIndividualMove(row, col));
        }
    }
}

/**
 * It displays the leaderboard for the current user in a new JFrame. The leaderboard includes the user's
 * nickname, current coins, number of wins, and best time (if available).
 */
private void showLeaderboard() {
        // To create a new JFrame for the leaderboard
    JFrame leaderboardFrame = new JFrame("Leaderboard");
    leaderboardFrame.setSize(300, 200);
    leaderboardFrame.setLayout(new BorderLayout());

        // To create a JTextArea to display the leaderboard text
    JTextArea leaderboardText = new JTextArea();
    leaderboardText.append("Leaderboard for " + nickname + ":\n\n");

    // Use default values if no data is available
    LeaderboardEntry currentUserEntry = leaderboard.getOrDefault(nickname, new LeaderboardEntry(0, null));
    // Display user information in the leaderboard
    leaderboardText.append("Nickname: " + nickname + "\n");
    leaderboardText.append("Coins: " + coins + "\n"); // Display current coins
    leaderboardText.append("Wins: " + currentUserEntry.getWins() + "\n");
    
        // Display the best time or "N/A" if not available
    leaderboardText.append("Best Time: " + (currentUserEntry.getBestTime() != null ? formatDuration(currentUserEntry.getBestTime()) : "N/A") + "\n");

    leaderboardFrame.add(new JScrollPane(leaderboardText), BorderLayout.CENTER);
    leaderboardFrame.setVisible(true);   // Make the leaderboardFrame visible
}

/**
 * Formats a Duration object into a string representation of minutes and seconds in the "MM:SS" format.
 *
 * @param duration The Duration object is to be formatted.
 * @return The formatted duration string, or "N/A" if the duration is null.
 */
private String formatDuration(Duration duration) {
        // Check if the duration is null
    if (duration == null) {
        return "N/A";// Return "N/A" if the duration is null
    }
        // Extract minutes and seconds from the duration
    long minutes = duration.toMinutes();
    long seconds = duration.getSeconds() % 60;
        // Format the duration as "MM:SS"
    return String.format("%02d:%02d", minutes, seconds);
}

/**
 * Reads the solution for a Sudoku game from a solution file based on the game type.
 *
 * @param gameType The type of Sudoku game ("4x4" or "9x9").
 * @return A 2D array representing the Sudoku game solution.
 */
private String[][] readSolution(String gameType) {
        // It determines the solution file path based on the game type
    String solutionFile = gameType.equals("4x4") ? "Solutions/esu1solution.txt" : "Solutions/su1solution.txt";
        // It create a 2D array to store the Sudoku game solution
    String[][] solution = new String[thegame.getGameSize()][thegame.getGameSize()];

    try (Scanner scanner = new Scanner(new File(solutionFile))) {
        // It reads the solution file and populate the solution array
        while (scanner.hasNextInt()) {
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            String value = scanner.next();
            solution[row][col] = value;
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace(); // Handles file not found exception
    }
    return solution; // Returns the Sudoku game solution
}

/**
 * Displays game instructions to the user based on the selected game type.
 */
private void showInstructions() {
    String instructions;
        // Determines the game instructions based on the game type ("4x4" or "9x9")
    if ("4x4".equals(gameType)) {
        instructions = get4x4Instructions();
    } else {
        instructions = get9x9Instructions();
    }
        // Shows the game instructions in a dialog box
    JOptionPane.showMessageDialog(frame, instructions, "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Provides the instructions for playing the 4x4 Sudoku game.
 *
 * @return The instructions for playing 4x4 Sudoku.
 */
private String get4x4Instructions() {
    return "Instructions for 4x4 Sudoku:\n" + 
           "Objective: \n Fill the 4x4 grid with numbers so that each row, column, and 2x2 box contains the numbers 1 through 4 without repeating\n How to Play:\nClick on an empty cell to select it. \n Enter a number from 1 to 4. Each number must appear exactly once in each row, column, and 2x2 box.\n If you make a mistake, you can change the number in a cell or use the undo feature. \n You can also use the coins to get hints.";
}

/**
 * Provides the instructions for playing the 9x9 Sudoku game.
 *
 * @return The instructions for playing 9x9 Sudoku.
 */
private String get9x9Instructions() {
    return "Instructions for 9x9 Sudoku:\n" + 
           " Objective: \n Fill the 9x9 grid with numbers so that each row, column, and 3x3 box contains the numbers 1 through 9 without repeating.\n How to Play:\n Click on an empty cell to select it. \n Enter a number from 1 to 9. Each number must appear exactly once in each row, column, and 3x3 box.\n If you make a mistake, you can change the number in a cell or use the undo feature. \n You can also use the coins to get hints.";
}

/**
 * Provides a hint to the player during the game.
 * Randomly selects an empty cell and displays the correct number as a hint if the player has enough coins.
 * If the player doesn't have enough coins, an error message is displayed.
 */
private void useHelp() {
    // Select a cell for the hint
    int row, col;
    do {
        row = (int) (Math.random() * thegame.getGameSize());
        col = (int) (Math.random() * thegame.getGameSize());
    } while (!thegame.getMoves()[row][col].getFillable() || !thegame.getIndividualMove(row, col).equals("-"));

    // Increment hint count and calculate the cost
    hintCounts[row][col]++;
    int hintCost = 10 * hintCounts[row][col];

    if (coins >= hintCost) {
        int result = JOptionPane.showConfirmDialog(frame, "Use " + hintCost + " coins for a hint?", "Confirm Hint", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            coins -= hintCost;
            saveUserData();
            updateCoinDisplay();

            String[][] solution = readSolution(gameType);
            String hint = solution[row][col];
            gridButtons[row][col].setText(hint);
            thegame.makeMove(Integer.toString(row), Integer.toString(col), hint); // Update game state
            checkWin(); // Check if the game is won after using a hint
        }
    } else {
        JOptionPane.showMessageDialog(frame, "Not enough coins for a hint.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Applies the selected theme to the Sudoku game interface.
 * The theme determines the visual appearance of the game.
 *
 * @param theme The name of the theme to apply.
 */
private void applyTheme(String theme) {
      switch (theme) {
            case "Classic":
                setClassicTheme();
                break;
            case "Light Mode":
                setLightTheme();
                break;
            case "Dark Mode":
                setDarkTheme();
                break;
            case "Pastel":
                setPastelTheme();
                break;
            case "High Contrast":
                setHighContrastTheme();
                break;
            case "Nature":
                setNatureTheme();
                break;
            case "Ocean":
                setOceanTheme();
                break;
            case "Seasonal":
                setAutumnTheme();
                break;
            case "Material Design":
                setMaterialDesignTheme();
                break;
            case "Retro/Vintage":
                setRetroVintageTheme();
                break;
        }
        frame.repaint();// Repaint the frame to apply the new theme
        frame.revalidate();// Revalidate the frame to ensure layout consistency

}
    
/**
 * Sets the Classic theme for the Sudoku game interface.
 * This theme features a white background with black text.
 */
    private void setClassicTheme() {
    frame.getContentPane().setBackground(Color.WHITE); // Set the frame's background to white
    gridPanel.setBackground(Color.WHITE); // Set the grid panel's background to white
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            button.setBackground(Color.WHITE); // Set each button's background to white
            button.setForeground(Color.BLACK); // Set each button's text color to black
        }
    }
}
    
/**
 * Sets the Light Theme for the Sudoku game interface.
 * This theme features a light gray background with black text.
 */
    private void setLightTheme() {
    Color lightGray = new Color(220, 220, 220); // Define a light gray color
    frame.getContentPane().setBackground(lightGray);  // Set the frame's background to light gray
    gridPanel.setBackground(lightGray); // Set the grid panel's background to light gray
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            button.setBackground(lightGray); // Set each button's background to light gray
            button.setForeground(Color.BLACK); // Set each button's text color to black
        }
    }
}

/**
 * Sets the Dark Theme for the Sudoku game interface.
 * This theme features a dark gray background with white text.
 */
    private void setDarkTheme() {
    Color darkGray = new Color(60, 63, 65); // Define a dark gray color
    frame.getContentPane().setBackground(darkGray); // Set the frame's background to dark gray
    gridPanel.setBackground(darkGray);// Set the grid panel's background to dark gray
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            button.setBackground(darkGray); // Set each button's background to dark gray
            button.setForeground(Color.WHITE); // Set each button's text color to white
        }
    }
}

/**
 * Sets the Pastel Theme for the Sudoku game interface.
 * This theme features pastel-colored backgrounds and dark gray text.
 */
private void setPastelTheme() {
    Color[] pastelColors = {new Color(255, 182, 193), new Color(173, 216, 230), new Color(152, 251, 152), new Color(230, 230, 250)}; // Define an array of pastel colors
    for (int row = 0; row < gridButtons.length; row++) {
        for (int col = 0; col < gridButtons[row].length; col++) {
            // Set each button's background to a pastel color based on its position in the grid
            gridButtons[row][col].setBackground(pastelColors[(row + col) % pastelColors.length]);
            gridButtons[row][col].setForeground(Color.DARK_GRAY); // Set the text color to dark gray
        }
    }
}

/**
 * Sets the High Contrast Theme for the Sudoku game interface.
 * This theme features a yellow background and blue text for high visibility.
 */
private void setHighContrastTheme() {
    Color backgroundColor = Color.YELLOW; // Define the background color as yellow
    Color textColor = Color.BLUE; // Define the text color as blue
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
                        // Set each button's background and text color
            button.setBackground(backgroundColor);
            button.setForeground(textColor);
        }
    }
}

/**
 * Sets the Nature Theme for the Sudoku game interface.
 * This theme features nature-inspired colors for a soothing visual experience.
 */
private void setNatureTheme() {
    Color[] natureColors = {new Color(107, 142, 35), new Color(160, 82, 45), new Color(70, 130, 180)};
    for (int row = 0; row < gridButtons.length; row++) {
        for (int col = 0; col < gridButtons[row].length; col++) {
            // Set each button's background color using nature-inspired colors in a cyclic pattern
            gridButtons[row][col].setBackground(natureColors[(row + col) % natureColors.length]);
            gridButtons[row][col].setForeground(Color.WHITE);// Set the text color to white for visibility
        }
    }
}

/**
 * Sets the Ocean Theme for the Sudoku game interface.
 * This theme features ocean-inspired colors for a refreshing visual experience.
 */
private void setOceanTheme() {
    Color[] oceanColors = {new Color(0, 105, 148), new Color(72, 202, 228), new Color(144, 224, 239)};
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            // Set each button's background color using ocean-inspired colors in a cyclic pattern
            button.setBackground(oceanColors[(buttonRow.length + button.getY()) % oceanColors.length]);
            button.setForeground(Color.WHITE);
        }
    }
}

/**
 * Sets the Autumn Theme for the Sudoku game interface.
 * This theme features warm autumn-inspired colors for a cozy visual ambiance.
 */
private void setAutumnTheme() {
    Color[] autumnColors = {new Color(205, 92, 92), new Color(233, 150, 122), new Color(255, 215, 0)};
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
                        // Set each button's background color using autumn-inspired colors in a cyclic pattern
            button.setBackground(autumnColors[(buttonRow.length + button.getY()) % autumnColors.length]);
            button.setForeground(Color.DARK_GRAY);
        }
    }
}

/**
 * Sets the Material Design Theme for the Sudoku game interface.
 * This theme features vibrant material design-inspired colors for a modern and colorful visual experience.
 */
private void setMaterialDesignTheme() {
    Color[] materialColors = {new Color(0, 150, 136), new Color(63, 81, 181), new Color(255, 235, 59)};
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            // Set each button's background color using material design-inspired colors in a cyclic pattern
            button.setBackground(materialColors[(buttonRow.length + button.getY()) % materialColors.length]);
            button.setForeground(Color.WHITE);
        }
    }
}

/**
 * Sets the Retro/Vintage Theme for the Sudoku game interface.
 * This theme features a retro and vintage color palette for a nostalgic visual experience.
 */
private void setRetroVintageTheme() {
    Color[] retroColors = {new Color(255, 204, 0), new Color(0, 128, 128), new Color(255, 99, 71)};
    for (JButton[] buttonRow : gridButtons) {
        for (JButton button : buttonRow) {
            // Set each button's background color using retro and vintage-inspired colors in a cyclic pattern
            button.setBackground(retroColors[(buttonRow.length + button.getY()) % retroColors.length]);
            button.setForeground(Color.WHITE);
        }
    }
}

/**
 * Updates the display of a button based on the state of a slot.
 *
 * @param slot The slot containing the state information to update the button's display.
 */
    private void updateButtonDisplay(Slot slot) {
        // It will call the overloaded version of updateButtonDisplay with the slot's row, column, and state
        updateButtonDisplay(slot.getRow(), slot.getCol(), slot.getState());
    }
    
/**
 * Updates the display of a button in the grid based on the state of a slot.
 *
 * @param row    The row index of the button in the grid.
 * @param col    The column index of the button in the grid.
 * @param state  The state of the slot, which determines the button's text and appearance.
 */
    private void updateButtonDisplay(int row, int col, String state) {
        JButton button = gridButtons[row][col];
            // Check if the slot state is "-" (unfilled) and set the button text accordingly
         if (state.equals("-")) {
        button.setText(""); // Use an empty string for unfilled slots
         } else {
        button.setText(state);
        }
            // Check if the slot is not fillable, and update button appearance accordingly
        if (!thegame.getMoves()[row][col].getFillable()) {
            button.setFont(button.getFont().deriveFont(Font.BOLD));
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            button.setForeground(Color.BLACK); // Ensures text color is visible
        } else {
            button.setFont(button.getFont().deriveFont(Font.PLAIN));
            button.setBorder(UIManager.getBorder("Button.border"));
            button.setForeground(Color.BLACK); // Ensures text color is visible
        }
    }

// Observer update method
/**
 * This method is called when an observed Slot object notifies its observers of a change.
 * It updates the display of the corresponding button in the Sudoku GUI grid based on the
 * state of the Slot.
 *
 * @param o   The Observable object (a Slot) that triggered the update.
 * @param arg An optional argument (not used in this implementation).
 */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Slot) {// To check if the observed object is an instance of Slot
            Slot slot = (Slot) o; // To cast the observed object to a Slot
            updateButtonDisplay(slot.getRow(), slot.getCol(), slot.getState());
        }
    }

/**
 * This inner class implements an ActionListener to respond to theme selection changes
 * in the theme selector JComboBox. It applies the selected theme to the Sudoku GUI.
 */
private class ThemeChangeListener implements ActionListener {
    /**
     * Invoked when a theme is selected in the JComboBox. It retrieves the selected theme
     * name and applies the chosen theme to the Sudoku GUI.
     *
     * @param e The ActionEvent representing the theme selection change.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<String> cb = (JComboBox<String>) e.getSource();
        String themeName = (String) cb.getSelectedItem();
        applyTheme(themeName);
    }
}
    
    // Call this method to use a hint
/**
 * This method allows the user to use a hint by spending coins. If the user has enough coins,
 * the method deducts the hint cost, saves user data, provides a hint to the user (hint logic is
 * not implemented here), and updates the coin display. If there are not enough coins, it displays
 * an error message.
 */
    private void useHint() {
        int hintCost = 50; // Cost for a hint
        if (coins >= hintCost) {
            coins -= hintCost;
            saveUserData();
            // Provide a hint to the user
            updateCoinDisplay();
        } else {
            JOptionPane.showMessageDialog(null, "Not enough coins for a hint.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Call this method when the user earns coins
/**
 * We will call this method when the user earns coins to increase their coin balance.
 *
 * @param amount The amount of coins to add to the user's balance.
 */
    private void earnCoins(int amount) {
        coins += amount;
        saveUserData();
        updateCoinDisplay();
    }
    
    // Update the coin display label
/**
 * It updates the coin display label to reflect the current number of coins.
 */
    private void updateCoinDisplay() {
        if (coinLabel != null) {
            coinLabel.setText("Coins: " + coins);
        }
    }
    
/**
 * ActionListener for individual cells in the Sudoku grid.
 * This listener is associated with a specific cell identified by its row and column.
 */
    private class CellActionListener implements ActionListener {
    private final int row;
    private final int col;
    /**
     * Constructor for CellActionListener.
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    public CellActionListener(int row, int col) {
        this.row = row;
        this.col = col;
    }

/**
* Handles the actionPerformed event when a cell is clicked.
* Prompts the user to enter a number for the selected cell.
* Validates the input number and updates the game state.
* @param e The ActionEvent representing the cell click event.
*/
 @Override
public void actionPerformed(ActionEvent e) {
    if (!gameStarted) {
        JOptionPane.showMessageDialog(frame, "Please click on 'Start Game' to make a move.", "Start Game", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    int maxNumber = gameType.equals("4x4") ? 4 : 9;
    String number = JOptionPane.showInputDialog(frame, "Enter number (1-" + maxNumber + "):");

    if (number != null && !number.isEmpty() && isNumberValid(number, maxNumber)) {
        String prevState = thegame.getIndividualMove(row, col);
        boolean moveMade = thegame.makeMove(Integer.toString(row), Integer.toString(col), number);

        if (moveMade) {
            moveHistory.push(new Move(row, col, prevState));
            updateButtonDisplay(row, col, thegame.getIndividualMove(row, col));
            checkWin(); // Checks if the game is won after each move
        } else {
            JOptionPane.showMessageDialog(frame, "Cannot change this number.");
        }
    } else {
        JOptionPane.showMessageDialog(frame, "Invalid move! Please enter a number between 1 and " + maxNumber + ".");
    }
}

/**
 * Checks if a given number is valid within the specified range.
 *
 * @param number    The number to be validated.
 * @param maxNumber The maximum valid number allowed.
 * @return True if the number is valid and within the range [1, maxNumber], false otherwise.
 */
private boolean isNumberValid(String number, int maxNumber) {
    try {
        int num = Integer.parseInt(number);
        return num >= 1 && num <= maxNumber;
    } catch (NumberFormatException e) {
        return false;
    }
}
}

/**
 * Saves the current game state to a text file.
 */
    private void saveGame() {
    // Creates a file chooser dialog for the user to choose where to save the game.
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Game");
    fileChooser.setSelectedFile(new File("sudoku_save.txt"));
        // Shows the save dialog and get the user's selection.
    int userSelection = fileChooser.showSaveDialog(frame);
        // Checks if the user approved the save dialog.
    if (userSelection == JFileChooser.APPROVE_OPTION) {
                // Get the selected file to save the game data.
        File fileToSave = fileChooser.getSelectedFile();
        try (FileWriter writer = new FileWriter(fileToSave)) {
            for (int i = 0; i < thegame.getGameSize(); i++) {
                for (int j = 0; j < thegame.getGameSize(); j++) {
                    writer.write(thegame.getIndividualMove(i, j) + " ");
                }
                writer.write("\n"); // Start a new line for the next row of cells.
            }
            JOptionPane.showMessageDialog(frame, "Game saved successfully to " + fileToSave.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while saving the game.", "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

/**
 * Starts the Sudoku game.
 */
private void startGame() {
        // Set the game as started
    gameStarted = true;
        // Initialize the grid buttons with the actual slot states
    for (int row = 0; row < thegame.getGameSize(); row++) {
        for (int col = 0; col < thegame.getGameSize(); col++) {
            // Update each grid button with the state of the corresponding slot
            updateButtonDisplay(row, col, thegame.getIndividualMove(row, col));
        }
    }

    // Ensure grid panel is updated and visible
    gridPanel.revalidate();
    gridPanel.repaint();

    // Start the timer
    startTime = LocalTime.now();
    gameTimer = new Timer(100, e -> updateTimer());
    gameTimer.start();
    btnStartGame.setEnabled(false); // Disable start button after game starts

    btnHelp.setVisible(true); // Make help button visible here, outside the loop
}

/**
 * Updates the game timer display.
 */
    private void updateTimer() {
            // Calculate the duration since the game started
        Duration duration = Duration.between(startTime, LocalTime.now());
            // Calculate minutes and seconds from the duration
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
            // Update the timer label with the formatted time
        timerLabel.setText(String.format("Timer: %02d:%02d", minutes, seconds));
    }
    
/**
 * Checks if the player has won the Sudoku game and takes appropriate actions if they have.
 * If the player has won, it stops the game timer, displays a congratulatory message with
 * the time taken to solve the puzzle, updates the player's leaderboard entry, saves user data,
 * saves the updated leaderboard, awards 50 coins for winning, re-enables the start button,
 * updates the leaderboard display, and resets the game for a new round.
 */
private void checkWin() {
        // Check if the game is won
    if (thegame.checkWin()) {
        gameTimer.stop();// Stop the game timer
                // Calculate the time taken to solve the puzzle
        Duration timeTaken = Duration.between(startTime, LocalTime.now());
                // Create a message to congratulate the player and display the time taken
        String timeMessage = String.format("Congratulations, you solved the puzzle in %d minutes and %d seconds! \n You earned 50 COINS!!! Hurrayyyyyyyy",
                                           timeTaken.toMinutes(), timeTaken.getSeconds() % 60);
        JOptionPane.showMessageDialog(frame, timeMessage);
        // Update the leaderboard entry for the player
        LeaderboardEntry entry = leaderboard.get(nickname);
        if (entry == null) {
            entry = new LeaderboardEntry(1, timeTaken);
        } else {
            entry.setWins(entry.getWins() + 1);
            entry.setBestTime(timeTaken);
        }
        leaderboard.put(nickname, entry);
        saveUserData(); // Save the user data, including leaderboard info
        saveLeaderboard();
        earnCoins(50); // Award 50 coins for winning
        btnStartGame.setEnabled(true); // Re-enable start button
                updateLeaderboard(timeTaken);
        resetGame();

    }
}
    
/**
 * Updates the player's leaderboard entry, saves user data, saves the updated leaderboard,
 * and awards 50 coins for winning the game. This method is called when the player wins
 * the Sudoku game.
 *
 * @param timeTaken The duration of time it took the player to solve the puzzle.
 */
private void updateLeaderboard(Duration timeTaken) {
        // Get the current leaderboard entry for the player, or create a new one if it doesn't exist
    LeaderboardEntry entry = leaderboard.get(nickname);
    if (entry == null) {
        entry = new LeaderboardEntry(1, timeTaken);
    } else {
        entry.setWins(entry.getWins() + 1);
                // Update the best time if the current time is better (lower) than the previous best
        if (entry.getBestTime() == null || timeTaken.compareTo(entry.getBestTime()) < 0) {
            entry.setBestTime(timeTaken);
        }
    }
    leaderboard.put(nickname, entry);
    saveUserData(); // Save the user data, including leaderboard info
    saveLeaderboard();
    earnCoins(50); // Award 50 coins for winning
}

/**
 * Resets the Sudoku game to its initial state. This includes reinitializing the game,
 * adding the GUI as an observer to all slots, clearing move and redo history, enabling
 * the "Start Game" button, hiding the "Help" button, resetting the timer label, and
 * displaying a message to inform the player that the game has been reset.
 */
private void resetGame() {
    thegame = new Sudoku(gameType); // Reinitialize the game
    thegame.addObserverToSlots(this); // Re-add this GUI as an observer to all Slots
    refreshGrid(); // Refresh the grid to initial state

        // Clear move history and redo history
    moveHistory.clear();
    redoHistory.clear();

    btnStartGame.setEnabled(true);
    btnHelp.setVisible(false);
    timerLabel.setText("Timer: 00:00");
    // Display a message to inform the player that the game has been reset
    JOptionPane.showMessageDialog(frame, "Game reset. Ready for a new challenge!");
}

/**
 * Loads the leaderboard data from a file and populates the leaderboard map with
 * user entries. The data is expected to be in txt format with each line containing
 * the user's nickname, number of wins, and best time in seconds (or -1 if no best time).
 * If the file is not found or an error occurs while reading, this method prints the
 * stack trace but does not throw an exception.
 */
    private void loadLeaderboard() {
    try (Scanner scanner = new Scanner(new File("leaderboard.txt"))) {
        while (scanner.hasNextLine()) {
                        // Parse each line of the leaderboard data
            String[] data = scanner.nextLine().split(",");
            String user = data[0];
            int wins = Integer.parseInt(data[1]);
            Duration bestTime = data[2].equals("-1") ? null : Duration.ofSeconds(Long.parseLong(data[2]));
                        // Create a LeaderboardEntry for the user and add it to the leaderboard map
            leaderboard.put(user, new LeaderboardEntry(wins, bestTime));
        }
    } catch (FileNotFoundException e) {
            // Print the stack trace if the file is not found, but continue execution
        e.printStackTrace();
    }
}

/**
 * Saves the current leaderboard data to a file. Each entry in the leaderboard
 * map is written as a line in the file, with the user's nickname, number of wins, and best
 * time in seconds . If an error occurs while writing the file,
 * this method prints the stack trace but does not throw an exception.
 */
    private void saveLeaderboard() {
    try (PrintWriter out = new PrintWriter(new File("leaderboard.txt"))) {
        // Iterate over the leaderboard map and write each entry to the file
        for (Map.Entry<String, LeaderboardEntry> entry : leaderboard.entrySet()) {
            long bestTimeInSeconds = entry.getValue().getBestTime() != null ? entry.getValue().getBestTime().getSeconds() : -1;
            out.printf("%s,%d,%d\n", entry.getKey(), entry.getValue().getWins(), bestTimeInSeconds);
        }
    } catch (IOException e) {
        // Print the stack trace if an error occurs while writing the file, but continue execution
        e.printStackTrace();
    }
}

/**
 * This is to Undo the last move made by the player. If there are moves in the move history stack,
 * the last move is popped from the stack, and the game state is reverted to the previous
 * state. The undone move is pushed to the redo history stack. The corresponding button on
 * the grid is updated to reflect the previous state. If no moves are available to undo,
 * a message is displayed to the user indicating that there are no moves to undo.
 */
private void undoMove() {
        if (!moveHistory.isEmpty()) {
            // Pop the last move from the move history
            Move lastMove = moveHistory.pop();
           // Push the undone move to the redo history
            redoHistory.push(new Move(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col)));
            // Revert the game state to the previous state   
            thegame.makeMove(Integer.toString(lastMove.row), Integer.toString(lastMove.col), lastMove.prevValue);
            // Update the button display to reflect the previous state
            updateButtonDisplay(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col));
            // Display a message indicating that the move has been undone
            JOptionPane.showMessageDialog(frame, "Move undone.");
        } else {
            // Display a message indicating that there are no moves to undo
            JOptionPane.showMessageDialog(frame, "No moves to undo.");
        }
    }

/**
 * It will redo the last undone move made by the player. If there are moves in the redo history stack,
 * the last undone move is popped from the stack, and the game state is reverted to the state
 * before the move was undone. The redone move is pushed back to the move history stack. The
 * corresponding button on the grid is updated to reflect the redone state. If no moves are
 * available to redo, a message is displayed to the user indicating that there are no moves to redo.
 */
private void redoMove() {
        if (!redoHistory.isEmpty()) {
             // Pop the last undone move from the redo history
            Move lastMove = redoHistory.pop();
            // Push the redone move to the move history
            moveHistory.push(new Move(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col)));
            // Revert the game state to the state before the move was undone
            thegame.makeMove(Integer.toString(lastMove.row), Integer.toString(lastMove.col), lastMove.prevValue);
            // Update the button display to reflect the redone state
            updateButtonDisplay(lastMove.row, lastMove.col, thegame.getIndividualMove(lastMove.row, lastMove.col));
            // Display a message indicating that the move has been redone
            JOptionPane.showMessageDialog(frame, "Move redone.");
        } else {
            // Display a message indicating that there are no moves to redo
            JOptionPane.showMessageDialog(frame, "No moves to redo.");
        }
    }

/**
 * Clears the current game, resetting only the fillable cells to their initial state. This method
 * sets the game state to a non-started state, reverting fillable cells to empty ("-"). The grid
 * buttons are updated to reflect this change. The move history and redo history stacks are cleared.
 * If a game timer is active, it is stopped and the timer label is reset to "Timer: 00:00". Finally,
 * the 'Start Game' button is re-enabled, allowing the player to start a new game.
 */
private void clearGame() {
    // Reset only the fillable cells to their initial state
    gameStarted = false;
    for (int row = 0; row < thegame.getGameSize(); row++) {
        for (int col = 0; col < thegame.getGameSize(); col++) {
            if (thegame.getMoves()[row][col].getFillable()) {
                thegame.makeMove(Integer.toString(row), Integer.toString(col), "-");
                updateButtonDisplay(row, col, "-");
            }
        }
    }
    // Clear history stacks
    moveHistory.clear();
    redoHistory.clear();
    // Stop the timer and reset the timer label
    if (gameTimer != null) {
        gameTimer.stop();
        timerLabel.setText("Timer: 00:00");
    }
    // Re-enable the 'Start Game' button
    btnStartGame.setEnabled(true);
    JOptionPane.showMessageDialog(frame, "Game reset.");
}

/**
 * Clears the text displayed on all grid buttons. This method sets the text of all grid buttons
 * to an empty string, effectively clearing any numbers or symbols displayed on the buttons.
 */
private void clearGrid() {
        for (int row = 0; row < thegame.getGameSize(); row++) {
            for (int col = 0; col < thegame.getGameSize(); col++) {
                gridButtons[row][col].setText("");
            }
        }
    }
    
/**
 * Loads a saved game state from a file and updates the Sudoku grid to reflect the loaded game.
 * This method allows users to continue a previously saved game.
 */
private void loadGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Load Game");
    int userSelection = fileChooser.showOpenDialog(frame);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToLoad = fileChooser.getSelectedFile();
        try (Scanner fileScanner = new Scanner(fileToLoad)) {
            // Reset the game to the initial state
            thegame = new Sudoku(gameType); 
            thegame.addObserverToSlots(this); // Re-add observer

            // Load the game state from the file
            for (int i = 0; i < thegame.getGameSize(); i++) {
                String[] line = fileScanner.nextLine().trim().split(" ");
                for (int j = 0; j < thegame.getGameSize(); j++) {
                    // Update the Sudoku object state
                    if (!line[j].equals("-")) {
                        thegame.makeMove(Integer.toString(i), Integer.toString(j), line[j]);
                    }
                }
            }
            // Refresh the GUI to reflect the loaded game
            refreshGrid(); 
            JOptionPane.showMessageDialog(frame, "Game loaded successfully from " + fileToLoad.getAbsolutePath());
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "File not found.", "Load Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading the game.", "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

/**
 * Refreshes the Sudoku grid in the GUI to reflect the current state of the game.
 * This method updates the display of each cell in the grid based on the values and fillable status
 * of the corresponding cells in the Sudoku game object.
 */
private void refreshGrid() {
    for (int row = 0; row < thegame.getGameSize(); row++) {
        for (int col = 0; col < thegame.getGameSize(); col++) {
            // Update the GUI to reflect the current state of each cell
            updateButtonDisplay(row, col, thegame.getIndividualMove(row, col));
        }
    }
}

    public static void main(String[] args) {
            // Start the SudokuGUI application on the event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SudokuGUI();
            }
        });
    }
}//end of SudokuGUI class
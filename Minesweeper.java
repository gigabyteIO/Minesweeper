import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


/**
 * This program plays the game Minesweeper. 
 * Mines are placed randomly on the board depending on the difficulty(beginner, intermediate, expert). 
 * Squares tell you how many mines are around shown square.
 * The player wins when they have cleared all the boxes without mines and have flagged the boxes with mines.
 * When a player flags a box(shift + left-click) it becomes immune to normal clicks.
 * A player can unflag a box by shift + left-clicking it again.
 * 
 * There are three difficulties the player can choose from in the tool bar.
 * 
 *  Beginner: 12 mines in 10x10 
 *  Intermediate: 35 mines in 15x15 
 *  Expert: 82 mines in 20x20 
 * 
 * @author martin
 *
 */
public class Minesweeper extends Application {

	private static int ROWS;		  	// The number of rows on the board.
	private static int COLUMNS;		  	// The number of columns on the board.
	private static int NUMBER_OF_MINES; // The number of mines on the board.
	
	enum BoxState { HIDDEN, SHOWN, FLAGGED }; 			// Represents the possible states a box can be in.
	enum Difficulty { BEGINNER, INTERMEDIATE, EXPERT }; // Represents the difficulties.
	
	private Difficulty difficulty; // The current difficulty.
	private double[] rowBounds;	   // The bounds for each row.	
	private double[] columnBounds; // The bounds for each column.
	
	private boolean[][] mines; 	// 2D array representing where the mines are placed.
	private BoxState[][] state; // 2D array representing what state the box is in.
	
	private GraphicsContext g; 		// A graphics context for drawing on the canvas.
	private boolean gameInProgress; // Represents if a game is currently in progress.
	private boolean shiftPressed;   // Represents if the player is pressing the shift button.
	
	/** Winning/Losing conditions **/
	
	private int boxesShown;			// The number of boxes in the shown state.
	private int flagsPlaced;		// The number of flags placed.
	private boolean hitMine;		// Represents if the player has clicked a mine.
	
	private Label flagsLabel;		// For displaying the # of flags.
	private Label clickedLabel;		// For displaying the # of shown squares.
	private Label outcomeLabel;		// For displaying if the game has been won or lost.
	
	
	
	/**
	 * The start() method of a JavaFX application sets up the GUI.
	 * and initialize global variables.
	 */
	public void start(Stage stage) throws Exception {
		Canvas canvas = new Canvas(600, 600);
		g = canvas.getGraphicsContext2D();
		
		flagsLabel = makeLabel("Flags placed: 0");
		clickedLabel = makeLabel("Boxes clicked: 0");
		outcomeLabel = makeLabel("Good luck!");
		
		HBox infoBar = new HBox(6, clickedLabel, flagsLabel, outcomeLabel );
		infoBar.setStyle("-fx-border-color: darkred; -fx-border-width: 3px 0 0 0;" 
                + "-fx-padding: 8px; -fx-background-color:beige");
		infoBar.setAlignment(Pos.CENTER);
		
		
		BorderPane root = new BorderPane(canvas);
		root.setTop(createMenuBar());
		root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
		root.setBottom(infoBar);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Minesweeper"); 
		stage.show();
		stage.setResizable(false);
		
		scene.setOnKeyPressed(evt -> doKeyPressed(evt));
		scene.setOnKeyReleased(evt -> doKeyReleased(evt));
		canvas.setOnMousePressed( evt -> doMousePressed(evt));
		
		difficulty = Difficulty.BEGINNER;
		setDifficulty(difficulty); // Initial difficulty is set to beginner.
		draw();
	}

	/**
	 * Does all the drawing for Minesweeper. This is called at the beginning of the 
	 * 	game or whenever anything needs to be updated on the board.
	 */
	private void draw() {
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, g.getCanvas().getWidth(), g.getCanvas().getHeight());

		double boxWidth, boxHeight;
		boxWidth = g.getCanvas().getWidth() / COLUMNS;
		boxHeight = g.getCanvas().getHeight() / ROWS;

		double lineX, lineY, rectX, rectY;
		lineX = 0;
		lineY = 0;
		rectX = 0;
		rectY = 0;

		if(gameInProgress) {
			for(int row = 0; row < ROWS; row++) {

				for(int col = 0; col < COLUMNS; col++) {

					/***** DRAW BOXES *****/
					// Hidden = DarkGreen
					if(state[row][col] == BoxState.HIDDEN) {
						g.setFill(Color.DARKGREEN);
						g.fillRect(rectX , rectY, boxWidth , boxHeight );
					}
					// Shown = LightGreen
					else if(state[row][col] == BoxState.SHOWN) {
						g.setFill(Color.LIMEGREEN);
						g.fillRect(rectX , rectY, boxWidth, boxHeight);
						int m = checkForMines(row, col);
						g.setStroke(setTextColor(m));
						if(m > 0)
							g.strokeText(Integer.toString(m), rectX + (boxWidth * .45), rectY + (boxHeight * .6));
					}
					// Flagged = PINK
					else if(state[row][col] == BoxState.FLAGGED) {
						g.setFill(Color.HOTPINK);
						g.fillRect(rectX, rectY, boxWidth, boxHeight);
					}

					/***** DRAW VERTICAL LINES *****/
					g.setStroke(Color.BLACK);
					g.strokeLine(lineX, 0, lineX, g.getCanvas().getHeight());

					/***** DRAW HORIZONTAL LINES *****/
					g.strokeLine(0, lineY, g.getCanvas().getWidth(), lineY);

					lineX += boxWidth;
					rectX += boxWidth;

				}
				// Reset for the next row.
				lineX = 0;
				rectX = 0;

				// Increment for the next row.
				lineY += boxHeight;
				rectY += boxHeight;
			}
		}
		// Game is over.
		else {
			for(int row = 0; row < ROWS; row++) {
				for(int col = 0; col < COLUMNS; col++) {

					/***** DRAW BOXES *****/
					// Hidden = DarkGreen
					if(state[row][col] == BoxState.HIDDEN) {
						g.setFill(Color.DARKGREEN);
						g.fillRect(rectX , rectY, boxWidth , boxHeight );

						if(mines[row][col] == true) {
							g.strokeText("*", rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
						else {
							int m = checkForMines(row, col);
							g.setStroke(setTextColor(m));
							if(m > 0)
								g.strokeText(Integer.toString(m), rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
					}
					// Shown = LightGreen
					else if(state[row][col] == BoxState.SHOWN) {
						g.setFill(Color.LIMEGREEN);
						g.fillRect(rectX , rectY, boxWidth, boxHeight);
						if(mines[row][col] == true) {
							g.setFill(Color.RED);
							g.fillRect(rectX , rectY, boxWidth, boxHeight);
							g.strokeText("*", rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
						else {
							int m = checkForMines(row, col);
							g.setStroke(setTextColor(m));
							if(m > 0)
								g.strokeText(Integer.toString(m), rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
					}
					// Flagged = PINK
					else if(state[row][col] == BoxState.FLAGGED) {
						g.setFill(Color.HOTPINK);
						g.fillRect(rectX, rectY, boxWidth, boxHeight);
						if(mines[row][col] == true) {
							g.strokeText("*", rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
						else {
							int m = checkForMines(row, col);
							g.setStroke(setTextColor(m));
							if(m > 0)
								g.strokeText(Integer.toString(m), rectX + (boxWidth * .45), rectY + (boxHeight * .6));
						}
					}

					/***** DRAW VERTICAL LINES *****/
					g.setStroke(Color.BLACK);
					g.strokeLine(lineX, 0, lineX, g.getCanvas().getHeight());

					/***** DRAW HORIZONTAL LINES *****/
					g.strokeLine(0, lineY, g.getCanvas().getWidth(), lineY);

					lineX += boxWidth;
					rectX += boxWidth;

				}
				// Reset for the next row.
				lineX = 0;
				rectX = 0;

				// Increment for the next row.
				lineY += boxHeight;
				rectY += boxHeight;
			}
		}
	}

	/**
	 * Initializes all global variables and draws the board. This is called by the
	 *  setDifficulty() method.
	 */
	private void initialize() {
		
		boxesShown = 0;
		flagsPlaced = 0;
		hitMine = false;
		
		/* Beginner: 12 mines in 10x10 
		 * Intermediate: 35 mines in 15x15 
		 * Expert: 82 mines in 20x20 */
		
		mines = new boolean[ROWS][COLUMNS];
		state = new BoxState[ROWS][COLUMNS];
		
		gameInProgress = true;
		
		// Set mines.
		placeMines(NUMBER_OF_MINES);
		
		// Initial state is hidden.
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				state[row][col] = BoxState.HIDDEN;
			}
		}
		
		calculateBounds();	
		showData();
		draw();		
	}

	
	
	/**
	 * Places the mines the the mines array.
	 * @param numOfMines The total number of mines to be placed.
	 */
	private void placeMines(int numOfMines) {
		int randomRow;
		int randomColumn;

		int i = 0;
		while(i < numOfMines) {
			randomRow = (int) (Math.random() * ROWS);
			randomColumn = (int) (Math.random() * COLUMNS);

			if(mines[randomRow][randomColumn] == false) {
				mines[randomRow][randomColumn] = true;
				i++;				
				// Debug statement.
				System.out.println("Mine " + i + ": [" + randomRow + ", " + randomColumn + "]");
			}
			else {
			}
		}	
	}
	
	/**
	 * Starts a new game by reseting global variables and redrawing the board.
	 */
	private void doNewGame() {
		setDifficulty(difficulty);
	}
	
	/*********** KEY EVENTS ************/
	
	/**
	 * This is to check for shift-clicks;
	 * @param evt The key event.
	 */
	private void doKeyPressed(KeyEvent evt) {
		KeyCode key = evt.getCode();
		System.out.println("Key Pressed: " + key);	
		
		if(key == KeyCode.SHIFT) {
			shiftPressed = true;
		}
	}
	
	/**
	 * This is to check for shift-clicks;
	 * @param evt The key event.
	 */
	private void doKeyReleased(KeyEvent evt) {
		KeyCode key = evt.getCode();
		System.out.println("Key Released: " + key);
		
		if(key == KeyCode.SHIFT) {
			shiftPressed = false;
		}
	}
	
	/**
	 * Handles mouse presses by determining the row and the column of the mouse press.
	 * Based on the row and column pressed, the state is updated and the draw() method is called
	 * to reflect the new state.
	 * @param evt The mouse press event.
	 */
	private void doMousePressed(MouseEvent evt ) {
		double x, y;

		x = evt.getX();
		y = evt.getY();

		int row, col;
		row = getRow(y);
		col = getCol(x);

		if(gameInProgress) {
			if(state[row][col] == BoxState.HIDDEN && !shiftPressed && mines[row][col] == false) {
				state[row][col] = BoxState.SHOWN;
				boxesShown++;

				if(boxesShown == (ROWS * COLUMNS - NUMBER_OF_MINES)) {
					gameInProgress = false;
				}
			}
			else if( (state[row][col] == BoxState.HIDDEN) && shiftPressed) {
				state[row][col] = BoxState.FLAGGED;
				flagsPlaced++;
			}
			else if( (state[row][col] == BoxState.FLAGGED) && shiftPressed) {
				state[row][col] = BoxState.HIDDEN;		
				flagsPlaced--;
			}
			else if( (state[row][col] == BoxState.HIDDEN) && !shiftPressed && mines[row][col] == true ) {
				state[row][col] = BoxState.SHOWN;
				hitMine = true;
				gameInProgress = false;
			}			
		}
		else {

		}		
		draw();
		showData();
	}	
	
	/**
	 * Checks for mines around a given block.
	 * 
	 * Corners: 3 checks.
	 * 
	 * Upper/Lower/Left/Right sides: 5 checks.
	 * 
	 * Center: 8 checks.
	 * 
	 * @param row The row.
	 * @param column The column.
	 * @return The number of mines surrounding the given (row, column).
	 */
	private int checkForMines(int row, int column) {
		int mine = 0;
		
		/********** CORNERS (3 checks) **********/
		/*** Upper left corner. ***/
		if(row == 0 && column == 0) {
			// 1) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;
			// 2) Lower right.
			// row + 1 col + 1
			if(mines[row + 1][column + 1] == true)
				mine += 1;
			// 3) Lower.		
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;
		}
		/*** Lower left corner. ***/
		else if(row == ROWS - 1 && column == 0) {
			// 1) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;
			// 2) Upper right.
			// row - 1 col + 1
			if(mines[row - 1][column + 1] == true)
				mine += 1;
			// 3) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;
		}
		/*** Upper right corner. ***/
		else if(row == 0 && column == COLUMNS - 1) {
			// 1) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;
			// 2) Lower left.
			// row + 1 col - 1
			if(mines[row + 1][column - 1] == true)
				mine += 1;
			// 3) Lower.
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;
		}
		/*** Lower right corner. ***/
		else if( (row == ROWS - 1) && column == COLUMNS - 1) {
			// 1) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;
			// 2) Upper left.
			// row - 1 col - 1
			if(mines[row - 1][column - 1] == true)
				mine += 1;
			// 3) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;
		}
		
		/********** SIDES (5 checks) **********/
		
		/*** Left side. ***/
		else if( (column == 0) && (row > 0) && (row < ROWS - 1)) {
			// 1) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;
			// 2) Upper right.
			// row - 1 col + 1
			if(mines[row - 1][column + 1] == true)
				mine += 1;
			// 3) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;
			// 4) Lower right.
			// row + 1 col + 1
			if(mines[row + 1][column + 1] == true)
				mine += 1;
			// 5) Lower.
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;
		}
		
		/*** Upper. ***/
		else if( (row == 0) && (column > 0) && (column < COLUMNS - 1) ) {
			// 1) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;
			// 2) Lower left.
			// row + 1 col - 1
			if(mines[row + 1][column - 1] == true)
				mine += 1;
			// 3) Lower.
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;
			// 4) Lower right.
			// row + 1 col + 1
			if(mines[row + 1][column + 1] == true)
				mine += 1;
			// 5) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;
		}
		
		/*** Right side. ***/
		else if( (column == COLUMNS - 1) && (row > 0) && (row < ROWS - 1)) {
			// 1) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;
			// 2) Upper left.
			// row - 1 col - 1
			if(mines[row - 1][column - 1] == true)
				mine += 1;
			// 3) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;
			// 4) Lower left.
			// row + 1 col - 1
			if(mines[row + 1][column - 1] == true)
				mine += 1;
			// 5) Lower.
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;
		}
		
		/*** Lower. ***/
		else if( (row == ROWS - 1) && (column > 0) && (column < COLUMNS - 1)) {
			// 1) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;
			// 2) Upper right.
			// row - 1 col + 1
			if(mines[row - 1][column + 1] == true)
				mine += 1;
			// 3) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;
			// 4) Upper left.
			// row - 1 col - 1
			if(mines[row - 1][column - 1] == true)
				mine += 1;
			// 5) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;
		}		
		/*** Rest of boxes. (8 checks) ***/
		else if( row > 0 && row < ROWS - 1 && column > 0 && column < COLUMNS - 1) {			
			// 1) Upper left.
			// row - 1 col - 1
			if(mines[row - 1][column - 1] == true)
				mine += 1;			
			// 2) Upper.
			// row - 1 col
			if(mines[row - 1][column] == true)
				mine += 1;			
			// 3) Upper right.
			// row - 1 col + 1
			if(mines[row - 1][column + 1] == true)
				mine += 1;			
			// 4) Right.
			// row col + 1
			if(mines[row][column + 1] == true)
				mine += 1;			
			// 5) Lower right.
			// row + 1 col + 1
			if(mines[row + 1][column + 1] == true)
				mine += 1;			
			// 6) Lower.
			// row + 1 col
			if(mines[row + 1][column] == true)
				mine += 1;			
			// 7) Lower left.
			// row + 1 col - 1
			if(mines[row + 1][column - 1] == true)
				mine += 1;			
			// 8) Left.
			// row col - 1
			if(mines[row][column - 1] == true)
				mine += 1;			
		}
		
		return mine;
	}
	
	/**
	 * Sets the rows/columns/mines to the correct values based on the difficulty.
	 * @param difficulty The difficulty(beginner, intermediate, or expert);
	 */
	private void setDifficulty(Difficulty difficulty) {
		
		/* Beginner: 12 mines in 10x10 
		 * Intermediate: 35 mines in 15x15 
		 * Expert: 82 mines in 20x20 */
		
		if(difficulty == Difficulty.BEGINNER) {
			this.difficulty = Difficulty.BEGINNER;
			ROWS = 10;
			COLUMNS = 10;
			NUMBER_OF_MINES = (int) ((ROWS * COLUMNS) * .1235); // (12.35%)
		}
		else if(difficulty == Difficulty.INTERMEDIATE) {
			this.difficulty = Difficulty.INTERMEDIATE;
			ROWS = 15;
			COLUMNS = 15;
			NUMBER_OF_MINES = (int) ((ROWS * COLUMNS) * .1563); // (15.63%)
		}
		else {
			this.difficulty = Difficulty.EXPERT;
			ROWS = 20;
			COLUMNS = 20;
			NUMBER_OF_MINES = (int) ((ROWS * COLUMNS) * .2063); // (20.63%)
		}
		
		initialize(); // Initialize the new game, with the new difficulty.
		System.out.println("Number of mines: " + NUMBER_OF_MINES); // Debug statement.
	}
	
    /**
     * Calculates bounds based on the amount of rows and columns.
     */
    private void calculateBounds() {
    	
    	double boxWidth = g.getCanvas().getWidth() / COLUMNS;
    	double boxHeight = g.getCanvas().getHeight() / ROWS;
    	
    	rowBounds = new double[ROWS];
		columnBounds = new double[COLUMNS];
		
		//System.out.println("Bounds:"); // Debug statement.
		double y = 0;
		for(int row = 0; row < ROWS; row++) {
			y += boxHeight;
			rowBounds[row] = y;	
			//System.out.println("Row " + row + ": " + y); // Debug statement.
		}
		
		double x = 0;
		for(int col = 0; col < COLUMNS; col++) {
			x += boxWidth;
			columnBounds[col] = x;
			//System.out.println("Column " + col + ": " + x); // Debug statement.
		}
    }
	
	/**
     * Returns the row number given a y-coordinate.
     * @param y The y-coordinate of the mouse press.
     * @return The row number of the click.
     */
    private int getRow(double y) {
    	int row = 0;	
    	
    	for(int i = 0; i < rowBounds.length; i++) {   		
    		if(y < rowBounds[i]) {
    			row = i;
    			break;
    		}
    	}    	
    	return row;
    }
    
    /**
     * Returns the column number given a x-coordinate.
     * @param x The x-coordinate of the mouse press.
     * @return The column number of the click. 
     */
    private int getCol(double x) {
    	int col = 0; 
    	
    	for(int i = 0; i < columnBounds.length; i++) {   		
    		if(x < columnBounds[i]) {
    			col = i;
    			break;
    		}
    	}   
    	return col;
    }
    
    /*********** Utility functions. ***********/
    
    /**
     *  Show the data from the StatCalc in the four output labels.
     */
    private void showData() {
    	
        clickedLabel.setText("Boxes clicked: " + boxesShown + "/" + (ROWS*COLUMNS - NUMBER_OF_MINES));
        flagsLabel.setText("Flags placed: " + flagsPlaced);     
        
        if(boxesShown == (ROWS * COLUMNS - NUMBER_OF_MINES) && !hitMine && flagsPlaced == NUMBER_OF_MINES) {
        	outcomeLabel.setText("You won!");
        }
        else if(hitMine) {
        	outcomeLabel.setText("You lose!");
        }
        else
        	outcomeLabel.setText("Good luck!");
    }
    
    
    /**
     * Sets the color of the text to be drawn in box based on how many mines there are.
     * @param mines The number of mines.
     * @return The color.
     */
    private Color setTextColor(int mines) {
    	Color color = null;
    	
    	if(mines == 1)
    		color = Color.DARKBLUE;
    	if(mines == 2)
    		color = Color.AQUAMARINE;
    	if(mines == 3)
    		color = Color.YELLOW;
    	if(mines == 4)
    		color = Color.MEDIUMPURPLE;
    	if(mines >= 5)
    		color = Color.RED;
    	
    	return color;
    }
    
    /**
	 * Creates a Menu Bar with the items "New Game" and "Quit".
	 * @return The menu bar.
	 */
	private MenuBar createMenuBar() {
		
		MenuBar menuBar;
		Menu menu;
		MenuItem newGameItem, quitGameItem;
		
		menuBar = new MenuBar();
		
		/* Control Menu */
		menu = new Menu("Control");
		menuBar.getMenus().add(menu);
		
		newGameItem = new MenuItem("New Game");
		menu.getItems().add(newGameItem);
		newGameItem.setOnAction(evt -> doNewGame());
		
		quitGameItem = new MenuItem("Quit");
		menu.getItems().add(quitGameItem);
		quitGameItem.setOnAction(evt -> System.exit(0));
		
		/* Difficulty Menu */
		RadioMenuItem beginnerItem, intermediateItem, expertItem;
		
		ToggleGroup difficultyGroup = new ToggleGroup();
		menu = new Menu("Difficulty");
		menuBar.getMenus().add(menu);
		
		beginnerItem = new RadioMenuItem("Beginner");
        beginnerItem.setOnAction( evt ->  setDifficulty(Difficulty.BEGINNER));
        beginnerItem.setToggleGroup(difficultyGroup);
        beginnerItem.setSelected(true);
        menu.getItems().add(beginnerItem);
        intermediateItem = new RadioMenuItem("Intermediate");
        intermediateItem.setOnAction( evt -> setDifficulty(Difficulty.INTERMEDIATE));
        intermediateItem.setToggleGroup(difficultyGroup);
        menu.getItems().add(intermediateItem);
        expertItem = new RadioMenuItem("Expert");
        expertItem.setOnAction( evt -> setDifficulty(Difficulty.EXPERT));
        expertItem.setToggleGroup(difficultyGroup);
        menu.getItems().add(expertItem);
		
		return menuBar;
	}
	
	/**
	 * A utility routine for creating the labels that are used
	 * for display.  This routine is used in the start() method.
	 */
	private Label makeLabel(String text) {
	   Label label = new Label(text);
	   label.setMaxSize(1000,1000);
	   label.setStyle("-fx-background-color:white; " +
	                        "-fx-font-family: monospace; -fx-font-weight: bold");
	   return label;
	} 

	
}

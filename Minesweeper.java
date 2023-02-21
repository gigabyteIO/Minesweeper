import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
	
	
	/**
	 * The start() method of a JavaFX application sets up the GUI.
	 * and can initialize global variables.
	 */
	public void start(Stage stage) throws Exception {
		Canvas canvas = new Canvas(600, 600);
		g = canvas.getGraphicsContext2D();
		
		BorderPane root = new BorderPane(canvas);
		root.setTop(createMenuBar());
		root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Minesweeper"); 
		stage.show();
		stage.setResizable(false);
		
		canvas.setOnMousePressed( evt -> doMousePressed(evt));
		
		setDifficulty("beginner"); // Initial difficulty is set to beginner.
		initialize();
		//draw();
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
		lineX = boxWidth;
		lineY = boxHeight;
		rectX = 0;
		rectY = 0;
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				
				// Draw vertical lines.
				g.setStroke(Color.BLACK);
				g.strokeLine(lineX, 0, lineX, g.getCanvas().getHeight());
				
				// Draw horizontal lines.
				g.strokeLine(0, lineY, g.getCanvas().getWidth(), lineY);
				
				// Draw boxes.
				g.setFill(Color.DARKGREEN);
				if(state[row][col] == BoxState.HIDDEN)
					g.fillRect(rectX + 1, rectY, boxWidth - 1, boxHeight - 1);
					
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
	
	/**
	 * Initializes all global variables and draw the board.
	 */
	private void initialize() {
		
		/* Beginner: 12 mines in 10x10 
		 * Intermediate: 35 mines in 15x15 
		 * Expert: 82 mines in 20x20 */
		
		mines = new boolean[ROWS][COLUMNS];
		state = new BoxState[ROWS][COLUMNS];
		
		// Set mines.
		
		// Initial state is hidden.
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLUMNS; col++) {
				state[row][col] = BoxState.HIDDEN;
			}
		}
		
		draw();
		
	}
	
	/**
	 * Starts a new game by reseting global variables and redrawing the board.
	 */
	private void doNewGame() {
		draw();
	}
	
	/**
	 * 
	 * @param evt
	 */
	private void doMousePressed(MouseEvent evt ) {
		double x, y;
		
		x = evt.getX();
		y = evt.getY();
		
		// Debug statements.
		System.out.println("X: " + x + "Y: " + y);
		System.out.println("Row: " + getRow(y));
		System.out.println("Column: " + getCol(x));
	}	
	
	/**
	 * Sets the rows/columns/mines to the correct values based on the difficulty.
	 * @param difficulty The difficulty(beginner, intermediate, or expert);
	 */
	private void setDifficulty(String difficulty) {
		
		/* Beginner: 12 mines in 10x10 
		 * Intermediate: 35 mines in 15x15 
		 * Expert: 82 mines in 20x20 */
		
		if(difficulty.equals("beginner")) {
			this.difficulty = Difficulty.BEGINNER;
			ROWS = 10;
			COLUMNS = 10;
			NUMBER_OF_MINES = (int) ((ROWS * COLUMNS) * .1235); // (12.35%)
		}
		else if(difficulty.equals("intermediate")) {
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
		
		System.out.println("Number of mines: " + NUMBER_OF_MINES); // Debug statement.
		initialize();
		calculateBounds();
		draw();
	}
	
	/**
     * Returns the row number given a y-coordinate.
     * @param y The y-coordinate of the mouse press.
     * @return The row number of the click.
     */
    private double getRow(double y) {
    	double row = 0;	
    	
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
    private double getCol(double x) {
    	double col = 0; 
    	
    	for(int i = 0; i < columnBounds.length; i++) {   		
    		if(x < columnBounds[i]) {
    			col = i;
    			break;
    		}
    	}   
    	return col;
    }
    
    /**
     * Calculates bounds based on the amount of rows and columns.
     */
    private void calculateBounds() {
    	
    	double boxWidth = g.getCanvas().getWidth() / COLUMNS;
    	double boxHeight = g.getCanvas().getHeight() / ROWS;
    	
    	rowBounds = new double[ROWS];
		columnBounds = new double[COLUMNS];
		
		System.out.println("Bounds:"); // Debug statement.
		double y = 0;
		for(int row = 0; row < ROWS; row++) {
			y += boxHeight;
			rowBounds[row] = y;	
			System.out.println("Row " + row + ": " + y);
		}
		
		double x = 0;
		for(int col = 0; col < COLUMNS; col++) {
			x += boxWidth;
			columnBounds[col] = x;
			System.out.println("Column " + col + ": " + x);
		}
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
        beginnerItem.setOnAction( evt ->  setDifficulty("beginner"));
        beginnerItem.setToggleGroup(difficultyGroup);
        beginnerItem.setSelected(true);
        menu.getItems().add(beginnerItem);
        intermediateItem = new RadioMenuItem("Intermediate");
        intermediateItem.setOnAction( evt -> setDifficulty("intermediate"));
        intermediateItem.setToggleGroup(difficultyGroup);
        menu.getItems().add(intermediateItem);
        expertItem = new RadioMenuItem("Expert");
        expertItem.setOnAction( evt -> setDifficulty("expert"));
        expertItem.setToggleGroup(difficultyGroup);
        menu.getItems().add(expertItem);
		
		return menuBar;
	}
	
}

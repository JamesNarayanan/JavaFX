import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

public class Grid {
	private Pane grid;
	private Spot[][] gridSpots;
	private Snake snake;
	private boolean start;
	private final int numRows, numCols;
	private final double sideLength;
	private Pane snakeGrid;
	private Timer timer;
	private final int timeInterval;
	
	public Grid() {
		grid = new Pane();
		numRows = 15; numCols = 20;
		gridSpots = new Spot[numCols][numRows];
		timeInterval = 200;
		
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		//double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		
		sideLength = Math.floor(screenHeight/25.0);
		grid.setPrefSize(numCols*sideLength, numRows*sideLength);
		for(int row = 0; row<numRows; row++) {
			for(int col = 0; col<numCols; col++) {
				Rectangle r = new Rectangle(sideLength, sideLength);
				r.setX(col*sideLength);
				r.setY(row*sideLength);
				r.setFill((row+col)%2==0 ? Color.valueOf("#A7D947") : Color.valueOf("#8ECC39"));
				r.setStroke(Color.BLACK);
				r.setStrokeWidth(.25);
				grid.getChildren().add(r);
				gridSpots[col][row] = Spot.EMPTY;
			}
		}
		newSnake();
	}
	
	public Pane getBackgroundGrid() {
		return grid;
	}
	
	public Pane getSnakeGrid() {
		return snakeGrid;
	}
	
	public Group getGrids() {
		return new Group(grid, snakeGrid);
	}
	
	public Snake getSnake() {
		return snake;
	}
	
	public boolean getStart() {
		return start;
	}
	
	public Snake newSnake() {
		start = true;
		snakeGrid = new Pane();
		snakeGrid.setPrefSize(numCols*sideLength, numRows*sideLength);
		snake = new Snake(gridSpots, new int[]{4, numRows/2}, snakeGrid, sideLength);
		return snake;
	}
	
	public static Pane getGridNode (GridPane grid, int row, int column) {
	    Node result = null;

	    for (Node node : grid.getChildren()) {
	        if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
	            result = node;
	            break;
	        }
	    }

	    return (Pane) result;
	}
	
	public void startTimer() {
		start = false;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			boolean stop = false;
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(!stop) {
							if(!snake.move()) {
								System.out.println("Lose");
								stop = true;
							}
						}
					}
				});
			}
		}, 0, timeInterval);
	}
}

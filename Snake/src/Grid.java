import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

public class Grid {
	private Pane grid;
	private int[][] gridSpots;
	private Snake snake;
	private final int numRows, numCols;
	private final double sideLength;
	private Pane snakeGrid;
	private Timer timer;
	
	public Grid() {
		grid = new Pane();
		numRows = 15; numCols = 20;
		gridSpots = new int[numCols][numRows];
		
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		//double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		
		sideLength = screenHeight/25.0;
		for(int row = 0; row<numRows; row++) {
			for(int col = 0; col<numCols; col++) {
				Rectangle r = new Rectangle(sideLength, sideLength);
				r.setX(col*sideLength);
				r.setY(row*sideLength);
				r.setFill(Color.LAWNGREEN);
				/* Does not look consistent, gets some fuzzy lines
				r.setStroke(Color.BLACK);
				r.setStrokeWidth(1.5);*/
				grid.getChildren().add(r);
				gridSpots[col][row] = 0;
			}
		}
		
		for(int i = 0; i<=numCols; i++) {
			Line line = new Line(sideLength*i, 0, sideLength*i, sideLength*numRows); //double startX, double startY, double endX, double endY
			line.setStrokeWidth(1.5);
			grid.getChildren().add(line);
		}
		for(int i = 0; i<=numRows; i++) {
			Line line = new Line(0, sideLength*i, sideLength*numCols, sideLength*i); //double startX, double startY, double endX, double endY
			line.setStrokeWidth(1.5);
			grid.getChildren().add(line);
		}
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
	
	public Snake newSnake() {
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
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				snake.move();
			}
		}, 0, 500);
	}
}

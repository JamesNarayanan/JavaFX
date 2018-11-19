import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.util.Duration;

public class Grid {
	private Pane grid;
	private Spot[][] gridSpots;
	private Snake snake;
	/**
	 * If the game is in the start stage, where movement has not started yet
	 * <br><br>
	 * {@code True} before starting the first time and after losing
	 */
	private boolean start;
	private Pane directionsBox;
	private Pane playAgainBox;
	private final int numRows, numCols;
	private final double sideLength;
	private Pane snakeGrid;
	private Pane scorePane;
	private Timer timer;
	private final int timeInterval;
	
	public Grid() {
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		//double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		
		sideLength = Math.floor(screenHeight/25.0);
		
		grid = new Pane();
		
		numRows = 15; numCols = 20;
		gridSpots = new Spot[numCols][numRows];
		timeInterval = 200;
		
		grid.setPrefSize(numCols*sideLength, numRows*sideLength);
		for(int row = 0; row<numRows; row++) {
			for(int col = 0; col<numCols; col++) {
				Rectangle r = new Rectangle(sideLength, sideLength);
				r.setX(col*sideLength);
				r.setY(row*sideLength);
				r.setFill((row+col)%2==0 ? Color.rgb(167,217,71) : Color.rgb(142,204,57));
				r.setStroke(Color.BLACK);
				r.setStrokeWidth(.25);
				grid.getChildren().add(r);
				gridSpots[col][row] = Spot.EMPTY;
			}
		}
		
		scorePane = new Pane();
		scorePane.setPrefSize(numCols*sideLength, sideLength);
		scorePane.setBackground(new Background(new BackgroundFill(Color.rgb(162,224,60), null, null)));
		grid.setTranslateY(scorePane.getPrefHeight());
		
		Text scoreText = new Text("Score: 0");
		scoreText.setTextOrigin(VPos.CENTER);
		scoreText.setFont(Font.font("Roboto", sideLength*.8));
		scoreText.setFill(Color.WHITE);
		scoreText.setY(scorePane.getPrefHeight()/2);
		scoreText.setWrappingWidth(scorePane.getPrefWidth());
		scoreText.setTextAlignment(TextAlignment.CENTER);
		scorePane.getChildren().add(scoreText);
		
		directionsBox = new Pane();
		directionsBox.setPrefSize(sideLength*8.5, sideLength*1.5);
		directionsBox.setTranslateY(sideLength*.75);
		directionsBox.setTranslateX((sideLength*numCols - directionsBox.getPrefWidth())/2);
		directionsBox.setBackground(new Background(new BackgroundFill(Color.gray(.3), new CornerRadii(5), null)));
		
		Text directions = new Text("Press any key to start!");
		directions.setTextOrigin(VPos.CENTER);
		directions.setFont(Font.font("Roboto", sideLength*.8));
		directions.setFill(Color.WHITE);
		directions.setY(directionsBox.getPrefHeight()/2);
		directions.setWrappingWidth(directionsBox.getPrefWidth());
		directions.setTextAlignment(TextAlignment.CENTER);
		directionsBox.getChildren().add(directions);
		
		playAgainBox = new Pane();
		playAgainBox.setPrefSize(sideLength*8.5, sideLength*5.5);
		playAgainBox.setTranslateY((sideLength*numRows - playAgainBox.getPrefHeight())/2);
		playAgainBox.setTranslateX((sideLength*numCols - playAgainBox.getPrefWidth())/2);
		playAgainBox.setBackground(new Background(new BackgroundFill(Color.BLUE, new CornerRadii(10), null)));
		playAgainBox.setOpacity(0);
		
		Text playAgain = new Text();
		playAgain.setTextOrigin(VPos.CENTER);
		playAgain.setFont(Font.font("Roboto", sideLength*.8));
		playAgain.setFill(Color.WHITE);
		playAgain.setY(playAgainBox.getPrefHeight()/2);
		playAgain.setWrappingWidth(playAgainBox.getPrefWidth());
		playAgain.setTextAlignment(TextAlignment.CENTER);
		playAgainBox.getChildren().add(playAgain);
		
	}
	
	public Group getPanes() {
		return new Group(scorePane, grid, snakeGrid);
	}
	
	public Snake getSnake() {
		return snake;
	}
	
	public boolean getStart() {
		return start;
	}
	
	public Snake newSnake() {
		start = true;
		playAgainBox.setOpacity(0);
		snakeGrid = new Pane();
		snakeGrid.setTranslateY(scorePane.getPrefHeight());
		snakeGrid.getChildren().addAll(directionsBox, playAgainBox);
		snakeGrid.setPrefSize(numCols*sideLength, numRows*sideLength);
		snake = new Snake(gridSpots, new int[]{4, numRows/2}, snakeGrid, sideLength);
		directionsBox.toFront();
		return snake;
	}
	
	public void startTimer() {
		start = false;
		fade(directionsBox, 500, true);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(!start) {
							if(!snake.move()) {
								System.out.println("Lose");
								playAgain();
							}
							else {
								((Text) (scorePane.getChildren().get(0))).setText("Score: " + snake.getScore());
							}
						}
					}
				});
			}
		}, 0, timeInterval);
	}
	
	private void playAgain() {
		playAgainBox.toFront();
		((Text) (playAgainBox.getChildren().get(0))).setText("Score: " + snake.getScore() + "\n\n↻ Play Again?");
		fade(playAgainBox, 200, false);
		timer.cancel();
		timer.purge();
		timer = new Timer();
		start = true;
		gridSpots = new Spot[numCols][numRows];
	}
	
	private void fade(Node node, double millis, boolean fadeOut) {
		FadeTransition fade = new FadeTransition(Duration.millis(millis), node);
		if(fadeOut) {
			fade.setFromValue(1.0);
			fade.setToValue(0.0);
		}
		else {
			fade.setFromValue(0.0);
			fade.setToValue(1.0);
		}
		fade.play();
	}
}

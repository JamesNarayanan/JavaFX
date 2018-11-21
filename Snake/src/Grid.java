import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private Pane pausePane;
	private Timer timer;
	private boolean paused;
	private int highScore;
	private final int timeInterval;
	private FadeTransition fadeTrans;
	
	public Grid() {
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		//double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		
		sideLength = Math.floor(screenHeight/25.0);
		
		grid = new Pane();
		
		numRows = 15; numCols = 20;
		gridSpots = new Spot[numCols][numRows];
		paused = false;
		timeInterval = 150;
		highScore = 0;
		
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
		scorePane.setPrefSize(numCols*sideLength, sideLength*1.5);
		scorePane.setBackground(new Background(new BackgroundFill(Color.rgb(162,224,60), null, null)));
		grid.setTranslateY(scorePane.getPrefHeight());
		
		pausePane = new Pane();
		pausePane.setPrefSize(numCols*sideLength, scorePane.getPrefHeight() + numRows*sideLength);
		pausePane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		pausePane.setOpacity(0);
		
		//Image taken from Google's snake game
		ImageView apple = null;
		try {
			apple = new ImageView(new Image(new FileInputStream("Images/food.png")));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		apple.setFitWidth(sideLength); apple.setFitHeight(sideLength);
		apple.setX(scorePane.getPrefWidth()/2 - sideLength*2.25);
		apple.setY((scorePane.getPrefHeight()-sideLength)/2 - sideLength/25);
		
		//Image taken from Google's snake game
		ImageView trophy = null;
		try {
			trophy = new ImageView(new Image(new FileInputStream("Images/trophy.png")));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		trophy.setFitWidth(sideLength); trophy.setFitHeight(sideLength);
		trophy.setX(scorePane.getPrefWidth()/2 + sideLength*1.25);
		trophy.setY((scorePane.getPrefHeight()-sideLength)/2 - sideLength/25);
		
		Text scoreText = new Text("0\t0");
		scoreText.setTextOrigin(VPos.CENTER);
		scoreText.setFont(Font.font("Roboto", sideLength*.8));
		scoreText.setFill(Color.WHITE);
		scoreText.setY(scorePane.getPrefHeight()/2);
		scoreText.setWrappingWidth(scorePane.getPrefWidth());
		scoreText.setTextAlignment(TextAlignment.CENTER);
		scorePane.getChildren().addAll(scoreText, apple, trophy);
		
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
		
		ImageView pAApple = null;
		try {
			pAApple = new ImageView(new Image(new FileInputStream("Images/food.png")));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		pAApple.setFitWidth(sideLength); pAApple.setFitHeight(sideLength);
		pAApple.setX(playAgainBox.getPrefWidth()/2 - sideLength*1.4);
		pAApple.setY((playAgainBox.getPrefHeight()-sideLength)/5.5);
		
		ImageView pATrophy = null;
		try {
			pATrophy = new ImageView(new Image(new FileInputStream("Images/trophy.png")));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		pATrophy.setFitWidth(sideLength); pATrophy.setFitHeight(sideLength);
		pATrophy.setX(playAgainBox.getPrefWidth()/2 - sideLength*1.4);
		pATrophy.setY((playAgainBox.getPrefHeight()-sideLength)/2.45);
		
		Text playAgain = new Text();
		playAgain.setTextOrigin(VPos.CENTER);
		playAgain.setFont(Font.font("Roboto", sideLength*.8));
		playAgain.setFill(Color.WHITE);
		playAgain.setY(playAgainBox.getPrefHeight()/2);
		playAgain.setWrappingWidth(playAgainBox.getPrefWidth());
		playAgain.setTextAlignment(TextAlignment.CENTER);
		playAgainBox.getChildren().addAll(playAgain, pAApple, pATrophy);
		
	}
	
	public Group getPanes() {
		return new Group(scorePane, grid, snakeGrid, pausePane);
	}
	
	public Snake getSnake() {
		return snake;
	}
	
	public boolean getStart() {
		return start;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public Snake newSnake() {
		start = true;
		if(fadeTrans!=null)
			fadeTrans.stop();
		playAgainBox.setOpacity(0);
		directionsBox.setOpacity(1);
		((Text) (scorePane.getChildren().get(0))).setText("0\t" + highScore);
		
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
								playAgain();
							}
							else {
								if(snake.getScore()>highScore)
									highScore = snake.getScore();
								((Text) (scorePane.getChildren().get(0))).setText("" + snake.getScore() + "\t" + highScore);
							}
						}
					}
				});
			}
		}, 0, timeInterval);
	}
	
	public void pause() {
		timer.cancel();
		paused = true;
		pausePane.toFront();
		pausePane.setOpacity(.3);
	}
	
	public void resume() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() { //Have to repeat code because a TimerTask cannot be rescheduled
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(!start) {
							if(!snake.move()) {
								playAgain();
							}
							else {
								if(snake.getScore()>highScore)
									highScore = snake.getScore();
								((Text) (scorePane.getChildren().get(0))).setText("" + snake.getScore() + "\t" + highScore);
							}
						}
					}
				});
			}
		}, 0, timeInterval);
		paused = false;
		pausePane.setOpacity(0);
	}
	
	private void playAgain() {
		start = true;
		playAgainBox.toFront();
		((Text) (playAgainBox.getChildren().get(0))).setText("" + snake.getScore() + "\n" + highScore + "\n\n↻ Play Again?");
		fadeTrans = fade(playAgainBox, 200, false);
		
		timer.cancel();
		timer.purge();
		
		for(int row = 0; row<numRows; row++) {
			for(int col = 0; col<numCols; col++) {
				gridSpots[col][row] = Spot.EMPTY;
			}
		}
	}
	
	private FadeTransition fade(Node node, double millis, boolean fadeOut) {
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
		return fade;
	}
}

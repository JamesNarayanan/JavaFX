
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Tetris JavaFX Game
 * @author James
 * @since November 2018
 */
public class Tetris extends Application {
	private final Color mainColor = Color.INDIANRED;
	private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
	private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
	
	private static Stage mainStage;
	private final String font = "Roboto";
	
	private final Color rectBg = Color.rgb(45,45,45);
	
	private final int rowLength = 10; //Number of blocks per row
	private final int numRows = 21;
	private final double sideLength = screenHeight/25.0;
	
	private int score;
	private Text scoreText;
	private int lines;
	private Text lineText;
	private Rectangle[] rect;
	private Integer[] rows;
	private Integer[] cols;
	private boolean[] placed;
	private Timer timer;
	private boolean wait;
	private enum Direction {LEFT, RIGHT, DOWN}
	private enum BlockType {STRAIGHT, SQUARE, T, J, L, S, Z}
	private BlockType blockType;
	private BlockType[] nextBlocks;
	private Rectangle[][] nextBlocksRect;
	private double defaultTrans;
	private String highScoreEndText;
	private double mainSceneWidth;
	private double mainSceneHeight;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Initiallizes variables
	 */
	@Override
	public void init() {
		score = 0;
		lines = 0;
		rect = new Rectangle[rowLength*numRows];
		rows = new Integer[4];
		cols = new Integer[4];
		placed = new boolean[rect.length];
		timer = new Timer();
		wait = true;
		nextBlocks = new BlockType[3]; //Cannot be less than 1
		nextBlocksRect = new Rectangle[nextBlocks.length][8];
	}
	
	/**
	 * Starts the game and sets {@link Stage} properties
	 */
	@Override
	public void start(Stage theStage) throws Exception {
		mainStage = theStage;
		mainStage.setScene(initScene());
		mainStage.setResizable(false);
		mainStage.setTitle("Tetris");
		mainStage.show();
	}
	
	/**
	 * The welcome screen
	 * @return The initial {@link Scene}
	 */
	private Scene initScene() {
		Pane init = new Pane();
		init.setBackground(new Background(new BackgroundFill(mainColor, null, null)));
		init.setPrefSize(screenWidth, screenHeight);
		EventHandler<Event> switchToMain = new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				mainStage.setScene(mainScene());
			}
		};
		init.setOnMouseClicked(switchToMain);
		
		Text welcome = new Text("Welcome to Tetris!");
		welcome.setWrappingWidth(screenWidth);
		welcome.setTextAlignment(TextAlignment.CENTER);
		welcome.setFont(Font.font(font, screenWidth/15.0));
		welcome.setY(screenHeight/2.2);
		
		Text directions = new Text("Press anything to play");
		directions.setWrappingWidth(screenWidth);
		directions.setTextAlignment(TextAlignment.CENTER);
		directions.setFont(Font.font(welcome.getFont().getFamily(), welcome.getFont().getSize()/2.0));
		directions.setY(screenHeight/1.9);
		
		init.getChildren().addAll(welcome, directions);
		Scene scene = new Scene(init);
		scene.setOnKeyPressed(switchToMain);
		return scene;
		
	}
	
	/**
	 * Generates the main scene where the game is played
	 * @return The main {@link Scene}
	 */
	private Scene mainScene() {
		lineText = new Text("Lines: " + lines);
		lineText.setFill(mainColor);
		lineText.setTextOrigin(VPos.TOP);
		int fontSize = (int) (sideLength/1.1);
		lineText.setStyle("-fx-font: " + fontSize + " " + font + ";"); //Uses CSS
		lineText.setX(sideLength/5);
		lineText.setY(lineText.getX());
		
		scoreText = new Text("Score: " + score);
		scoreText.setFill(mainColor);
		scoreText.setTextOrigin(VPos.TOP);
		scoreText.setStyle("-fx-font: " + fontSize + " " + font + ";"); //Uses CSS
		scoreText.setX(lineText.getX() + sideLength*4.5);
		scoreText.setY(lineText.getY());
		
		Text highScoreText = new Text();
		try {
			highScoreText = new Text("High Score: " + highScores(false).get(0));
		} catch (IOException e) {e.printStackTrace();}
		highScoreText.setFill(mainColor);
		highScoreText.setTextOrigin(VPos.TOP);
		highScoreText.setStyle("-fx-font: " + fontSize + " " + font + ";"); //Uses CSS
		highScoreText.setX(lineText.getX());
		highScoreText.setY(lineText.getY() + sideLength);
		
		Pane top = new Pane(lineText, scoreText, highScoreText);
		//Three lines in height
		top.setPrefSize(sideLength*rowLength, sideLength*3); //Width, Height
		top.setBackground(new Background(new BackgroundFill(rectBg, null, null)));
		
		int rowNum = 0;
		int colNum = 0;
		
		for(int i = 0; i<rect.length; i++) {
			rect[i] = new Rectangle(sideLength, sideLength); //Width, Height
			
			rect[i].setX(sideLength*colNum);
			rect[i].setY(sideLength*rowNum);
			
			rect[i].setFill(rectBg);
			rect[i].setStroke((i<rowLength) ? Color.WHITE : Color.GREY);
			
			if(colNum==rowLength-1) { //If the row has been filled
				colNum=0;
				rowNum++;
			}
			else
				colNum++;
		}
		
		
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() { //Allows scene switching
					public void run() {
						if(wait) {
							newBlock();
							wait=false;
						}
						else {
							if(canMoveBlock(Direction.DOWN))
								moveBlock(Direction.DOWN, 0); //Multiplier of 0 because it's not worth any points
							else {
								newBlock();
							}
						}
					}
				});
			}
		}, 3000, 100); //Starts after 3 seconds, moves every 1
		
		Pane rectPane = new Pane(rect);
		rectPane.setPrefSize(sideLength*rowLength, sideLength*numRows); //Width, Height
		
		VBox vbox = new VBox(top, rectPane);
		
		
		Text nextBlocksText = new Text("Next Blocks");
		nextBlocksText.setTextAlignment(TextAlignment.CENTER);
		nextBlocksText.setTextOrigin(VPos.TOP);
		nextBlocksText.setStyle("-fx-font: " + fontSize/1.2 + " " + font + ";");
		nextBlocksText.setFill(mainColor);
		nextBlocksText.setY(sideLength/5);
		
		Pane nextBlocks = new Pane(nextBlocksText);
		nextBlocks.setPrefSize(sideLength*7, (this.nextBlocks.length*3+2)*sideLength);
		nextBlocksText.setWrappingWidth(nextBlocks.getPrefWidth());
		nextBlocks.setBorder(new Border(new BorderStroke(
			mainColor, 
			new BorderStrokeStyle(StrokeType.CENTERED, null, null, 10, 0, null), 
			new CornerRadii(10), 
			new BorderWidths(5))
		));
		
		colNum = 0;
		rowNum = 0;
		defaultTrans = (nextBlocks.getPrefWidth()-sideLength*4)/2;
		//Necessary for displayNextBlocks()
		for(int i = 0; i<nextBlocksRect.length; i++) {
			for(int j = 0; j<8; j++) {
				nextBlocksRect[i][j] = new Rectangle(sideLength, sideLength);
				nextBlocksRect[i][j].setStroke(rectBg);
				nextBlocksRect[i][j].setFill(rectBg);
				nextBlocksRect[i][j].setX(sideLength*colNum);
				nextBlocksRect[i][j].setY(sideLength*rowNum);
				nextBlocksRect[i][j].setTranslateY(sideLength*(i+2));
				nextBlocksRect[i][j].setTranslateX(defaultTrans);
				
				if(colNum==3) {
					colNum=0;
					rowNum++;
				}
				else
					colNum++;
			}
		}
		for(int i = 0; i<nextBlocksRect.length; i++)
			nextBlocks.getChildren().addAll(nextBlocksRect[i]);
		
		AnchorPane sidePanel = new AnchorPane(nextBlocks);
		sidePanel.setPrefSize(sideLength*8, top.getPrefHeight()+rectPane.getPrefHeight());
		sidePanel.setBackground(new Background(new BackgroundFill(rectBg, null, null)));
		AnchorPane.setTopAnchor(nextBlocks, top.getPrefHeight());
		AnchorPane.setLeftAnchor(nextBlocks, (sidePanel.getPrefWidth()-nextBlocks.getPrefWidth())/2);
		
		HBox hbox = new HBox(vbox, sidePanel);
		
		mainSceneWidth = top.getPrefWidth()+sidePanel.getPrefWidth();
		mainSceneHeight = sidePanel.getPrefHeight();
		Pane startCover = new Pane();
		startCover.setPrefSize(mainSceneWidth, mainSceneHeight);
		startCover.setBackground(new Background(new BackgroundFill(rectBg, null, null)));
		Scene startScene = new Scene(startCover);
		
		timer.scheduleAtFixedRate(new TimerTask() {
			int countdown = 4;
			Text countdownText = new Text("" + countdown);
			boolean first = true;
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if(countdown==0) {
							return;
						}
						if(first) {
							startCover.getChildren().add(countdownText);
							countdownText.setTextOrigin(VPos.CENTER);
							countdownText.setTextAlignment(TextAlignment.CENTER);
							countdownText.setWrappingWidth(startCover.getPrefWidth());
							countdownText.setY(startCover.getPrefHeight()/2);
							countdownText.setStyle("-fx-font: " + fontSize*5 + " " + font + ";");
							countdownText.setFill(mainColor);
							first = false;
						}
						countdown--;
						countdownText.setText("" + countdown);
					}
				});
			}
		}, 0, 1000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Scene scene = new Scene(hbox);
						scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
							@Override
							public void handle(KeyEvent event) {
								//System.out.println(event.getCode());
								if(!wait) {
									if(event.getCode()==KeyCode.DOWN) {
										if(canMoveBlock(Direction.DOWN))
											moveBlock(Direction.DOWN, 1);
										else
											newBlock();
									}
									else if(event.getCode()==KeyCode.UP) {
										rotateBlock();
									}
									else if(event.getCode()==KeyCode.LEFT) {
										if(canMoveBlock(Direction.LEFT))
											moveBlock(Direction.LEFT, 0);
									}
									else if(event.getCode()==KeyCode.RIGHT) {
										if(canMoveBlock(Direction.RIGHT))
											moveBlock(Direction.RIGHT, 0);
									}
									else if(event.getCode()==KeyCode.SPACE) {
										while(canMoveBlock(Direction.DOWN))
											moveBlock(Direction.DOWN, 2);
									}
								}
							}
						});
						mainStage.setScene(scene);
					} 
				});
			}
		}, 3000);
		return startScene;
	}
	
	/**
	 * Generates a new block by changing the color of one of the top blocks
	 * @see <a href="https://en.wikipedia.org/wiki/Tetromino">Tetromino Wikipedia Page</a>
	 * @return The column of the new block
	 */
	private void newBlock() { //Tetriminos always spawn with 3 empty blocks to the left
		if(nextBlocks[0]==null) { //First time
			for(int i = 0; i<nextBlocks.length; i++) {
				switch((int)(Math.random()*7)) { //*7 Because there are 7 different tetriminos and it is 0 referenced
				case 0: //Straight
					nextBlocks[i] = BlockType.STRAIGHT;
					break;
				case 1: //Square
					nextBlocks[i] = BlockType.SQUARE;
					break;
				case 2: //T
					nextBlocks[i] = BlockType.T;
					break;
				case 3: //J
					nextBlocks[i] = BlockType.J;
					break;
				case 4: //L
					nextBlocks[i] = BlockType.L;
					break;
				case 5: //S
					nextBlocks[i] = BlockType.S;
					break;
				case 6: //Z
					nextBlocks[i] = BlockType.Z;
					break;
				}
			}
		}
		
		Color color;
		switch(nextBlocks[0]) {
		case STRAIGHT: //Straight
			color = Color.CYAN;
			for(int i = 0; i<rows.length; i++)
				rows[i] = 0;
			for(int i = 0; i<rows.length; i++)
				cols[i] = i+3;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[6].setFill(color);
			break;
		case SQUARE: //Square
			color = Color.YELLOW;
			rows[0] = 0; rows[1] = 0; rows[2] = 1; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 3; cols[3] = 4;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[3+rowLength].setFill(color);
			rect[4+rowLength].setFill(color);
			break;
		case T: //T
			color = Color.MAGENTA;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 4;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[4+rowLength].setFill(color);
			break;
		case J: //J
			color = Color.DODGERBLUE;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 5;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[5+rowLength].setFill(color);
			break;
		case L: //L
			color = Color.ORANGE;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 3;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[3+rowLength].setFill(color);
			break;
		case S: //S
			color = Color.LIME;
			rows[0] = 1; rows[1] = 1; rows[2] = 0; rows[3] = 0;
			cols[0] = 3; cols[1] = 4; cols[2] = 4; cols[3] = 5;
			rect[3+rowLength].setFill(color);
			rect[4+rowLength].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			break;
		case Z: //Z
			color = Color.RED;
			rows[0] = 0; rows[1] = 0; rows[2] = 1; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 4; cols[3] = 5;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[4+rowLength].setFill(color);
			rect[5+rowLength].setFill(color);
			break;
		}
		blockType = nextBlocks[0];
		for(int i = 0; i<nextBlocks.length-1; i++) {
			nextBlocks[i] = nextBlocks[i+1];
		}
		switch((int)(Math.random()*7)) { //Assigns new value
		case 0: //Straight
			nextBlocks[nextBlocks.length-1] = BlockType.STRAIGHT;
			break;
		case 1: //Square
			nextBlocks[nextBlocks.length-1] = BlockType.SQUARE;
			break;
		case 2: //T
			nextBlocks[nextBlocks.length-1] = BlockType.T;
			break;
		case 3: //J
			nextBlocks[nextBlocks.length-1] = BlockType.J;
			break;
		case 4: //L
			nextBlocks[nextBlocks.length-1] = BlockType.L;
			break;
		case 5: //S
			nextBlocks[nextBlocks.length-1] = BlockType.S;
			break;
		case 6: //Z
			nextBlocks[nextBlocks.length-1] = BlockType.Z;
			break;
		}
		displayNextBlocks();
		//System.out.println(Arrays.toString(nextBlocks));
		
		fixPlaced();
	}
	
	private void displayNextBlocks() {
		for(int n = 0; n<nextBlocksRect.length; n++) //Resets the colors
			for(int i = 0; i<8; i++)
				nextBlocksRect[n][i].setFill(rectBg);
		
		Color color;
		for(int n = 0; n<nextBlocksRect.length; n++) {
			switch(nextBlocks[n]) {
			case STRAIGHT: //Straight
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans);
				color = Color.CYAN;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][2].setFill(color);
				nextBlocksRect[n][3].setFill(color);
				break;
			case SQUARE: //Square
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength);
				color = Color.YELLOW;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][4].setFill(color);
				nextBlocksRect[n][5].setFill(color);
				break;
			case T: //T
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength/2);
				color = Color.MAGENTA;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][2].setFill(color);
				nextBlocksRect[n][5].setFill(color);
				break;
			case J: //J
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength/2);
				color = Color.DODGERBLUE;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][2].setFill(color);
				nextBlocksRect[n][6].setFill(color);
				break;
			case L: //L
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength/2);
				color = Color.ORANGE;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][2].setFill(color);
				nextBlocksRect[n][4].setFill(color);
				break;
			case S: //S
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength/2);
				color = Color.LIME;
				nextBlocksRect[n][4].setFill(color);
				nextBlocksRect[n][5].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][2].setFill(color);
				break;
			case Z: //Z
				for(int i = 0; i<8; i++)
					nextBlocksRect[n][i].setTranslateX(defaultTrans+sideLength/2);
				color = Color.RED;
				nextBlocksRect[n][0].setFill(color);
				nextBlocksRect[n][1].setFill(color);
				nextBlocksRect[n][5].setFill(color);
				nextBlocksRect[n][6].setFill(color);
				break;
			}
		}
	}
	
	/**
	 * Will move the tetrimino in the desired direction
	 * Assumes block can be moved in the desired direction
	 * @param dir The direction to move the block in
	 * @param multiplier The amount of points scored for every move down
	 */
	private void moveBlock(Direction dir, int multiplier) {
		if(dir==Direction.DOWN) {
			ArrayList<Integer> moveList = new ArrayList<>();
			for(int i = 0; i<4; i++) {
				Paint c = rect[cols[i] + rows[i]*rowLength].getFill();
				if(!moveList.contains(cols[i] + rows[i]*rowLength))
					rect[cols[i] + rows[i]*rowLength].setFill(rectBg);
				rect[cols[i] + (rows[i]+1)*rowLength].setFill(c);
				moveList.add(cols[i] + (rows[i]+1)*rowLength);
				rows[i]++;
			}
				score+=multiplier;
				scoreText.setText("Score: " + score);
		}
		else if(dir==Direction.LEFT) {
			ArrayList<Integer> moveList = new ArrayList<>();
			for(int i = 0; i<4; i++) {
				Paint c = rect[cols[i] + rows[i]*rowLength].getFill();
				if(!moveList.contains(cols[i] + rows[i]*rowLength))
					rect[cols[i] + rows[i]*rowLength].setFill(rectBg);
				rect[cols[i]-1 + rows[i]*rowLength].setFill(c);
				moveList.add(cols[i]-1 + rows[i]*rowLength);
				cols[i]--;
			}
		}
		else if(dir==Direction.RIGHT) {
			ArrayList<Integer> moveList = new ArrayList<>();
			for(int i = 3; i>=0; i--) { //Has to go right to left in array for it to work
				Paint c = rect[cols[i] + rows[i]*rowLength].getFill();
				if(!moveList.contains(cols[i] + rows[i]*rowLength))
					rect[cols[i] + rows[i]*rowLength].setFill(rectBg);
				rect[cols[i]+1 + rows[i]*rowLength].setFill(c);
				moveList.add(cols[i]+1 + rows[i]*rowLength);
				cols[i]++;
			}
		}
	}
	
	/**
	 * Tests if the block can move in the desired direction
	 * @param dir The direction for the block to move in
	 * @return If the block can move in the desired direction
	 */
	private boolean canMoveBlock(Direction dir) {
		if(dir==Direction.DOWN) {
			for(int i = 0; i<4; i++) { //Using <4 because that will always be rows.length
				if(rows[i]==numRows-1 || placed[cols[i]+(rows[i]+1)*rowLength]==true) {
					for(int j = 0; j<4; j++) //Adds the block to placed[]
						placed[cols[j]+rows[j]*rowLength] = true;
					if(checkLoss()) {
						//System.out.println("You lost with a score of " + score + "!");
						try {
							highScores(true);
						} catch (IOException e) {e.printStackTrace();}
						//System.exit(0);
						timer.cancel();
						timer.purge();
						mainStage.setScene(loseScene());
					}
					removeFullRows();
					return false;
				}
			}
		}
		else if(dir==Direction.LEFT) {
			for(int i = 0; i<4; i++) { //Using <4 because that will always be rows.length
				if(cols[i]==0 || placed[cols[i]-1+rows[i]*rowLength]==true)
					return false;
			}
		}
		else if(dir==Direction.RIGHT) {
			for(int i = 0; i<4; i++) { //Using <4 because that will always be rows.length
				if(cols[i]==rowLength-1 || placed[cols[i]+1+rows[i]*rowLength]==true)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Rotates the block counterclockwise
	 * @see <a href="https://www.youtube.com/watch?v=Atlr5vvdchY">Source</a>
	 */
	private void rotateBlock() {///<3
		int[] pivotPoint = new int[2];
		int blockNum = 0;
		switch(blockType) {
		case STRAIGHT:
			blockNum = 0;
			break;
		case SQUARE:
			blockNum = 1;
			break;
		case T:
			blockNum = 1;
			break;
		case J:
			blockNum = 1;
			break;
		case L:
			blockNum = 0;
			break;
		case S:
			blockNum = 1;
			break;
		case Z:
			blockNum = 1;
			break;
		}
		pivotPoint[0] = rows[blockNum]; pivotPoint[1] = cols[blockNum];
		if(!canRotate(blockNum, pivotPoint))
			return;
		ArrayList<Integer> moveList = new ArrayList<>();
		for(int i = 0; i<4; i++) {
			if(i!=blockNum) {
				int[] point = {rows[i], cols[i]};
				int[] Vr = {point[0]-pivotPoint[0], point[1]-pivotPoint[1]};
				int[][] R = { {0, -1} , {1, 0} };
				int[] Vt = { R[0][0]*Vr[0] + R[0][1]*Vr[1] , R[1][0]*Vr[0] + R[1][1]*Vr[1] };
				int[] newPoint = { pivotPoint[0] + Vt[0] , pivotPoint[1] + Vt[1] };
				
				Paint color = rect[rows[i]*rowLength + cols[i]].getFill();
				if(!moveList.contains(cols[i] + rows[i]*rowLength))
					rect[rows[i]*rowLength + cols[i]].setFill(rectBg);
				rows[i] = newPoint[0];
				cols[i] = newPoint[1];
				rect[rows[i]*rowLength + cols[i]].setFill(color);
				moveList.add(rows[i]*rowLength + cols[i]);
			}
		}
	}
	
	/**
	 * Checks if the block can be rotated
	 * @param blockNum The number of the block in the arrays of {@link rows} and {@link cols}
	 * @param pivotPoint The point which the rotation pivots around
	 * @return If the block can be rotated
	 */
	private boolean canRotate(int blockNum, int[] pivotPoint) {
		for(int i = 0; i<4; i++) {
			if(i!=blockNum) {
				int[] point = {rows[i], cols[i]};
				int[] Vr = {point[0]-pivotPoint[0], point[1]-pivotPoint[1]};
				int[][] R = { {0, -1} , {1, 0} };
				int[] Vt = { R[0][0]*Vr[0] + R[0][1]*Vr[1] , R[1][0]*Vr[0] + R[1][1]*Vr[1] };
				int[] newPoint = { pivotPoint[0] + Vt[0] , pivotPoint[1] + Vt[1] };
				
				if(newPoint[0]<0 || newPoint[0]>=numRows || newPoint[1]<0 || newPoint[1]>=rowLength || placed[newPoint[0]*rowLength+newPoint[1]])
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the player lost the game
	 * @return True if the player lost
	 */
	private boolean checkLoss() {
		for(int i = 0; i<rowLength; i++)
			if(placed[i]==true)
				return true;
		return false;
	}
	
	/**
	 * Gets and returns the high scores from the text file
	 * @param update If the high scores list should should be updated with the current score
	 * @return The high scores
	 * @throws IOException
	 */
	private ArrayList<String> highScores(boolean update) throws IOException {
		String scores = new String (Files.readAllBytes(Paths.get("HighScores.txt")));
		
		ArrayList<String> scoresList = new ArrayList<>(Arrays.asList(scores.split("\n")));
		if(!update)
			return scoresList;
		
		int high = -1;
		
		for(int i = 0; i<scoresList.size(); i++) {
			if(score>Integer.parseInt(scoresList.get(i))) {
				switch(i) {
				case 0:
					highScoreEndText = "You got a new high score!";
					break;
				case 1:
					highScoreEndText = "That's your second highest score!";
					break;
				case 2:
					highScoreEndText = "That's your third highest score!";
					break;
				case 3:
					highScoreEndText = "That's your fourth highest score!";
					break;
				case 4:
					highScoreEndText = "That's your fifth highest score!";
					break;
				}
				
				high = i;
				i = scoresList.size()-1; //This ends the for loop
			}
		}
		
		if(high!=-1) {
			scoresList.add(high, "" + score);
			scoresList.remove(5);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("HighScores.txt"));
			for(String str : scoresList)
				writer.write(str + "\n");
			writer.close();
		}
		else
			highScoreEndText = "No new high score :(";
		return scoresList;
	}
	
	/**
	 * Fixes problems with {@link placed} created when rows are removed
	 * and stops any block from floating
	 */
	private void fixPlaced() {
		boolean go = true;
		for(int i = 0; i<placed.length; i++) {
			for(int j = 0; j<4; j++) {
				if(i==cols[j] + rows[j]*rowLength)
					go = false;
			}
			if(go)
				placed[i] = (rect[i].getFill()==rectBg) ? false : true;
			go = true;
		}
	}
	
	/**
	 * Removes a full row and adjusts {@link placed} and {@link line} values
	 */
	private void removeFullRows() {
		int linesRemoved = 0;
		for(int r = 1; r<=numRows; r++) {
			boolean remove = true;
			for(int i = r*rowLength-1; i>=r*rowLength - rowLength; i--) {
				if(rect[i].getFill().equals(rectBg)) //Will not remove if row is not full
					remove = false;
			}
			if(remove) { //Remove that row
				for(int i = r*rowLength-1; i>=0; i--) {
					if(i<rowLength) {//If it's looping through the top row
						rect[i].setFill(rectBg);
						placed[i] = false;
					}
					else {
						rect[i].setFill(rect[i-rowLength].getFill());
						placed[i] = placed[i-rowLength];
					}
				}
				lines++;
				lineText.setText("Lines: " + lines);
				linesRemoved++;
			}
		}
		switch(linesRemoved) {
		case 1:
			score+=40;
			break;
		case 2:
			score+=100;
			break;
		case 3:
			score+=300;
			break;
		case 4:
			score+=1200;
			break;
		}
		scoreText.setText("Score: " + score);
	}

	private Scene loseScene() {
		Pane panel = new Pane();
		panel.setPrefSize(mainSceneWidth, mainSceneHeight);
		panel.setBackground(new Background(new BackgroundFill(rectBg, null, null)));
		
		Pane loss = new Pane();
		loss.setPrefSize(panel.getPrefWidth(), 2*panel.getPrefHeight()/3);
		Text lossText = new Text("You lost with a score of " + score + "\n\n" + highScoreEndText);
		lossText.setFill(mainColor);
		lossText.setStyle("-fx-font: " + sideLength*1.5 + " " + font + ";");
		lossText.setTextAlignment(TextAlignment.CENTER);
		lossText.setWrappingWidth(loss.getPrefWidth()*.9);
		lossText.setX((loss.getPrefWidth()-lossText.getWrappingWidth())/2);
		lossText.setY(loss.getPrefHeight()/3);
		loss.getChildren().add(lossText);
		panel.getChildren().add(loss);
		
		Pane playAgain = new Pane();
		playAgain.setPrefSize(2*panel.getPrefWidth()/3, panel.getPrefHeight()-loss.getPrefHeight());
		playAgain.setTranslateY(loss.getPrefHeight());
		playAgain.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				init();
				mainStage.setScene(mainScene());
			}
		});
		playAgain.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				playAgain.setOpacity(.9);
			}
		});
		playAgain.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				playAgain.setOpacity(1);
			}
		});
		playAgain.setBackground(new Background(new BackgroundFill(mainColor, null, null)));
		Text playAgainText = new Text("Click here to play again!");
		playAgainText.setFill(Color.BLACK);
		playAgainText.setStyle("-fx-font: " + sideLength + " " + font + ";");
		playAgainText.setTextAlignment(TextAlignment.CENTER);
		playAgainText.setWrappingWidth(playAgain.getPrefWidth()*.9);
		playAgainText.setX((playAgain.getPrefWidth()-playAgainText.getWrappingWidth())/2);
		playAgainText.setTextOrigin(VPos.CENTER);
		playAgainText.setY(playAgain.getPrefHeight()/2);
		playAgain.getChildren().add(playAgainText);
		panel.getChildren().add(playAgain);
		
		Line median = new Line();
		median.setStrokeWidth(2);
		median.setEndY(playAgain.getPrefHeight());
		median.setTranslateX(playAgain.getPrefWidth());
		median.setTranslateY(loss.getPrefHeight());
		panel.getChildren().add(median);
		
		Pane end = new Pane();
		end.setPrefSize(panel.getPrefWidth()-playAgain.getPrefWidth(), playAgain.getPrefHeight());
		end.setTranslateY(playAgain.getTranslateY());
		end.setTranslateX(playAgain.getPrefWidth());
		end.setBackground(playAgain.getBackground());
		end.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.exit(0);
			}
		});
		end.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				end.setOpacity(.9);
			}
		});
		end.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				end.setOpacity(1);
			}
		});
		Text endText = new Text("Click here to end");
		endText.setFill(Color.BLACK);
		endText.setStyle("-fx-font: " + sideLength + " " + font + ";");
		endText.setTextAlignment(TextAlignment.CENTER);
		endText.setWrappingWidth(end.getPrefWidth()*.9);
		endText.setX((end.getPrefWidth()-endText.getWrappingWidth())/2);
		endText.setTextOrigin(VPos.CENTER);
		endText.setY(end.getPrefHeight()/2);
		end.getChildren().add(endText);
		panel.getChildren().add(end);
		
		return new Scene(panel);
	}
}

package Tetris;

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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Tetris extends Application {
	private final Color background = Color.LAWNGREEN;
	private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
	private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
	
	private static Stage mainStage;
	private final String font = "Roboto";
	
	private final Color rectBg = Color.rgb(45,45,45);
	
	private final int rowLength = 10; //Number of blocks per row
	private final int numRows = 21;
	private final double sideLength = screenHeight/25.0;
	
	private int score = 0;
	private Text scoreText;
	private int lines = 0;
	private Text lineText;
	private Rectangle[] rect;
	private Integer[] rows;
	private Integer[] cols;
	private boolean[] placed;
	private boolean first;
	private enum Direction {LEFT, RIGHT, DOWN}
	private enum BlockType {STRAIGHT, SQUARE, T, J, L, S, Z}
	private BlockType blockType;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		rect = new Rectangle[rowLength*numRows];
		rows = new Integer[4];
		cols = new Integer[4];
		placed = new boolean[rect.length];
		first = true;
	}
	
	@Override
	public void start(Stage theStage) throws Exception {
		mainStage = theStage;
		mainStage.setScene(initScene());
		mainStage.setResizable(false);
		mainStage.setTitle("Tetris");
		mainStage.show();
	}
	
	private Scene initScene() {
		Pane init = new Pane();
		init.setBackground(new Background(new BackgroundFill(background, null, null)));
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
	
	private Scene mainScene() {
		lineText = new Text("Lines: " + lines);
		lineText.setFill(Color.WHITE);
		lineText.setTextOrigin(VPos.TOP);
		int fontSize = (int) (sideLength/1.1);
		lineText.setStyle("-fx-font: " + fontSize + " " + font + ";"); //Uses CSS
		lineText.setX(sideLength/5);
		lineText.setY(lineText.getX());
		
		scoreText = new Text("Score: " + score);
		scoreText.setFill(Color.WHITE);
		scoreText.setTextOrigin(VPos.TOP);
		scoreText.setStyle("-fx-font: " + fontSize + " " + font + ";"); //Uses CSS
		scoreText.setX(lineText.getX() + sideLength*4.5);
		scoreText.setY(lineText.getY());
		
		Text highScoreText = new Text();
		try {
			highScoreText = new Text("High Score: " + highScores(false).get(0));
		} catch (IOException e) {e.printStackTrace();}
		highScoreText.setFill(Color.WHITE);
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
			rect[i].setStroke((i<rowLength) ? Color.rgb(250, 250, 250) : Color.GREY);
			
			if(colNum==rowLength-1) //If the row has been filled	Uses rowLength-1 because colNum is 0 indexed
				colNum=0; //This is because it will get incremented right after
			else
				colNum++;
			if((i+1)%rowLength==0) //If the row has been filled		Uses i+1 because i is 0 indexed
				rowNum++;
		}
		
		
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(first) {
					newBlock();
					first=false;
				}
				else {
					if(canMoveBlock(Direction.DOWN))
						moveBlock(Direction.DOWN, 0); //Multiplier of 0 because it's not worth any points
					else {
						newBlock();
					}
				}
			}
		}, 3000, 1000); //Starts after 3 seconds, moves every 1
		
		Pane pane = new Pane(rect);
		
		pane.setPrefSize(sideLength*rowLength, sideLength*numRows); //Width, Height
		
		VBox box = new VBox(top, pane);
		Scene scene = new Scene(box);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				//System.out.println(event.getCode());
				if(!first) {
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
		return scene;
	}
	
	/**
	 * Generates a new block by changing the color of one of the top blocks
	 * @see <a href="https://en.wikipedia.org/wiki/Tetromino">Tetromino Wikipedia Page</a>
	 * @return The column of the new block
	 */
	private void newBlock() { //Tetriminos always spawn with 3 empty blocks to the left
		Color color;
		switch((int)(Math.random()*7)) { //*7 Because there are 7 different tetriminos and it is 0 referenced
		case 0: //Straight
			blockType = BlockType.STRAIGHT;
			color = Color.CYAN;
			for(int i=0; i<rows.length; i++)
				rows[i] = 0;
			for(int i=0; i<rows.length; i++)
				cols[i] = i+3;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[6].setFill(color);
			break;
		case 1: //Square
			blockType = BlockType.SQUARE;
			color = Color.YELLOW;
			rows[0] = 0; rows[1] = 0; rows[2] = 1; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 3; cols[3] = 4;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[3+rowLength].setFill(color);
			rect[4+rowLength].setFill(color);
			break;
		case 2: //T
			blockType = BlockType.T;
			color = Color.MAGENTA;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 4;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[4+rowLength].setFill(color);
			break;
		case 3: //J
			blockType = BlockType.J;
			color = Color.DODGERBLUE;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 5;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[5+rowLength].setFill(color);
			break;
		case 4: //L
			blockType = BlockType.L;
			color = Color.ORANGE;
			rows[0] = 0; rows[1] = 0; rows[2] = 0; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 5; cols[3] = 3;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			rect[3+rowLength].setFill(color);
			break;
		case 5: //S
			blockType = BlockType.S;
			color = Color.LIME;
			rows[0] = 1; rows[1] = 1; rows[2] = 0; rows[3] = 0;
			cols[0] = 3; cols[1] = 4; cols[2] = 4; cols[3] = 5;
			rect[3+rowLength].setFill(color);
			rect[4+rowLength].setFill(color);
			rect[4].setFill(color);
			rect[5].setFill(color);
			break;
		case 6: //Z
			blockType = BlockType.Z;
			color = Color.RED;
			rows[0] = 0; rows[1] = 0; rows[2] = 1; rows[3] = 1;
			cols[0] = 3; cols[1] = 4; cols[2] = 4; cols[3] = 5;
			rect[3].setFill(color);
			rect[4].setFill(color);
			rect[4+rowLength].setFill(color);
			rect[5+rowLength].setFill(color);
			break;
		}
		fixPlaced();
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
						System.out.println("You lost with a score of " + score + "!");
						try {
							highScores(true);
						} catch (IOException e) {e.printStackTrace();}
						System.exit(0);
						//Not working -> mainStage.setScene(loseScene());
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
	 * @see <a href="https://www.youtube.com/watch?v=Atlr5vvdchY">Source</a>
	 */
	private void rotateBlock() {///<3
		int[] pivotPoint = new int[2];
		int spot = 0;
		switch(blockType) {
		case STRAIGHT:
			spot = 0;
			break;
		case SQUARE:
			spot = 1;
			break;
		case T:
			spot = 3;
			break;
		case J:
			spot = 1;
			break;
		case L:
			spot = 0;
			break;
		case S:
			spot = 1;
			break;
		case Z:
			spot = 1;
			break;
		}
		pivotPoint[0] = rows[spot]; pivotPoint[1] = cols[spot];
		if(!canRotate(spot, pivotPoint))
			return;
		ArrayList<Integer> moveList = new ArrayList<>();
		for(int i = 0; i<4; i++) {
			if(i!=spot) {
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
	
	private boolean canRotate(int spot, int[] pivotPoint) {
		for(int i = 0; i<4; i++) {
			if(i!=spot) {
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
		String scores = new String (Files.readAllBytes(Paths.get("src/Tetris/HighScores.txt")));
		
		ArrayList<String> scoresList = new ArrayList<>(Arrays.asList(scores.split("\n")));
		if(!update)
			return scoresList;
		int high = -1;
		
		for(int i = 0; i<scoresList.size(); i++) {
			if(score>Integer.parseInt(scoresList.get(i))) {
				switch(i) {
				case 0:
					System.out.println("You got a new high score!");
					break;
				case 1:
					System.out.println("That's your second highest score!");
					break;
				case 2:
					System.out.println("That's your third highest score!");
					break;
				case 3:
					System.out.println("That's your fourth highest score!");
					break;
				case 4:
					System.out.println("That's your fifth highest score!");
					break;
				default:
					System.out.println("No new high score :(");
				}
				
				high = i;
				i = scoresList.size()-1; //This ends the for loop
			}
		}
		
		if(high!=-1) {
			scoresList.add(high, "" + score);
			scoresList.remove(5);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/Tetris/HighScores.txt"));
			for(String str : scoresList)
				writer.write(str + "\n");
			writer.close();
		}
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

	@SuppressWarnings("unused")
	private Scene loseScene() {
		Text loss = new Text("You lost");
		loss.setStyle("-fx-font: " + 30 + " " + font + ";");
		Pane pane = new Pane(loss);
		return new Scene(pane);
	}
}

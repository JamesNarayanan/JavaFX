
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * The class that creates and runs a TicTacToe game
 * @author James Narayanan
 * @since June 2018
 */
public class TicTacToe extends Application {
	/**
	 * The side length in boxes of the playing board of {@link #spots}
	 */
	private final int size = 3;
	
	/**
	 * The side length of each box in the playing board
	 */
	private final int boxSize = 180;
	
	/**
	 * The character (X or O) of the current player's turn
	 */
	private char turn;
	
	/**
	 * Used to display the current {@link #turn} in two player mode
	 */
	private Text turnText;
	
	/**
	 * The potential color schemes for the game
	 * <p>One of the potential choices is picked by {@link #colors}
	 */
	private final Color[][] colorChoices = {
			{Color.CADETBLUE, Color.DARKCYAN, Color.LIGHTSEAGREEN},
			{Color.CRIMSON, Color.INDIANRED, Color.FIREBRICK},
			{Color.FORESTGREEN, Color.LIGHTGREEN, Color.DARKSEAGREEN},
			{Color.VIOLET, Color.BLUEVIOLET, Color.DARKVIOLET}
	};
	
	/**
	 * The color scheme of the game, used if {@link #colorful} is true
	 * <p>One of the potential color schemes from {@link #colorChoices}
	 */
	private final Color[] colors = colorChoices[2];

	/**
	 * Whether or not the board will be colorful and use the game {@link #colors}
	 * <p> If {@code false}, the game will be black and white
	 */
	private final boolean colorful = true;
	
	/**
	 * All of the panes to go on the board
	 */
	private Pane[][] spots;
	
	/**
	 * The contents of all the {@link #spots} on the board
	 * <p>Contains a space if empty
	 */
	private char[][] occupied;
	
	/**
	 * The Stage used to display graphics throughout the game
	 */
	private Stage mainStage;
	
	/**
	 * The X player's score
	 * <p> Also the human player's score in single player
	 */
	private int xScore;
	
	/**
	 * The O player's score
	 * <p> Also the bot's score in single player
	 */
	private int oScore;
	
	/**
	 * True if the user wants to play by him or herself against a bot
	 */
	private boolean isSinglePlayer;
	
	/**
	 * Stops the player from making a move while waiting for the bot to make its move
	 */
	private boolean waitForBot;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		turn = 'X'; //Will start with X every time like this, could easily make it random with: (Math.random()>.5) ? 'X' : 'O';
		
		turnText = new Text(20, boxSize/4, "Turn: " + turn);
		turnText.setFont(Font.font("helvetica", FontWeight.BOLD, (double) boxSize/5));
		turnText.setWrappingWidth(size*boxSize);
		
		spots = new Pane[size][size];
		occupied = new char[size][size];
		
		xScore = 0;
		oScore = 0;
		
		waitForBot = false;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		mainStage.setScene(generateWelcomeScene());
		mainStage.setResizable(false);
		mainStage.setTitle("TicTacToe");
		mainStage.show();
	}
	
	/**
	 * Creates a welcome screen for the opening of the game
	 * @return The opening scene of the game
	 */
	private Scene generateWelcomeScene() {
		Pane top = new Pane();
		top.setPrefSize(size*boxSize, boxSize); //Width, Height
		top.setBackground(new Background(new BackgroundFill( (colorful) ? colors[2] : Color.WHITE, null, null)));
		
		//Text should be centered in the pane
		Text welcomeMessage = new Text(0, (double) boxSize/2, "Welome to TicTacToe!"); //the horizontal position of the text, the vertical position of the text, the text
		welcomeMessage.setFont(Font.font("helvetica", FontWeight.BOLD, (double) boxSize/3));
		welcomeMessage.setWrappingWidth((double) size*boxSize);
		welcomeMessage.setTextAlignment(TextAlignment.CENTER);
		welcomeMessage.setTextOrigin(VPos.CENTER);
		top.getChildren().add(welcomeMessage);
		
		
		Pane singlePlayer = new Pane();
		singlePlayer.setPrefSize(size*boxSize/2, boxSize); //Width, Height
		singlePlayer.setBackground(new Background(new BackgroundFill( (colorful) ? colors[0] : Color.WHITE, null, null)));
		singlePlayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		isSinglePlayer = true;
	        		mainStage.setScene(generateMainScene());
	        }
	    });
		Text singles = new Text(0, (double) boxSize/2, "Click here to play Single Player!"); //the horizontal position of the text, the vertical position of the text, the text
		singles.setFont(Font.font("helvetica", FontWeight.BOLD, (double) boxSize/6));
		singles.setWrappingWidth((double) size*boxSize/2);
		singles.setTextAlignment(TextAlignment.CENTER);
		singles.setTextOrigin(VPos.CENTER);
		singlePlayer.getChildren().add(singles);
		
		Pane twoPlayer = new Pane();
		twoPlayer.setPrefSize((double) size*boxSize/2, boxSize); //Width, Height
		twoPlayer.setBackground(new Background(new BackgroundFill( (colorful) ? colors[0] : Color.WHITE, null, null)));
		twoPlayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		isSinglePlayer = false;
	        		mainStage.setScene(generateMainScene());
	        }
	    });
		Text duos = new Text(0, (double) boxSize/2, "Click here to play Two Player!"); //the horizontal position of the text, the vertical position of the text, the text
		duos.setFont(Font.font("helvetica", FontWeight.BOLD, (double) boxSize/6));
		duos.setWrappingWidth((double) size*boxSize/2);
		duos.setTextAlignment(TextAlignment.CENTER);
		duos.setTextOrigin(VPos.CENTER);
		twoPlayer.getChildren().add(duos);
		
		Line median = new Line();
		median.setEndY(boxSize);
		
		HBox choices = new HBox();
		choices.getChildren().addAll(singlePlayer, median, twoPlayer);
		
		VBox view = new VBox();
		view.getChildren().addAll(top, choices);
		
		return new Scene(view);
	}
	
	/**
	 * Generates the main game scene
	 * @return The main scene where the game is played
	 */
	private Scene generateMainScene() {
		Pane top = new Pane();
		top.setPrefSize(size*boxSize, boxSize); //Width, Height
		top.setBackground(new Background(new BackgroundFill( (colorful) ? colors[2] : Color.WHITE, null, null)));
		
		String scoreMessage = (isSinglePlayer) ? "Your Score: " + xScore + "\nBot Score: " + oScore : "X Score: " + xScore + "\tO Score: " + oScore;
		Text score = new Text(20, (double) ((isSinglePlayer) ? boxSize/4 : boxSize/2), scoreMessage); //the horizontal position of the text, the vertical position of the text, the text
		score.setFont(Font.font("helvetica", FontWeight.BOLD, (double) boxSize/5));
		score.setWrappingWidth(size*boxSize);
		
		if(!isSinglePlayer)
			top.getChildren().addAll(turnText, score); //Should only add turnText in two player
		else
			top.getChildren().add(score);
		
		GridPane board = new GridPane();
		board.setPrefSize(size*boxSize, size*boxSize);
		
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				spots[i][j] = new Pane(); //Needed to avoid null pointers for the first time and to reset after that
				occupied[i][j] = ' ';
			}
		}
		
		for(int i = 0; i<size; i++) {
			for(int j = 0; j<size; j++) {
				Pane p = spots[i][j]; //Necessary to pass spot into placeMove(Pane)
				int row = j; int col = i;
				p.setPrefSize(boxSize, boxSize);
				
				p.setOnMouseClicked(new EventHandler<MouseEvent>() {
			        @Override
			        public void handle(MouseEvent event) {
			        		if(occupied[row][col]==' ') {
			        			//occupied[row][col] = turn;
			        			placeMove(row, col, false);
			        		}
			        }
			    });
				
				Color c;
				if(colorful)
					c = (((i+1)-(j+1))%2==0) ? colors[0] : colors[1];
				else
					c = Color.WHITE; //For an empty grid
				
				Background b = new Background(new BackgroundFill(c, null, null));
				p.setBackground(b);
				
				board.add(p, i, j);
			}
		}
		if(!colorful) board.setGridLinesVisible(true); //If grid is empty (white)
		
		VBox view = new VBox();
		view.getChildren().addAll(top, board);
		
		return new Scene(view);
	}
	
	/**
	 * Prints an X or O in the clicked Pane
	 * @param row Row number of current pane in {@linkplain #spots} starting at zero and going upto size-1
	 * @param col Column number of current pane in {@linkplain #spots} starting at zero and going upto size-1
	 * @param isBot True if the method is being called by the bot
	 */
	private void placeMove(int row, int col, boolean isBot) {
		if(!isBot) {
			if(waitForBot) {
				return;
			}
		}
		else {
			waitForBot = false;
		}
		
		occupied[row][col] = turn;
		
		//System.out.println(turn);
		//System.out.println("Row: " + row + " | Col: " + col);
		
		if(turn=='O') {
			double buffer = (double) boxSize/10;
			double center = (double) boxSize/2;
			
			Circle c = new Circle(center - buffer); //@param is radius, gives buffer room around the circle
			c.setCenterX(center);
			c.setCenterY(center);
			
			if(!colorful) {
				c.setFill(Color.WHITE);
				c.setStroke(Color.BLACK);
			}
			else {
				c.setFill((((row+1)-(col+1))%2==0) ? colors[0] : colors[1]);
				c.setStroke((((row+1)-(col+1))%2==0) ? colors[1] : colors[0]);
			}
			c.setStrokeWidth(buffer);
			//p.getChildren().add(c);
			spots[col][row].getChildren().add(c);
		}
		else {
			double length = (double) boxSize/8;
			double shift = (double) 55*boxSize/126; //This fits it in the middle at a 45° angle, got it by doing (3/7 + 4/9)/2
			
			Rectangle r1 = new Rectangle(length, boxSize);
			r1.setRotate(-45);
			r1.relocate(shift, 0);
			
			Rectangle r2 = new Rectangle(length, boxSize);
			r2.setRotate(45);
			//r2.relocate(boxSize-(shift*1.28), 0);
			r2.relocate(shift, 0);
			
			if(colorful) {
				r1.setFill((((row+1)-(col+1))%2==0) ? colors[1] : colors[0]);
				r2.setFill((((row+1)-(col+1))%2==0) ? colors[1] : colors[0]);
			}
			else {
				r1.setFill(Color.BLACK);
				r2.setFill(Color.BLACK);
			}
			
			//p.getChildren().addAll(r1, r2);
			spots[col][row].getChildren().addAll(r1, r2);
		}
		
		boolean switchedScreen = false;
		
		if(checkWin(row, col)) {
			waitForBot = true;
			if(turn=='X')
				xScore++;
			else
				oScore++;
			showWinScreen();
			switchedScreen = true;
		}
		else if(isFull()) {
			waitForBot = true;
			showFullScreen();
			switchedScreen = true;
		}
		
		else if (isSinglePlayer) {
			turn = (turn=='X') ? 'O' : 'X';
			if(!isBot) { //Will only make a bot move if this move was made by a human
				placeBotMove(true);
			}
		}
		
		if(!isSinglePlayer) {
			if(!switchedScreen) turn = (turn=='X') ? 'O' : 'X';
			//Prevents a double switch from happening because the show__Screen() methods
			//call reset which switches the screen
			turnText.setText("Turn: " + turn);
		}
	}
	
	/**
	 * Checks if the player who's turn it currently is wins
	 * @param row The row of the move in {@linkplain #spots} starting at zero and going upto size - 1
	 * @param col The column of the move in {@linkplain #spots} starting at zero and going upto size - 1
	 * @return true if the player who's {@linkplain #turn} it currently is wins
	 */
	private boolean checkWin(int row, int col) {
		//Checking for row wins
		for(int c = 0; c<size; c++) {
			if(occupied[row][c]!=turn) {
				break;
			}
			if(c==size-1)
				return true;
		}
		
		//Column wins
		for(int r = 0; r<size; r++) {
			if(occupied[r][col]!=turn) {
				break;
			}
			if(r==size-1)
				return true;
		}
		
		//Top left to bottom right diagonal
		for(int r=0, c=0; r<size; r++, c++) { //Checking for r and c is redundant
			if(occupied[r][c]!=turn) {
				break;
			}
			if(r==size-1)
				return true;
		}
		//Bottom left to top right diagonal
		for(int r=size-1, c=0; r>=0; r--, c++) {
			if(occupied[r][c]!=turn) {
				break;
			}
			if(r==0)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the board is full of X's or O's
	 * @return True if the board is full
	 */
	private boolean isFull() {
		for(char[] row : occupied) {
			for(char c : row) {
				if(c==' ')
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Displays a scene showing that a player has won the round
	 * <p> Gives the Player options to play again or end the game
	 */
	private void showWinScreen() {
		int textSize = 64;
		int width = 400;
		
		Pane win = new Pane();
		win.setPrefSize(width, textSize+20);
		String winMessage = (isSinglePlayer) ? ((turn=='X') ? "YOU WIN!" : "BOT WINS!") : turn + " WINS!";
		Text winText = new Text(20, 60, winMessage); //the horizontal position of the text, the vertical position of the text, the text
		winText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		win.getChildren().add(winText);
		win.setBackground(new Background(new BackgroundFill((colorful) ? colors[0] : Color.WHITE, null, null)));
		
		Pane playAgain = new Pane();
		playAgain.setPrefSize(width, textSize+20);
		Text pAText = new Text(20, 60, "Play Again!"); //the horizontal position of the text, the vertical position of the text, the text
		pAText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		playAgain.getChildren().add(pAText);
		playAgain.setBackground(new Background(new BackgroundFill((colorful) ? colors[1] : Color.WHITE, null, null)));
		playAgain.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		reset();
	        }
	    });
		
		Pane end = new Pane();
		end.setPrefSize(width, textSize+20);
		Text endText = new Text(20, 60, "End"); //the horizontal position of the text, the vertical position of the text, the text
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		end.getChildren().add(endText);
		end.setBackground(new Background(new BackgroundFill((colorful) ? colors[2] : Color.WHITE, null, null)));
		end.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		showEndScreen();
	        }
	    });
		
		VBox view = new VBox();
		view.getChildren().addAll(win, playAgain, end);
		
		Scene scene = new Scene(view);
		
		//This is so the player has a chance to see how they won or lost
		//Link to source in placeBotMove()
		Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {Thread.sleep(300);} catch (InterruptedException e) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
	            mainStage.setScene(scene);
            }
        });
        new Thread(sleeper).start();
        
		//mainStage.setScene(scene); //Used to just be this
	}
	
	/**
	 * Displays a scene showing that the game is a tie because the board is full without a winner
	 * <br> Gives the player the option to play again or end the game
	 */
	private void showFullScreen() {
		int textSize = 64;
		int width = 400;
		
		Pane tie = new Pane();
		tie.setPrefSize(width, textSize+20);
		Text tieText = new Text(20, 60, "It's a tie :("); //the horizontal position of the text, the vertical position of the text, the text
		tieText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		tie.getChildren().add(tieText);
		tie.setBackground(new Background(new BackgroundFill((colorful) ? colors[0] : Color.WHITE, null, null)));
		
		Pane playAgain = new Pane();
		playAgain.setPrefSize(width, textSize+20);
		Text pAText = new Text(20, 60, "Play Again!"); //the horizontal position of the text, the vertical position of the text, the text
		pAText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		playAgain.getChildren().add(pAText);
		playAgain.setBackground(new Background(new BackgroundFill((colorful) ? colors[1] : Color.WHITE, null, null)));
		playAgain.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		reset();
	        }
	    });
		
		Pane end = new Pane();
		end.setPrefSize(width, textSize+20);
		Text endText = new Text(20, 60, "End"); //the horizontal position of the text, the vertical position of the text, the text
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, textSize));
		end.getChildren().add(endText);
		end.setBackground(new Background(new BackgroundFill((colorful) ? colors[2] : Color.WHITE, null, null)));
		end.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		showEndScreen();
	        }
	    });
		VBox view = new VBox();
		view.getChildren().addAll(tie, playAgain, end);
		
		Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {Thread.sleep(300);} catch (InterruptedException e) {}
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
            	mainStage.setScene(new Scene(view));
            }
        });
        new Thread(sleeper).start();
	}
	
	/**
	 * Displays a final scene showing the final score
	 */
	private void showEndScreen() {
		Pane end = new Pane();
		end.setBackground(new Background(new BackgroundFill((colorful) ? colors[0] : Color.WHITE, null, null)));
		String message = "";
		if(xScore==oScore) {
			message = "It's a tie :(";
		}
		if(isSinglePlayer) {
			if(xScore>oScore)
				message = "You Win!";
			else if(xScore<oScore)
				message = "The Bot Wins!";
			message += "\nScore: You–" + xScore + "\tBot–" + oScore;
		}
		else {
			if(xScore>oScore)
				message = "X Wins!";
			else if(xScore<oScore)
				message = "O Wins!";
			message += "\nScore: X–" + xScore + "\tO–" + oScore;
		}
		Text endText = new Text(20, 50,  message); //the horizontal position of the text, the vertical position of the text, the text
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, 48));
		end.setPrefSize(message.length()*22, 120); //Width, Height
		end.getChildren().add(endText);
		
		mainStage.setScene(new Scene(end));
	}
	
	/**
	 * In single player mode, places the bot's move
	 * @param wait True if the method should have a delay before placing a move (such as after a player move)
	 * <br>False if the method should place a move immediately (such as after a {@linkplain #reset()})
	 * @see <a href="https://stackoverflow.com/questions/26454149/make-javafx-wait-and-continue-with-code">How to wait in JavaFX</a>
	 */
	private void placeBotMove(boolean wait) {
		waitForBot = true;
		
		//Delays the bot move by half a second to make it seem more natural
		if(wait) {
			Task<Void> sleeper = new Task<Void>() {
	            @Override
	            protected Void call() throws Exception {
	                try {Thread.sleep(500);} catch (InterruptedException e) {}
	                return null;
	            }
	        };
	        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	            @Override
	            public void handle(WorkerStateEvent event) {
		            	botMoveCode();
	            }
	        });
	        new Thread(sleeper).start();
		}
		else {
			botMoveCode();
		}
	}
		
	/**
	 * Contains the code that placces the bot's move
	 * <p> Exists so that the code does not have to be written twice in {@link #placeBotMove(boolean)}
	 */
	private void botMoveCode() {
		int row = -1;
		int col = -1;
		
		int[] winSpot = botWinSpot();
		int[] blockSpot = botBlockSpot();

		if(winSpot[0]!=-1) {
			placeMove(winSpot[0], winSpot[1], true);
		}
		else if(blockSpot[0]!=-1) {
			placeMove(blockSpot[0], blockSpot[1], true);
		}
		else if(placeMiddle()) {
			placeMove((size-1)/2, (size-1)/2, true);
		}
		else {
			while(row==-1 || col==-1) { //Ineffective
				int r = (int) (Math.random()*size);
				int c = (int) (Math.random()*size);
				if(occupied[r][c]==' ') {
					row = r;
					col = c;
				}
			}
			placeMove(row, col, true);
		}
	}
	
	/**
	 * If a player's first move is on a corner, the best spot to play is in the middle
	 * <br> I always think the best place for a first move is the middle so now it works like that
	 * @return True if the bot should place it's first move in the middle
	 */
	private boolean placeMiddle() {
		if(size%2!=0) //Won't pick the middle on an even sized board
			if(occupied[(size-1)/2][(size-1)/2]==' ')
				//if(occupied[0][0]=='X' || occupied[size-1][0]=='X' || occupied[0][size-1]=='X' || occupied[size-1][size-1]=='X')
					return true;
		return false;
	}
	
	/**
	 * Checks if the bot can block a human player win
	 * @return An int array with two spots, the first being the row of the spot to block,
	 * the second the column of the spot to block
	 * <br>-1 if no blocking spot is found
	 */
	private int[] botBlockSpot() {
		turn = 'X'; //Necessary because of how checkWin() works
		
		boolean openSpots[][] = getOpenSpots();
		int[] output = new int[2];
		
		output[0] = -1;
		output[1] = -1;
		
		for(int r = 0; r<openSpots.length; r++) {
			for(int c = 0; c<openSpots[r].length; c++) {
				if(openSpots[r][c]) {
					occupied[r][c] = 'X'; //For checking if human would win
					if(checkWin(r, c)) {
						occupied[r][c] = 'O'; //Because there will now be an O there
						turn = 'O'; //Reverts it to the Bot's turn
						output[0] = r;
						output[1] = c;
						return output;
					}
					else {
						occupied[r][c] = ' ';
					}
				}
			}
		}
		
		turn = 'O'; //Reverts it to the Bot's turn
		
		return output;
	}
	
	/**
	 * Checks if the bot can play a winning move
	 * @return An int array with two spots, the first being the row of the winning spot,
	 * the second the column of the winning spot
	 * <br>-1 if no winning spot is found
	 */
	private int[] botWinSpot() {
		boolean openSpots[][] = getOpenSpots();
		int[] output = new int[2];
		
		output[0] = -1;
		output[1] = -1;
		
		for(int r = 0; r<openSpots.length; r++) {
			for(int c = 0; c<openSpots[r].length; c++) {
				if(openSpots[r][c]) {
					occupied[r][c] = 'O';
					if(checkWin(r, c)) {
						output[0] = r;
						output[1] = c;
						return output;
					}
					else {
						occupied[r][c] = ' ';
					}
				}
			}
		}
		return output;
	}
	
	/**
	 * Finds open spots on the board
	 * @return An array containing boolean values true if
	 * the coresponding spot on the board of {@link #getOpenSpots()} is empty
	 */
	private boolean[][] getOpenSpots() {
		boolean[][] openSpots = new boolean[size][size];
		
		for(int r = 0; r<size; r++) {
			for(int c = 0; c<size; c++) {
				if(occupied[r][c]==' ')
					openSpots[r][c] = true;
			}
		}
		
		return openSpots;
	}
	
	/**
	 * Resets the board to play again by using {@link #generateMainScene()}
	 * <p>If {@linkplain #isSinglePlayer} is true and {@linkplain #turn} is O, generates a bot move to begin.
	 * <p> Changes the {@linkplain #turn} now in case the bot moves first in the new game
	 */
	private void reset() {
		turn = (turn=='X') ? 'O' : 'X';
		mainStage.setScene(generateMainScene());
		if(turn=='O' && isSinglePlayer)
			placeBotMove(false);
		waitForBot = false;
	}
}
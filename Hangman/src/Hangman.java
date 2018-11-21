
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Hangman JavaFX game
 * @author James Narayanan
 * @since June 2018
 */
public class Hangman extends Application {
	/**
	 * The Stage used to display graphics throughout the game
	 */
	private Stage mainStage;
	
	/**
	 * The word that the player is guessing
	 */
	private String theWord;
	
	/**
	 * The progress that the player has made towards guessing {@linkplain #theWord}
	 */
	private String progress;
	
	/**
	 * All of the letters that the player has guessed so far
	 */
	private String enteredLetters;
	
	/**
	 * The amount of lives the player has remaining
	 */
	private int lives;
	
	/**
	 * The {@link Text} object used to display the {@linkplain #progress} towards guessing the word
	 */
	private Text progressText;
	
	/**
	 * The {@link Text} object used to display the {@linkplain #enteredLetters} of the user
	 */
	private Text enteredLettersText;
	
	/**
	 * The {@link Text} object used to display the number of {@linkplain #lives} the user has remaining
	 */
	private Text livesText;
	
	/**
	 * The {@link Text} object used to tell the player that they have already entered the letter they just typed
	 */
	private Text alreadyEntered;
	
	/**
	 * The {@link Text} object used to tell the player the basic directions when they first play
	 */
	private Text directions;
	
	/**
	 * Only true if the player has just opened the app and chosen a mode.
	 * If true, the {@link #directions} are displayed
	 */
	private boolean first;
	
	/**
	 * The number of wins the player has.
	 * <p>Only displayed in the {@link #endScene()}
	 */
	private int numWins;
	
	/**
	 * The difficulty level the user chose
	 * <br>Either 1 or 2
	 */
	private int difficultyLevel;
	
	/**
	 * The {@linkplain ArrayList} of {@linkplain Shape}s used to make the body of the man to be hanged
	 */
	private ArrayList<Shape> body;
	
	/**
	 * Prevents a user from losing then quickly pressing the winning letter to win.
	 * Also prevents any changes from being made inbetween losing and the {@link #gameEndScene(boolean)} appearing
	 */
	private boolean justLost;
	
	/**
	 * The background {@linkplain Color} of the game
	 */
	private final Color background = Color.CRIMSON;
	
	/**
	 * The {@linkplain Color} used when a button is not being hovered over
	 */
	private final Color hoverColor = Color.gray(.875);
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		progress = "";
		enteredLetters = "";
		numWins = 0;
		first = true;
		justLost = false;
	}
	
	@Override
	public void start(Stage theStage) throws Exception {
		mainStage = theStage;
		mainStage.setScene(initScene());
		mainStage.setResizable(false);
		mainStage.setTitle("Hangman");
		mainStage.show();
	}
	
	/**
	 * Makes the initial welcome screen where the player can select the difficulty level
	 * @return The initial game scene where a difficulty can be chosen
	 */
	private Scene initScene() {
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		
		Pane top = new Pane();
		top.setPrefSize(screenWidth, screenHeight/2.0);
		top.setBackground(new Background(new BackgroundFill(background, null, null)));
		Text welcomeMessage = new Text(0, top.getPrefHeight()/2.0, "Welome to Hangman!"); //the horizontal position of the text, the vertical position of the text, the text
		welcomeMessage.setWrappingWidth(screenWidth);
		welcomeMessage.setTextOrigin(VPos.CENTER);
		welcomeMessage.setTextAlignment(TextAlignment.CENTER);
		welcomeMessage.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/11.0));
		top.getChildren().add(welcomeMessage);
		
		
		Pane regular = new Pane();
		regular.setPrefSize(screenWidth/3.0, screenHeight/2.0);
		regular.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		try {
	        			difficultyLevel = 1;
	        			theWord = getWord();
	        			lives = 6;
	        			mainStage.setScene(mainScene());	
				} catch (IOException e) {System.out.println("Common Words file not found");}
	        }
	    });
		addHoverAnimation(regular);
		Text regularText = new Text(0, screenHeight/4.0, "Click here to play regular mode!");
		regularText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/22.0));
		regularText.setWrappingWidth(screenWidth/3.0);
		regularText.setTextAlignment(TextAlignment.CENTER);
		regularText.setTextOrigin(VPos.CENTER);
		regular.getChildren().add(regularText);
		
		Pane scrabble = new Pane();
		scrabble.setPrefSize(screenWidth/3.0, screenHeight/2.0);
		scrabble.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		try {
	        			difficultyLevel = 2;
	        			theWord = getWord();
	        			lives = 8;
	        			mainStage.setScene(mainScene());
				} catch (IOException e) {System.out.println("Scrabble file not found");}
	        }
	    });
		addHoverAnimation(scrabble);
		Text scrabbleText = new Text(0, screenHeight/4.0, "Click here to play scrabble words mode!");
		scrabbleText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/22.0));
		scrabbleText.setWrappingWidth(screenWidth/3.0);
		scrabbleText.setTextAlignment(TextAlignment.CENTER);
		scrabbleText.setTextOrigin(VPos.CENTER);
		scrabble.getChildren().add(scrabbleText);
		
		Pane insane = new Pane();
		insane.setPrefSize(screenWidth/3.0, screenHeight/2.0);
		insane.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		try {
	        			difficultyLevel = 3;
	        			theWord = getWord();
	        			lives = 8;
	        			mainStage.setScene(mainScene());
				} catch (IOException e) {System.out.println("Words file not found");}
	        }
	    });
		addHoverAnimation(insane);
		Text insaneText = new Text(0, screenHeight/4.0, "Click here to play insane mode!");
		insaneText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/22.0));
		insaneText.setWrappingWidth(screenWidth/3.0);
		insaneText.setTextAlignment(TextAlignment.CENTER);
		insaneText.setTextOrigin(VPos.CENTER);
		insane.getChildren().add(insaneText);
		
		Line medianOne = new Line();
		medianOne.setEndY(screenHeight/2.0);
		Line medianTwo = new Line();
		medianTwo.setEndY(screenHeight/2.0);
		
		HBox bottom = new HBox(regular, medianOne, scrabble, medianTwo, insane);
		bottom.setPrefSize(screenWidth, screenHeight/2.0);
		
		VBox view = new VBox(top, bottom);
		view.setPrefSize(screenWidth, screenHeight);
		
		return new Scene(view);
	}
	
	/**
	 * Generates the main game scene by making the noose, finding a word, and making spots for each character of that word
	 * @return The main game scene
	 * @throws FileNotFoundException 
	 */
	private Scene mainScene() throws FileNotFoundException {
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		
		//Noose display
		Rectangle bottom = new Rectangle(screenWidth/4.0, screenWidth/60.0); //Using screenWidth for height to standardize beam width
		
		double poleShift = bottom.getWidth()/8.0; //From the left edge
		Rectangle pole = new Rectangle(screenWidth/60.0, screenHeight/2.0); //Width, Height
		
		double topShift = poleShift/4.0; //From the left edge
		Rectangle top = new Rectangle(bottom.getWidth() - topShift, pole.getWidth()); //Width, Height
		
		double hookShift = (bottom.getWidth()* (2.0/3.0)*((9.0/16.0)+(1.0/2.0)));
		//Halfway from the pole to the right edge + 1/3 of that
		Rectangle hook = new Rectangle(hookShift/16, pole.getHeight()/8); //Width, Height
		
		Rectangle topSupport = new Rectangle(pole.getWidth()*.66666666, pole.getHeight()/2.5); //Width, Height
		topSupport.setRotate(45.0);
		//topSupport.setLayoutX(poleShift*2.0);
		Rectangle topSupportBlock = new Rectangle(poleShift, pole.getHeight()); //Is useful for if the topSupport juts out past the pole
		topSupportBlock.setFill(background);
		
		//The body of the hanged man
		body = new ArrayList<>();
		//Head
		body.add(new Circle(screenWidth/50.0, background));  //Radius, Fill
		body.get(0).setStroke(Color.BLACK);
		body.get(0).setStrokeWidth(((Circle) body.get(0)).getRadius()/12.0);
		body.get(0).setLayoutX(hookShift + hook.getWidth()/2.0);
		body.get(0).setLayoutY(hook.getHeight()/1.5 + ((Circle) body.get(0)).getRadius()); //Dividing by 1.5 looks better covers some of the hook
		
		//Body
		body.add(new Rectangle(hook.getWidth(), pole.getHeight()/2.5)); //Width, Height
		body.get(1).setLayoutX(hookShift);
		body.get(1).setLayoutY(body.get(0).getLayoutY() + ((Circle) body.get(0)).getRadius());
		
		//Left Arm
		double appendageShift = ((Rectangle) body.get(1)).getHeight()/8.0;
		//30-60-90 triangle, it is the 30° side length, hypotenuse is bodyHeight/4 because the triangle
		//is only made of the top half of the rectangle because the rectangle rotates from its center
		//Because of this, the hypotenuse is half of the length of the shape which is bodyHeight/2/2 = bodyHeight/4
		//Since the 30° angle will have half of the length of the hypotenuse, the length is bodyHeight/8
		body.add(new Rectangle(((Rectangle) body.get(1)).getWidth(), ((Rectangle) body.get(1)).getHeight()/2.0)); //Width, Height
		body.get(2).setLayoutX(body.get(1).getLayoutX() - appendageShift);
		body.get(2).setLayoutY(body.get(1).getLayoutY() + ((Rectangle) body.get(1)).getHeight()/5.0);
		body.get(2).setRotate(30.0);
		
		//Right Arm
		body.add(new Rectangle(((Rectangle) body.get(1)).getWidth(), ((Rectangle) body.get(1)).getHeight()/2.0)); //Width, Height
		body.get(3).setLayoutX(body.get(1).getLayoutX() + appendageShift);
		body.get(3).setLayoutY(body.get(2).getLayoutY());
		body.get(3).setRotate(-30.0);
		
		//Left Leg
		body.add(new Rectangle(((Rectangle) body.get(1)).getWidth(), ((Rectangle) body.get(1)).getHeight()/2.0)); //Width, Height
		body.get(4).setLayoutX(body.get(1).getLayoutX() - appendageShift);
		body.get(4).setLayoutY(body.get(1).getLayoutY() + ((Rectangle) body.get(1)).getHeight()/1.0575);
		body.get(4).setRotate(30.0);
		
		//Right Leg
		body.add(new Rectangle(((Rectangle) body.get(1)).getWidth(), ((Rectangle) body.get(1)).getHeight()/2.0)); //Width, Height
		body.get(5).setLayoutX(body.get(1).getLayoutX() + appendageShift);
		body.get(5).setLayoutY(body.get(4).getLayoutY());
		body.get(5).setRotate(-30.0);
		
		//Eyes only used for insane mode
		//Left eye
		Circle leftEye = new Circle(((Circle) body.get(0)).getRadius()/10.0);
		leftEye.setLayoutX(body.get(0).getLayoutX() - 3.0*leftEye.getRadius());
		leftEye.setLayoutY(body.get(0).getLayoutY() - 2.0*leftEye.getRadius());
		//Right eye
		Circle rightEye = new Circle(leftEye.getRadius());  //Radius
		rightEye.setLayoutX(body.get(0).getLayoutX() + 3.0*leftEye.getRadius());
		rightEye.setLayoutY(body.get(0).getLayoutY() - 2.0*leftEye.getRadius());
		
		body.add(Shape.union(leftEye, rightEye));
		
		//Mouth
		body.add(drawSemiRing(
			body.get(0).getLayoutX(),
			body.get(0).getLayoutY() + ((Circle) body.get(0)).getRadius()/1.75,
			((Circle) body.get(0)).getRadius()/2.5,
			((Circle) body.get(0)).getRadius()/2.5,
			background,
			Color.BLACK
		));
		
		for(int i = 0; i<body.size(); i++) {
			body.get(i).setOpacity(0.0);
		}
		
		AnchorPane noose = new AnchorPane(topSupport, topSupportBlock, bottom, pole, top, hook);
		noose.getChildren().addAll(body);
		//The order changes what is covered. The objects later in the order are on top
		//noose.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null))); //Temporary so it can be seen
		
		AnchorPane.setBottomAnchor(bottom, noose.getHeight());
		
		AnchorPane.setLeftAnchor(pole, poleShift);
		
		AnchorPane.setLeftAnchor(top, topShift);
		
		AnchorPane.setLeftAnchor(topSupport, poleShift*1.5);
		AnchorPane.setTopAnchor(topSupport, pole.getHeight()/-25.0);
		
		AnchorPane.setLeftAnchor(hook, hookShift);
		
		
		//Progress word display
		for(int i = 0; i<theWord.length(); i++) {
			progress += "_";
			if(i<theWord.length()-1)
				progress += " ";
		}
		System.out.println("Word: " + theWord);
		progressText = new Text(progress); //the horizontal position of the text, the vertical position of the text, the text
		progressText.setFont(Font.font("courier", FontWeight.BLACK, screenWidth/(theWord.length()*3.0)));
		progressText.setTextOrigin(VPos.CENTER);
		
		//Entered Letters display
		enteredLettersText = new Text("Entered Letters:\n");
		enteredLettersText.setFont(Font.font("courier", FontWeight.BOLD, screenWidth/45.0));
		enteredLettersText.setTextOrigin(VPos.CENTER);
		enteredLettersText.setWrappingWidth(screenWidth/2.0 - screenWidth/15.0);
		
		//Lives display
		livesText = new Text("Lives: " + lives);
		livesText.setFont(Font.font("courier", FontWeight.BOLD, screenWidth/60.0));
		livesText.setTextAlignment(TextAlignment.RIGHT);
		livesText.setTextOrigin(VPos.TOP);
		
		//Displayed to show the player they have already entered a character
		alreadyEntered = new Text("You have already entered this letter");
		alreadyEntered.setOpacity(0.0);
		alreadyEntered.setFont(Font.font("courier", FontWeight.BOLD, screenWidth/45.0));
		alreadyEntered.setWrappingWidth(screenWidth/2.0 - screenWidth/15.0);
		alreadyEntered.setTextAlignment(TextAlignment.CENTER);
		alreadyEntered.setTextOrigin(VPos.CENTER);
		
		//Displayed to display directions for when the player first begins
		directions = new Text("Just type a letter to guess it! You are not penalized for retyping a guessed letter");
		if(!first)
			directions.setVisible(false);
		directions.setFont(Font.font("courier", FontWeight.BOLD, screenWidth/55.0));
		directions.setWrappingWidth(screenWidth/2.0);
		directions.setTextAlignment(TextAlignment.CENTER);
		directions.setTextOrigin(VPos.CENTER);
		
		//Full main view
		AnchorPane view = new AnchorPane(noose, progressText, enteredLettersText, livesText, alreadyEntered, directions);
		view.setPrefSize(screenWidth, screenHeight); //Full screen window
		AnchorPane.setBottomAnchor(noose, screenHeight/4.0);
		AnchorPane.setLeftAnchor(noose, screenWidth/2.0);
		
		AnchorPane.setBottomAnchor(progressText, screenHeight/2.0);
		AnchorPane.setLeftAnchor(progressText, screenWidth/15.0);
		
		AnchorPane.setTopAnchor(enteredLettersText, screenHeight/8.0);
		AnchorPane.setLeftAnchor(enteredLettersText, screenWidth/15.0);
		
		AnchorPane.setRightAnchor(livesText, 0.0);
		
		AnchorPane.setLeftAnchor(alreadyEntered, screenWidth/15.0);
		AnchorPane.setTopAnchor(alreadyEntered, screenHeight/1.66666);
		
		AnchorPane.setLeftAnchor(directions, screenWidth/4.0);
		AnchorPane.setTopAnchor(directions, screenHeight/25.0);
		
		
		Scene mainScene = new Scene(view);
		mainScene.setFill(background);
		mainScene.setOnKeyTyped(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent event) {
	        		//System.out.println(event.getCharacter());
	        		if(Character.isLetter(event.getCharacter().charAt(0)))
	        			update(event.getCharacter().toLowerCase());
	        }
	    });
		return mainScene;
	}
	
	/**
	 * Generates the scene to be used after a win or loss
	 * @param win If the the player just won or lost the game
	 * @return The scene to be used after a win or loss
	 */
	private Scene gameEndScene(boolean win) {
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		
		justLost = false;
		
		if(win) {
			numWins++;
		}
		
		Text winLose = new Text("YOU " + ((win) ? "WIN!" : "LOSE"));
		winLose.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/12.0));
		winLose.setTextOrigin(VPos.CENTER);
		winLose.setTextAlignment(TextAlignment.CENTER);
		winLose.setWrappingWidth(screenWidth);
		
		Text wordDisp = new Text("The word was: " + theWord);
		wordDisp.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/14.0));
		wordDisp.setTextOrigin(VPos.CENTER);
		wordDisp.setTextAlignment(TextAlignment.CENTER);
		wordDisp.setWrappingWidth(screenWidth);
		
		AnchorPane top = new AnchorPane(winLose, wordDisp);
		top.setPrefSize(screenWidth, 2.0*screenHeight/3.0);
		top.setBackground(new Background(new BackgroundFill(background, null, null)));
		AnchorPane.setTopAnchor(winLose, top.getPrefHeight()/4.0);
		AnchorPane.setTopAnchor(wordDisp, 2*top.getPrefHeight()/3.0);
		
		
		Pane regular = new Pane();
		regular.setPrefSize(screenWidth/6.0, 2*screenHeight/9.0);
		regular.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		reset(1);
	        }
	    });
		addHoverAnimation(regular);
		Text regularText = new Text(0, regular.getPrefHeight()/2.0, "Click here to play regular mode!");
		regularText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/44.0));
		regularText.setWrappingWidth(regular.getPrefWidth());
		regularText.setTextAlignment(TextAlignment.CENTER);
		regularText.setTextOrigin(VPos.CENTER);
		regular.getChildren().add(regularText);
		
		Pane scrabble = new Pane();
		scrabble.setPrefSize(regular.getPrefWidth(), regular.getPrefHeight());
		scrabble.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
		        	reset(2);
	        }
	    });
		addHoverAnimation(scrabble);
		Text scrabbleText = new Text(0, scrabble.getPrefHeight()/2.0, "Click here to play scrabble words mode!");
		scrabbleText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/44.0));
		scrabbleText.setWrappingWidth(scrabble.getPrefWidth());
		scrabbleText.setTextAlignment(TextAlignment.CENTER);
		scrabbleText.setTextOrigin(VPos.CENTER);
		scrabble.getChildren().add(scrabbleText);
		
		Pane insane = new Pane();
		insane.setPrefSize(regular.getPrefWidth(), regular.getPrefHeight());
		insane.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
		        	reset(3);
	        }
	    });
		addHoverAnimation(insane);
		Text insaneText = new Text(0, insane.getPrefHeight()/2.0, "Click here to play insane mode!");
		insaneText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/44.0));
		insaneText.setWrappingWidth(insane.getPrefWidth());
		insaneText.setTextAlignment(TextAlignment.CENTER);
		insaneText.setTextOrigin(VPos.CENTER);
		insane.getChildren().add(insaneText);
		
		Line regScrabMedian = new Line();
		regScrabMedian.setEndY(regular.getPrefHeight());
		Line scrabInsaneMedian = new Line();
		scrabInsaneMedian.setEndY(regular.getPrefHeight());
		
		HBox playAgainOptions = new HBox(regular, regScrabMedian, scrabble, scrabInsaneMedian, insane);
		
		Pane playAgainTextBox = new Pane();
		playAgainTextBox.setPrefSize(screenWidth/2.0, screenHeight/9.0);
		Text playAgainText = new Text(0, playAgainTextBox.getPrefHeight()/2.0, "Play Again?");
		playAgainText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/22.0));
		playAgainText.setWrappingWidth(screenWidth/2.0);
		playAgainText.setTextAlignment(TextAlignment.CENTER);
		playAgainText.setTextOrigin(VPos.CENTER);
		playAgainTextBox.getChildren().add(playAgainText);
		
		Line playAgainTextBoxPlayAgainOptionsMedian = new Line(); //YEET
		playAgainTextBoxPlayAgainOptionsMedian.setEndX(screenWidth/2.0);
		
		VBox playAgain = new VBox(playAgainTextBox, playAgainTextBoxPlayAgainOptionsMedian, playAgainOptions);
		
		
		Pane end = new Pane();
		end.setPrefSize(screenWidth/2.0, screenHeight/3.0);
		end.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
		        	mainStage.setScene(endScene());
	        }
	    });
		addHoverAnimation(end);
		Text endText = new Text(0, end.getPrefHeight()/2.0, "End");
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/11.0));
		endText.setWrappingWidth(screenWidth/2.0);
		endText.setTextAlignment(TextAlignment.CENTER);
		endText.setTextOrigin(VPos.CENTER);
		end.getChildren().add(endText);
		
		Line median = new Line();
		median.setEndY(screenHeight/3.0);
		
		HBox bottom = new HBox(playAgain, median, end);
		bottom.setPrefSize(screenWidth, screenHeight/3.0);
		
		return new Scene(new VBox(top, bottom));
	}
	
	/**
	 * Makes the scene used if the player chooses to end the game
	 * @return The final game scene
	 */
	private Scene endScene() {
		double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
		
		String endMessage;
		switch(numWins) {
			case 0:
				endMessage = "You never won :(";
				break;
			case 1:
				endMessage = "You won once!";
				break;
			case 2:
				endMessage = "You won twice!";
				break;
			default:
				endMessage = "YOU WON " + numWins + " TIMES!";
				break;
		}
		
		Text endText = new Text(endMessage);
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/11.0));
		endText.setWrappingWidth(screenWidth);
		endText.setTextAlignment(TextAlignment.CENTER);
		
		Text sass = new Text("and never will...");
		sass.setFont(Font.font("helvetica", FontWeight.BOLD, screenWidth/77.0));
		sass.setWrappingWidth(screenWidth);
		sass.setTextAlignment(TextAlignment.RIGHT);
		
		AnchorPane endView = new AnchorPane(endText);
		if(numWins==0)
			endView.getChildren().add(sass);
		endView.setPrefSize(screenWidth, screenHeight);
		endView.setBackground(new Background(new BackgroundFill(background, null, null)));
		AnchorPane.setTopAnchor(endText, screenHeight/3.0);
		
		AnchorPane.setBottomAnchor(sass, .7*sass.getFont().getSize());
		
		return new Scene(endView);
	}
	
	/**
	 * Updates the game by changing {@link #progress} and {@link #enteredLetters}
	 * as well as their respective text objects: {@link #progressText} and {@link #enteredLettersText}
	 * @param guess The character guessed by the player
	 */
	private void update(String guess) {
		if(first) {
			first = false;
			
			fade(directions, 1500, true);
		}
		
		if(!theWord.contains(guess) && !enteredLetters.contains(guess)) {
			if(!justLost) {
				fade(body.get(((difficultyLevel==1) ? 6 : 8) - lives), 800, false);

				lives--;

				livesText.setText("Lives: " + lives);
				enteredLetters += (enteredLetters.equals("")) ? guess : ", " + guess;
			}
			
			if(lives==0) {
				//System.out.println("You lose!");
				justLost = true;
				Task<Void> sleeper = new Task<Void>() {
		            @Override
		            protected Void call() throws Exception {
		                try {Thread.sleep(800);} catch (InterruptedException e) {}
		                return null;
		            }
		        };
		        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		            @Override
		            public void handle(WorkerStateEvent event) {
		            		mainStage.setScene(gameEndScene(false));
		            }
		        });
		        new Thread(sleeper).start();
				return;
			}
		}
		else if(enteredLetters.contains(guess)) {
			fade(alreadyEntered, 1000, true);
			//System.out.println("\nYou have already entered this letter");
		}
		else {
			String temp = "";
			for(int i = 0; i<theWord.length(); i++) {
				if((theWord.charAt(i)+"").equals(guess)) {
					temp += theWord.charAt(i) + " ";
				}
				else {
					temp += progress.charAt(i*2) + " "; //i*2 so that spaces are skipped
				}
			}
			progress = temp;
			
			if(!justLost) { //Prevents a user from losing then quickly pressing the winning letter to win
				progressText.setText(progress.toUpperCase()); //Upper Case looks better
				enteredLetters += (enteredLetters.equals("")) ? guess : ", " + guess;
				
				if(!progress.contains("_")) {
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
			            		mainStage.setScene(gameEndScene(true));
			            }
			        });
			        new Thread(sleeper).start();
					return;
				}
			}
		}
		//System.out.println("Entered Letters:\n" + enteredLetters);
		enteredLettersText.setText("Entered Letters:\n" + enteredLetters);
	}
	
	/**
	 * Resets all the game variables so it can played again
	 * <p>Generates a new main scene
	 * @param difficultyLevel The difficulty of the new game being played
	 */
	private void reset(int difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
		lives = (difficultyLevel==1) ? 6 : 8;
		try {
			theWord = getWord();
		} catch (IOException e) {e.printStackTrace();}
		progress = "";
		enteredLetters = "";
		try {
			mainStage.setScene(mainScene());
		} catch (FileNotFoundException e) {e.printStackTrace();}
	}
	
	/**
	 * Came from <a href="https://stackoverflow.com/questions/11719005/draw-a-semi-ring-javafx">this</a>
	 * StackOverflow page
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param innerRadius
	 * @param bgColor
	 * @param strkColor
	 * @return
	 */
	private Path drawSemiRing(double centerX, double centerY, double radius, double innerRadius, Color bgColor, Color strkColor) {
        Path path = new Path();
        path.setFill(bgColor);
        path.setStroke(strkColor);
        path.setFillRule(FillRule.EVEN_ODD);

        MoveTo moveTo = new MoveTo();
        moveTo.setX(centerX + innerRadius);
        moveTo.setY(centerY);

        ArcTo arcToInner = new ArcTo();
        arcToInner.setX(centerX - innerRadius);
        arcToInner.setY(centerY);
        arcToInner.setRadiusX(innerRadius);
        arcToInner.setRadiusY(innerRadius);

        MoveTo moveTo2 = new MoveTo();
        moveTo2.setX(centerX + innerRadius);
        moveTo2.setY(centerY);

        HLineTo hLineToRightLeg = new HLineTo();
        hLineToRightLeg.setX(centerX + radius);

        ArcTo arcTo = new ArcTo();
        arcTo.setX(centerX - radius);
        arcTo.setY(centerY);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);

        HLineTo hLineToLeftLeg = new HLineTo();
        hLineToLeftLeg.setX(centerX - innerRadius);

        path.getElements().add(moveTo);
        path.getElements().add(arcToInner);
        path.getElements().add(moveTo2);
        path.getElements().add(hLineToRightLeg);
        path.getElements().add(arcTo);
        path.getElements().add(hLineToLeftLeg);

        return path;
	}
	
	/**
	 * Applies a fade animation to the node passed in
	 * @param n The node to apply the animation to
	 * @param millis The duration of the fade in milliseconds
	 * @param fadeOut True if the animation will be used to fade an object out (reduce opacity to zero)
	 */
	private void fade(Node n, double millis, boolean fadeOut) {
		FadeTransition fade = new FadeTransition(Duration.millis(millis), n);
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
	
	/**
	 * Applies a color change animation for when the pane p is hovered over
	 * @param p The {@linkplain Pane} to apply the animation to
	 */
	private void addHoverAnimation(Pane p) {
		p.setBackground(new Background(new BackgroundFill(hoverColor, null, null)));
		p.setOnMouseEntered(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		p.setBackground(new Background(new BackgroundFill(null, null, null)));
	        }
	    });
		p.setOnMouseExited(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        		p.setBackground(new Background(new BackgroundFill(hoverColor, null, null)));
	        }
	    });
	}
	
	/**
	 * Gets the word that the player will be guessing
	 * @param difficultyLvl The difficulty of the game, either 1 or 2. This correlates to the wordlist that will be chosen
	 * @return The chosen word
	 * @throws IOException 
	 */
	private String getWord() throws IOException {
		//This chooses which file to use based on the difficulty level
		InputStream input = null;
		if(difficultyLevel==1) {
			input = Hangman.class.getResourceAsStream("/WordLists/CommonWords.txt");
		}
		else if(difficultyLevel==2) {
			input = Hangman.class.getResourceAsStream("/WordLists/Scrabble.txt");
		}
		else if(difficultyLevel==3) {
			input = Hangman.class.getResourceAsStream("/WordLists/Words.txt");
		}
		
		List<String> words = Arrays.asList(extract(input).split("\n"));
		
		//Creates a random integer less than the number of words, and gets the list value at that location
		int rndWord = (int) (Math.random()*words.size());
		while(
			!words.get(rndWord).toLowerCase().contains("a") &&
			!words.get(rndWord).toLowerCase().contains("e") &&
			!words.get(rndWord).toLowerCase().contains("i") &&
			!words.get(rndWord).toLowerCase().contains("o") &&
			!words.get(rndWord).toLowerCase().contains("u") &&
			!words.get(rndWord).toLowerCase().contains("y") ||
			words.get(rndWord).length()<3) {
			rndWord = (int) (Math.random()*words.size());
		}
		String word = words.get(rndWord).toLowerCase();
		return word.substring(0, word.length()-1);
	}
	
	/**
	 * @see <a href="http://www.gregbugaj.com/?p=283">Source</a>
	 * @param inputStream InputStream to read from
	 * @return The content from the stream
	 * @throws IOException
	 */
	public static String extract(InputStream inputStream) throws IOException {	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();				
		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
			baos.write(buffer, 0, read);
		}		
		baos.flush();		
		return  new String(baos.toByteArray(), "UTF-8");
	}
}
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Checkers extends Application {
	public static Stage mainStage;
	private static double gameHeight;
	private static double gameWidth;
	public static double tileSize;
	public static Color background;
	public static Spot[][] spot;
	private static Spot selectedSpot;
	public static int turn;
	public static int[] pieces;
	public static Text turnText;
	public static Text[] piecesText;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		gameHeight = Screen.getPrimary().getVisualBounds().getHeight() - 50;
		gameWidth = gameHeight * 1.5;
		tileSize = gameHeight / 8;
		
		background = Color.RED;
		
		turn = Math.random() > .5 ? 0 : 1;
	}
	
	@Override
	public void start(Stage theStage) throws Exception {
		mainStage = theStage;
		mainStage.setScene(initScene());
		mainStage.setResizable(false);
		mainStage.setTitle("Checkers");
		mainStage.show();
	}
	
	private Scene initScene() {
		Pane main = new Pane();
		main.setPrefSize(gameWidth, gameHeight);
		main.setBackground(new Background(new BackgroundFill(background, null, null)));
		Text welcomeMessage = new Text(0, main.getPrefHeight() / 3.0, "Welcome to Checkers!"); //the horizontal position of the text, the vertical position of the text, the text
		welcomeMessage.setWrappingWidth(gameWidth);
		welcomeMessage.setTextOrigin(VPos.CENTER);
		welcomeMessage.setTextAlignment(TextAlignment.CENTER);
		welcomeMessage.setFont(Font.font("helvetica", FontWeight.BOLD, 75));
		main.getChildren().add(welcomeMessage);
		
		Pane playBtn = new Pane();
		playBtn.setPrefSize(main.getPrefWidth() / 2, main.getPrefHeight() / 4);
		playBtn.setBackground(new Background(new BackgroundFill(Color.grayRgb(120), null, null)));
		playBtn.setTranslateX(gameWidth / 2 - playBtn.getPrefWidth() / 2);
		playBtn.setTranslateY(gameHeight * 2/3);
		playBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainStage.setScene(mainScene());
			}
		});
		Text playText = new Text(0, playBtn.getPrefHeight() / 2.0, "Play!"); //the horizontal position of the text, the vertical position of the text, the text
		playText.setWrappingWidth(playBtn.getPrefWidth());
		playText.setTextOrigin(VPos.CENTER);
		playText.setTextAlignment(TextAlignment.CENTER);
		playText.setFont(Font.font("helvetica", FontWeight.BOLD, 50));
		playText.setFill(Color.WHITE);
		playBtn.getChildren().add(playText);
		
		double offset = playBtn.getPrefWidth() / 50;
		Polygon[] playBtnGradient = btnGradient(playBtn, offset);
		applyHidingBehavior(playBtn, playBtnGradient, offset);
		
		main.getChildren().add(playBtn);
		main.getChildren().addAll(playBtnGradient);
		
		return new Scene(main);
	}
	
	private static Scene mainScene() {
		HBox main = new HBox();
		
		GridPane grid = new GridPane();
		spot = new Spot[8][8];
		for(int r = 0; r<8; r++) {
			for(int c = 0; c<8; c++) {
				boolean isBlack = (r & 1) == (c & 1);
				boolean occupy = (isBlack && r < 3) || (isBlack && r > 4);
				int player = -1;
				if(r < 3) player = 0;
				else if(r > 4) player = 1;
				spot[r][c] = new Spot(r, c, player, isBlack, occupy);
				grid.add(spot[r][c], c, r);
			}
		}
		
		
		Pane side = new Pane();
		side.setBackground(new Background(new BackgroundFill(background.darker(), null, null)));
		side.setPrefSize(gameHeight / 2, gameHeight);
		
		turnText = new Text(0, 50, "Turn: " + (turn == 0 ? "White" : "Colored"));
		turnText.setWrappingWidth(side.getPrefWidth());
		turnText.setTextOrigin(VPos.CENTER);
		turnText.setTextAlignment(TextAlignment.CENTER);
		turnText.setFont(Font.font("helvetica", FontWeight.BOLD, 50));
		turnText.setFill(Color.WHITE);
		
		pieces = new int[2]; pieces[0] = 12; pieces[1] = 12;
		piecesText = new Text[2];
		piecesText[0] = new Text(0, 150, "White Pieces: 12");
		piecesText[1] = new Text(0, 200, "Colored Pieces: 12");
		for(int i = 0; i<2; i++) {
			piecesText[i].setWrappingWidth(side.getPrefWidth());
			piecesText[i].setTextOrigin(VPos.CENTER);
			piecesText[i].setTextAlignment(TextAlignment.CENTER);
			piecesText[i].setFont(Font.font("helvetica", FontWeight.BOLD, side.getPrefWidth() / 10));
			piecesText[i].setFill(Color.WHITE);
		}
		
		side.getChildren().addAll(turnText, piecesText[0], piecesText[1]);
		
		main.getChildren().addAll(grid, side);
		
		return new Scene(main);
	}
	
	public static Scene endScene() {
		Pane bgPane = new Pane();
		bgPane.setPrefSize(gameWidth, gameHeight);
		bgPane.setBackground(new Background(new BackgroundFill(background, null, null)));
		
		Text endText = new Text(0, gameHeight * .25, "GAME OVER!");
		endText.setWrappingWidth(bgPane.getPrefWidth());
		endText.setTextOrigin(VPos.CENTER);
		endText.setTextAlignment(TextAlignment.CENTER);
		endText.setFont(Font.font("helvetica", FontWeight.BOLD, bgPane.getPrefWidth() / 10));
		endText.setFill(Color.WHITE);
		
		Text winnerText = new Text(0, gameHeight * .4, (turn == 1 ? "White" : "Colored") + " Player Wins!");
		winnerText.setWrappingWidth(bgPane.getPrefWidth());
		winnerText.setTextOrigin(VPos.CENTER);
		winnerText.setTextAlignment(TextAlignment.CENTER);
		winnerText.setFont(Font.font("helvetica", FontWeight.BOLD, bgPane.getPrefWidth() / 15));
		winnerText.setFill(Color.WHITE);
		
		Pane playAgainBtn = new Pane();
		playAgainBtn.setPrefSize(gameWidth / 2, gameHeight * .2);
		playAgainBtn.setBackground(new Background(new BackgroundFill(Color.grayRgb(120), null, null)));
		playAgainBtn.setTranslateX(gameWidth / 2 - playAgainBtn.getPrefWidth() / 2); playAgainBtn.setTranslateY(gameHeight * .6);
		playAgainBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainStage.setScene(mainScene());
			}
		});
		
		Text playAgainBtnText = new Text(0, playAgainBtn.getPrefHeight()/2, "Play Again!");
		playAgainBtnText.setWrappingWidth(playAgainBtn.getPrefWidth());
		playAgainBtnText.setTextOrigin(VPos.CENTER);
		playAgainBtnText.setTextAlignment(TextAlignment.CENTER);
		playAgainBtnText.setFont(Font.font("helvetica", FontWeight.BOLD, playAgainBtn.getPrefWidth() / 10));
		playAgainBtnText.setFill(Color.WHITE);
		playAgainBtn.getChildren().add(playAgainBtnText);
		
		double offset = playAgainBtn.getPrefWidth() / 50;
		Polygon[] playAgainBtnGradient = btnGradient(playAgainBtn, offset);
		applyHidingBehavior(playAgainBtn, playAgainBtnGradient, offset);
		
		bgPane.getChildren().addAll(endText, winnerText, playAgainBtn);
		bgPane.getChildren().addAll(playAgainBtnGradient);
		
		return new Scene(bgPane);
	}
	
	private static Polygon[] btnGradient(Pane frontPane, double offset) {
		Stop[] stops = {new Stop(0, ((Color) frontPane.getBackground().getFills().get(0).getFill()).darker()), new Stop(1, Color.BLACK)};
		Polygon topGradient = new Polygon();
		topGradient.getPoints().addAll(
			frontPane.getTranslateX(),										frontPane.getTranslateY(),
			frontPane.getTranslateX() + offset,								frontPane.getTranslateY() - offset,
			frontPane.getTranslateX() + offset + frontPane.getPrefWidth(),	frontPane.getTranslateY() - offset,
			frontPane.getTranslateX() + frontPane.getPrefWidth(),			frontPane.getTranslateY(),
			frontPane.getTranslateX(),										frontPane.getTranslateY()
		);
		topGradient.setStroke(Color.BLACK);
		topGradient.setStrokeWidth(0);
		topGradient.setFill(new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops));
		
		Polygon rightGradient = new Polygon();
		rightGradient.getPoints().addAll(
			frontPane.getTranslateX() + frontPane.getPrefWidth(),			frontPane.getTranslateY(),
			frontPane.getTranslateX() + offset + frontPane.getPrefWidth(),	frontPane.getTranslateY() - offset,
			frontPane.getTranslateX() + offset + frontPane.getPrefWidth(),	frontPane.getTranslateY() - offset + frontPane.getPrefHeight(),
			frontPane.getTranslateX() + frontPane.getPrefWidth(),			frontPane.getTranslateY() + frontPane.getPrefHeight(),
			frontPane.getTranslateX() + frontPane.getPrefWidth(),			frontPane.getTranslateY()
		);
		rightGradient.setStroke(Color.BLACK);
		rightGradient.setStrokeWidth(0);
		rightGradient.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
		
		Polygon[] gradients = {topGradient, rightGradient};
		return gradients;
	}
	private static void applyHidingBehavior(Pane btn, Polygon[] btnGradient, double offset) {
		btn.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				btn.setTranslateX(btn.getTranslateX() + offset);
				btn.setTranslateY(btn.getTranslateY() - offset);
				
				btnGradient[0].setVisible(false);
				btnGradient[1].setVisible(false);
			}
		});
		btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				btn.setTranslateX(btn.getTranslateX() - offset);
				btn.setTranslateY(btn.getTranslateY() + offset);
				
				btnGradient[0].setVisible(true);
				btnGradient[1].setVisible(true);
			}
		});
	}
	
	public static Spot getSelectedSpot() { return selectedSpot; }
	public static void setSelectedSpot(Spot newSpot) { selectedSpot = newSpot; }
}

class Checker extends Group {
	private Circle circle;
	private Circle kingDot;
	private final Color background;
	private boolean king;
	
	public Checker(int player, double tileSize, Color background) {
		this.background = background;
		king = false;
		
		circle = new Circle(tileSize / 2, tileSize / 2, .4 * tileSize, player == 0 ? Color.WHITE : background);
		kingDot = new Circle(tileSize / 2, tileSize / 2, .05 * tileSize, Color.BLACK);
		
		super.getChildren().add(circle);
	}
	
	public void changePlayer(int player) {
		circle.setFill(player == 0 ? Color.WHITE : background);
	}
	
	public void setKing(boolean king) {
		this.king = king;
		if(king)
			super.getChildren().add(kingDot);
		else
			super.getChildren().remove(kingDot);
	}
	public boolean isKing() {
		return this.king;
	}
}

class Spot extends Pane {
	private int r, c, player;
	private final boolean isBlack;
	private Checker checker;
	private boolean occupied;
	
	private Rectangle[] selectedBorder;
	
	public Spot(int r, int c, int player, boolean isBlack, boolean occupy) {
		double tileSize = Checkers.tileSize;
		Color background = Checkers.background;
		
		this.r = r; this.c = c;
		this.player = player;
		this.isBlack = isBlack;
		this.occupied = occupy;
		this.checker = new Checker(player, tileSize, background);
		if(occupied) {
			super.getChildren().add(checker);
		}
		
		super.setBackground(new Background(new BackgroundFill(isBlack ? Color.BLACK : background, null, null)));
		super.setPrefSize(tileSize, tileSize);
		
		selectedBorder = new Rectangle[4];
		double borderWidth = .05 * tileSize;
		for(int i = 0; i<4; i++) {
			selectedBorder[i] = new Rectangle(i % 2 == 1 ? tileSize : borderWidth, i % 2 == 1 ? borderWidth : tileSize, Color.WHITE); // width, height, fill
		}
		selectedBorder[2].setX(tileSize - borderWidth);
		selectedBorder[3].setY(tileSize - borderWidth);
		
		super.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(isBlack) {
					if(occupied)
						selectStart();
					else
						selectEnd();
				}
			}
		});
	}
	
	private void selectStart() {
		if(Checkers.turn != this.player) return;
		
		Spot spot = Checkers.getSelectedSpot();
		
		System.out.println("Start\nSelected Spot: " + spot);
		System.out.println("This Spot: " + this + "\n");
		
		if(spot == this) { // If you re-clicked the currently selected spot
			Checkers.setSelectedSpot(null);
			for(Rectangle selectedBorderPiece : selectedBorder)
				super.getChildren().remove(selectedBorderPiece);
			return;
		}
		
		if(spot == null || (spot.player == this.player && this.occupied)) {
			// Select the spot that was clicked
			if(spot != null) {
				spot.unselect();
			}
			
			Checkers.setSelectedSpot(this);
			for(Rectangle selectedBorderPiece : selectedBorder)
				super.getChildren().add(selectedBorderPiece);
		}
	}
	private void selectEnd() {
		Spot spot = Checkers.getSelectedSpot();
		
		System.out.println("End\nSelected Spot: " + spot);
		System.out.println("This Spot: " + this + "\n");
		
		if(spot == null || !spot.isBlack)
			return;
		
		byte mult = 1;
		if(spot.player == 1)
			mult = -1;
		
		if(Math.abs(this.c - spot.c) == 1  &&
			(this.r - spot.r == mult || (spot.checker.isKing() && this.r - spot.r == -mult))
		) {
			moveChecker(false);
		}
		else if(Math.abs(this.c - spot.c) == 2 &&
				(this.r - spot.r == mult * 2 || (spot.checker.isKing() && this.r - spot.r == -mult * 2)) &&
				Checkers.spot[(this.r + spot.r) / 2][(this.c + spot.c) / 2].player != spot.player &&
				Checkers.spot[(this.r + spot.r) / 2][(this.c + spot.c) / 2].player != -1) {
			Checkers.spot[(this.r + spot.r) / 2][(this.c + spot.c) / 2].unoccupy();
			moveChecker(false);
			
			Checkers.piecesText[Checkers.turn].setText(String.format("%s Pieces: %s", Checkers.turn==0 ? "White" : "Colored", --Checkers.pieces[Checkers.turn]));
			if(Checkers.pieces[Checkers.turn] == 0) {
				Checkers.mainStage.setScene(Checkers.endScene());
			}
		}
	}
	
	private void moveChecker(boolean moveAgain) {
		Spot spot = Checkers.getSelectedSpot();
		this.occupy();
		spot.unselect();
		spot.unoccupy();
		Checkers.setSelectedSpot(null);
		
		if(!moveAgain) {
			Checkers.turn = Checkers.turn == 0 ? 1 : 0;
			Checkers.turnText.setText("Turn: " + (Checkers.turn == 0 ? "White" : "Colored"));
		}
	}
	
	private void unselect() {
		for(Rectangle selectedBorderPiece : selectedBorder)
			super.getChildren().remove(selectedBorderPiece);
	}
	
	private void occupy() {
		Spot spot = Checkers.getSelectedSpot();
		this.player = spot.player;
		this.checker.changePlayer(spot.player);
		this.checker.setKing(spot.checker.isKing());
		if(!this.checker.isKing() && ((this.r == 0 && this.player == 1) || (this.r == 7 && this.player == 0))) {
			this.checker.setKing(true);
			System.out.println("Kinging checker: " + this + "\n");
		}
		this.occupied = true;
		super.getChildren().add(this.checker);
	}
	private void unoccupy() {
		this.player = -1;
		
		this.occupied = false;
		super.getChildren().remove(this.checker);
		this.checker.setKing(false);
	}
	
	@Override
	public String toString() {
		return String.format("{Row: %s, Col: %s, Player: %s}", r, c, player);
	}
}
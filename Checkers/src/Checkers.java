import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
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
	private Stage mainStage;
	private double gameHeight;
	private double gameWidth;
	private double tileSize;
	private Color background;
	public static Spot[][] spot;
	private static Spot selectedSpot;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		gameHeight = Screen.getPrimary().getVisualBounds().getHeight() - 50;
		gameWidth = gameHeight*1.5;
		tileSize = gameHeight/8;
		
		background = Color.RED;
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
		Text welcomeMessage = new Text(0, main.getPrefHeight()/3.0, "Welcome to Checkers!"); //the horizontal position of the text, the vertical position of the text, the text
		welcomeMessage.setWrappingWidth(gameWidth);
		welcomeMessage.setTextOrigin(VPos.CENTER);
		welcomeMessage.setTextAlignment(TextAlignment.CENTER);
		welcomeMessage.setFont(Font.font("helvetica", FontWeight.BOLD, 75));
		main.getChildren().add(welcomeMessage);
		
		Pane playBtn = new Pane();
		playBtn.setPrefSize(main.getPrefWidth()/2, main.getPrefHeight()/4);
		playBtn.setBackground(new Background(new BackgroundFill(Color.grayRgb(120), null, null)));
		playBtn.setTranslateX(gameWidth/2 - playBtn.getPrefWidth()/2);
		playBtn.setTranslateY(gameHeight*2/3);
		playBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mainStage.setScene(mainScene());
			}
		});
		Text playText = new Text(0, playBtn.getPrefHeight()/2.0, "Play!"); //the horizontal position of the text, the vertical position of the text, the text
		playText.setWrappingWidth(playBtn.getPrefWidth());
		playText.setTextOrigin(VPos.CENTER);
		playText.setTextAlignment(TextAlignment.CENTER);
		playText.setFont(Font.font("helvetica", FontWeight.BOLD, 50));
		playText.setFill(Color.WHITE);
		playBtn.getChildren().add(playText);
		
		double offset = playBtn.getPrefWidth()/50;
		Polygon[] playBtnGradient = btnGradient(playBtn, offset);
		
		main.getChildren().add(playBtn);
		main.getChildren().addAll(playBtnGradient);
		
		playBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				playBtn.setTranslateX(playBtn.getTranslateX() + offset);
				playBtn.setTranslateY(playBtn.getTranslateY() - offset);
				
				playBtnGradient[0].setVisible(false);
				playBtnGradient[1].setVisible(false);
			}
		});
		playBtn.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				playBtn.setTranslateX(playBtn.getTranslateX() - offset);
				playBtn.setTranslateY(playBtn.getTranslateY() + offset);
				
				playBtnGradient[0].setVisible(true);
				playBtnGradient[1].setVisible(true);
			}
		});
		
		return new Scene(main);
	}
	
	private Scene mainScene() {
		HBox main = new HBox();
		
		GridPane grid = new GridPane();
		spot = new Spot[8][8];
		for(int r = 0; r<8; r++) {
			for(int c = 0; c<8; c++) {
				boolean isBlack = (r & 1) == (c & 1);
				boolean occupy = (isBlack && r < 3) || (isBlack && r > 4);
				int player = 0;
				if(r < 3) player = 1;
				else if(r > 4) player = 2;
				spot[r][c] = new Spot(r, c, player, isBlack, occupy, tileSize, background);
				grid.add(spot[r][c], c, r);
			}
		}
		
		
		Pane side = new Pane();
		side.setBackground(new Background(new BackgroundFill(background.darker(), null, null)));
		side.setPrefSize(gameHeight/2, gameHeight);
		
		main.getChildren().addAll(grid, side);
		
		return new Scene(main);
	}
	
	private Polygon[] btnGradient(Pane frontPane, double offset) {
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
	
	public static Spot getSelectedSpot() { return selectedSpot; }
	public static void setSelectedSpot(Spot newSpot) { selectedSpot = newSpot; }
}

class Checker extends Circle {
	private final Color background;
	
	public Checker(int player, double tileSize, Color background) {
		this.background = background;
		
		super.setCenterX(tileSize/2);
		super.setCenterY(tileSize/2);
		super.setRadius(.4*tileSize);
		super.setFill(player == 1 ? Color.WHITE : background);
	}
	
	public void changePlayer(int player) {
		super.setFill(player == 1 ? Color.WHITE : background);
	}
}

class Spot extends Pane {
	private int r, c, player;
	private final boolean isBlack;
	private Checker checker;
	private boolean occupied;
	
	private Rectangle[] selectedBorder;
	
	public Spot(int r, int c, int player, boolean isBlack, boolean occupy, double tileSize, Color background) {
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
		if(spot.player == 2)
			mult = -1;
		
		if(this.r - spot.r == mult*1 && Math.abs(this.c - spot.c) == 1) {
			moveChecker();
		}
		else if(this.r - spot.r == mult*2 && Math.abs(this.c - spot.c) == 2 &&
				Checkers.spot[(this.r + spot.r)/2][(this.c + spot.c)/2].player != spot.player &&
				Checkers.spot[(this.r + spot.r)/2][(this.c + spot.c)/2].player != 0) {
			Checkers.spot[(this.r + spot.r)/2][(this.c + spot.c)/2].unoccupy();
			moveChecker();
		}
	}
	
	private void moveChecker() {
		Spot spot = Checkers.getSelectedSpot();
		this.occupy(spot.player);
		spot.unselect();
		spot.unoccupy();
		Checkers.setSelectedSpot(null);
	}
	
	private void unselect() {
		for(Rectangle selectedBorderPiece : selectedBorder)
			super.getChildren().remove(selectedBorderPiece);
	}
	
	private void occupy(int player) {
		this.player = player;
		this.checker.changePlayer(player);
		this.occupied = true;
		super.getChildren().add(checker);
	}
	private void unoccupy() {
		this.player = 0;
		this.occupied = false;
		super.getChildren().remove(checker);
	}
	
	@Override
	public String toString() {
		return String.format("{Row: %s, Col: %s, Player: %s}", r, c, player);
	}
}
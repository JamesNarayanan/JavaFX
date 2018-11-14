import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SnakeGame extends Application {
	
	private Stage mainStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void start(Stage primaryStage) {
		mainStage = primaryStage;
		mainStage.setScene(mainScene());
		mainStage.setTitle("Snake");
		mainStage.setResizable(false);
		mainStage.show();
	}
	
	private Scene mainScene() {
		Grid grid = new Grid();
		grid.newSnake();
		grid.startTimer();
		
		Scene scene = new Scene(grid.getGrids());
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				grid.getSnake().turn(Direction.keyCodeToDir(event.getCode()));
			}
		});
		return scene;
	}
}

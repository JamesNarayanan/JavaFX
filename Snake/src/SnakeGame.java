import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SnakeGame extends Application {
	
	private Stage mainStage;
	private int step = 1;
	
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
		
		Scene scene = new Scene(grid.getPanes());
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			boolean first = true;
			@Override
			public void handle(KeyEvent event) {
				if(grid.getStart()) {
					if(first) {
						first = false;
						grid.startTimer();
						grid.getSnake().turn(Direction.keyCodeToDir(event.getCode()));
					}
					else {
						if(step==1) {
							grid.newSnake();
							scene.setRoot(grid.getPanes());
							step++;
						}
						else {
							grid.startTimer();
							grid.getSnake().turn(Direction.keyCodeToDir(event.getCode()));
							step--;
						}
					}
				}
				else {
					if(event.getCode()==KeyCode.SPACE) {
						if(grid.isPaused())
							grid.resume();
						else
							grid.pause();
					}
					grid.getSnake().turn(Direction.keyCodeToDir(event.getCode()));
				}
			}
		});
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(grid.getStart()) {
					if(step==1) {
						grid.newSnake();
						scene.setRoot(grid.getPanes());
						step++;
					}
				}
			}
		});
		return scene;
	}
}

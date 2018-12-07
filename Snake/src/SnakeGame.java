import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SnakeGame extends Application {
	public Stage mainStage;
	public int step;
	private boolean first;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		step = 1;
		first = true;
	}
	
	@Override
	public void start(Stage primaryStage) {
		mainStage = primaryStage;
		mainStage.setScene(mainScene());
		mainStage.setTitle("Snake");
		mainStage.setResizable(false);
		mainStage.show();
	}
	
	private Scene mainScene() { //Test master
		Grid grid = new Grid(this);
		grid.newSnake();
		
		Scene scene = new Scene(grid.getPanes());
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
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
		return scene;
	}
}

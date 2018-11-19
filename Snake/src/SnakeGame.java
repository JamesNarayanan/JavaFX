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
		
		Scene scene = new Scene(grid.getPanes());
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			boolean first = true;
			int step = 1;
			@Override
			public void handle(KeyEvent event) {
				if(grid.getStart()) {
					if(first) {
						first = false;
						grid.startTimer();
					}
					else {
						if(step==1) {
							grid.newSnake();
							scene.setRoot(grid.getPanes());
							step++;
						}
						else {
							grid.startTimer();
							step--;
						}
					}
					
				}
				else
					grid.getSnake().turn(Direction.keyCodeToDir(event.getCode()));
			}
		});
		return scene;
	}
}

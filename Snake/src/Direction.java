import javafx.scene.input.KeyCode;

public enum Direction {
	UP, DOWN, LEFT, RIGHT;
	
	@SuppressWarnings("incomplete-switch")
	public static Direction keyCodeToDir(KeyCode code) {
		switch(code) {
		case UP:
			return Direction.UP;
		case DOWN:
			return Direction.DOWN;
		case LEFT:
			return Direction.LEFT;
		case RIGHT:
			return Direction.RIGHT;
		}
		return null;
	}
	
	public static Direction oppositeDir(Direction dir) {
		switch(dir) {
		case UP:
			return Direction.DOWN;
		case DOWN:
			return Direction.UP;
		case LEFT:
			return Direction.RIGHT;
		case RIGHT:
			return Direction.LEFT;
		}
		return null;
	}
}

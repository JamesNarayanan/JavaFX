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
	
	public static Direction getOpposite(Direction dir) {
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
	
	/**
	 * Gets the point in the given direction from the original point
	 * @param dir The direction of the new point from the original
	 * @param point The original point
	 * @return The new point
	 */
	public static int[] getPointInDirection(Direction dir, int[] point) {
		int[] newPoint = new int[2];
		for(int i = 0; i<2; i++)
			newPoint[i] = point[i];
		
		switch(dir) {
		case UP:
			newPoint[1]--;
			break;
		case DOWN:
			newPoint[1]++;
			break;
		case LEFT:
			newPoint[0]--;
			break;
		case RIGHT:
			newPoint[0]++;
			break;
		}
		return newPoint;
	}
}

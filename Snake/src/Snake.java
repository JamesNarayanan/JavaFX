
import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Snake {
	private int[][] gridSpots;
	private Pane grid;
	private final double sideLength;
	private ArrayList<Rectangle> rects;
	
	/**
	 * Represents the x and y coordinates of the head
	 * <br>
	 * {@code head[0]} = x coordinate
	 * <br>
	 * {@code head[1]} = y coordinate
	 */
	private int[] head;
	private int length;
	private Direction dir;
	
	public Snake(int[][] gridSpots, int[] head, Pane grid, double sideLength) {
		this.gridSpots = gridSpots;
		this.head = head;
		this.grid = grid;
		this.sideLength = sideLength;
		this.length = 3;
		this.dir = Direction.RIGHT;
		this.rects = new ArrayList<>();
		
		makeSnake();
	}
	
	private void makeSnake() {
		for(int i = head[0]; i>head[0]-length; i--) {
			gridSpots[i][head[1]] = 1;
			Color c = i==head[0] ? Color.BLUE : Color.RED;
			Rectangle r = new Rectangle(sideLength, sideLength, c);
			r.setTranslateX(sideLength*i);
			r.setTranslateY(sideLength*head[1]);
			grid.getChildren().add(r);
			rects.add(r);
		}
	}
	
	public void move() {
		switch(dir) {
		case UP:
			for(Rectangle r : rects)
				r.setTranslateY(r.getTranslateY() - sideLength);
			head[1]--;
			break;
		case DOWN:
			for(Rectangle r : rects)
				r.setTranslateY(r.getTranslateY() + sideLength);
			head[1]++;
			break;
		case LEFT:
			for(Rectangle r : rects)
				r.setTranslateX(r.getTranslateX() - sideLength);
			head[0]--;
			break;
		case RIGHT:
			for(Rectangle r : rects)
				r.setTranslateX(r.getTranslateX() + sideLength);
			head[0]++;
			break;
		}
	}
	
	public void grow() {
		length++;
	}
	
	public void turn(Direction dir) {
		if(dir!=Direction.oppositeDir(this.dir))
			this.dir = dir;
	}
	
	public int[][] getGridSpots() {
		return gridSpots;
	}
	
	public int[] getHead() {
		return head;
	}
	
	public int getLength() {
		return length;
	}
	
	
}

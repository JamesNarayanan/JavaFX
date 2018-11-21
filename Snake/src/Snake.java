
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Snake {
	private Spot[][] gridSpots;
	private Pane grid;
	private final double sideLength;
	private ArrayList<Rectangle> rects;
	private ArrayList<int[]> snake;
	private ImageView food;
	private ArrayList<ArrayList<Object>> turnPoints;
	private int length;
	private int score;
	private ArrayList<Direction> directions;
	private boolean alreadyTurned;
	
	public Snake(Spot[][] gridSpots, int[] head, Pane grid, double sideLength) {
		this.gridSpots = gridSpots;
		this.grid = grid;
		this.sideLength = sideLength;
		this.length = 3;
		this.score = 0;
		this.snake = new ArrayList<>();
		snake.add(head);
		//this.food = new Rectangle(sideLength, sideLength, Color.YELLOW);
		//Image taken from Google's snake game
		try {
			this.food = new ImageView(new Image(new FileInputStream("Images/food.png")));
		} catch (FileNotFoundException e) {e.printStackTrace();}
		this.food.setFitWidth(sideLength); this.food.setFitHeight(sideLength);
		this.directions = new ArrayList<>();
		this.rects = new ArrayList<>();
		this.turnPoints = new ArrayList<>();
		this.alreadyTurned = false;
		
		makeSnake();
	}
	
	private void makeSnake() {
		for(int i = snake.get(0)[0]; i>snake.get(0)[0]-length; i--) {
			gridSpots[i][snake.get(0)[1]] = Spot.SNAKE;
			Color c = i==snake.get(0)[0] ? Color.BLUE : Color.RED;
			Rectangle r = new Rectangle(sideLength, sideLength, c);
			r.setTranslateX(sideLength*i);
			r.setTranslateY(sideLength*snake.get(0)[1]);
			grid.getChildren().add(r);
			directions.add(Direction.RIGHT); //Start snake facing right
			if(i!=snake.get(0)[0])
				snake.add(new int[] {i, snake.get(0)[1]});
			rects.add(r);
		}

		newFood();
		grid.getChildren().add(food);
	}
	
	public boolean move() {
		alreadyTurned = false;
		
		for(int i = 0; i<length; i++) {
			for(int t = 0; t<turnPoints.size(); t++) {
				if(Arrays.equals(snake.get(i), (int[]) turnPoints.get(t).get(0))) {
					directions.set(i, (Direction) turnPoints.get(t).get(1)); 
					if(i==length-1)
						turnPoints.remove(t);
				}
			}
			
			if(i==0 && !canMove())
				return false;
			
			
			switch(directions.get(i)) {
			case UP:
				rects.get(i).setTranslateY(rects.get(i).getTranslateY() - sideLength);
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.EMPTY;
				snake.get(i)[1]--;
				if(gridSpots[snake.get(i)[0]][snake.get(i)[1]]==Spot.FOOD)
					grow();
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.SNAKE;
				
				break;
			case DOWN:
				rects.get(i).setTranslateY(rects.get(i).getTranslateY() + sideLength);
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.EMPTY;
				snake.get(i)[1]++;
				if(gridSpots[snake.get(i)[0]][snake.get(i)[1]]==Spot.FOOD)
					grow();
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.SNAKE;
				break;
			case LEFT:
				rects.get(i).setTranslateX(rects.get(i).getTranslateX() - sideLength);
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.EMPTY;
				snake.get(i)[0]--;
				if(gridSpots[snake.get(i)[0]][snake.get(i)[1]]==Spot.FOOD)
					grow();
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.SNAKE;
				break;
			case RIGHT:
				rects.get(i).setTranslateX(rects.get(i).getTranslateX() + sideLength);
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.EMPTY;
				snake.get(i)[0]++;
				if(gridSpots[snake.get(i)[0]][snake.get(i)[1]]==Spot.FOOD)
					grow();
				gridSpots[snake.get(i)[0]][snake.get(i)[1]] = Spot.SNAKE;
				break;
			}
		}
		return true;
	}
	
	public boolean canMove() {
		int[] point = Direction.getPointInDirection(directions.get(0), snake.get(0));
		if((snake.get(0)[0]==0 && directions.get(0)==Direction.LEFT)
		|| (snake.get(0)[0]==gridSpots.length-1 && directions.get(0)==Direction.RIGHT)
		|| (snake.get(0)[1]==0 && directions.get(0)==Direction.UP)
		|| (snake.get(0)[1]==gridSpots[0].length-1 && directions.get(0)==Direction.DOWN)
		|| gridSpots[point[0]][point[1]]==Spot.SNAKE)
			return false;
		return true;
	}

	public void grow() {
		snake.add(Direction.getPointInDirection(Direction.getOpposite(directions.get(length-1)), snake.get(length-1)));
		directions.add(directions.get(length-1));
		Rectangle r = new Rectangle(sideLength, sideLength, rects.get(length-1).getFill());
		r.setX(snake.get(length)[0]*sideLength);
		r.setY(snake.get(length)[1]*sideLength);
		grid.getChildren().add(r);
		rects.add(r);
		
		length++;
		score++;
		newFood();
	}
	
	public void newFood() {
		int[] foodSpot = {(int) (Math.random()*gridSpots.length), (int) (Math.random()*gridSpots[0].length)};
		while(gridSpots[foodSpot[0]][foodSpot[1]]!=Spot.EMPTY) {
			foodSpot[0] = (int) (Math.random()*gridSpots.length);
			foodSpot[1] = (int) (Math.random()*gridSpots[0].length);
		}
		gridSpots[foodSpot[0]][foodSpot[1]] = Spot.FOOD;
		food.setX(foodSpot[0]*sideLength);
		food.setY(foodSpot[1]*sideLength);
	}
	
	public void turn(Direction dir) {
		if(dir==null || alreadyTurned)
			return;
		
		if(dir!=Direction.getOpposite(this.directions.get(0))) {
			ArrayList<Object> spot = new ArrayList<>();
			spot.add(new int[]{snake.get(0)[0], snake.get(0)[1]});
			spot.add(dir);
			turnPoints.add(spot);
			alreadyTurned = true;
		}
	}
	
	public int getScore() {
		return score;
	}
	
}
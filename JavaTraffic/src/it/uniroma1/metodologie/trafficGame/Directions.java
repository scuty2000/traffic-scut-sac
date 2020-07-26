package it.uniroma1.metodologie.trafficGame;

public enum Directions {
	UP(0,-1,-90),DOWN(0,1,+90),LEFT(-1,0,180),RIGHT(1,0,0);
	
	private int x;
	private int y;
	private int startRotation;
	
	Directions(int x, int y, int sr){
		this.x = x;
		this.y = y;
		this.startRotation = sr;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public boolean isOpposite(Directions d2) {
		return (this.x - d2.x == 0) || (this.y -d2.y == 0);
	}
	
	public int getStartingRotation() { return startRotation; }
	
	public static Directions getDirectionFromRotation(int rotation) {
		
		switch(rotation+"") {
			case "1":
				return Directions.DOWN;
			case "2":
				return Directions.LEFT;
			case "3":
				return Directions.UP;
			case "4":
				return Directions.RIGHT;
		}
		
		return null;
	}
}

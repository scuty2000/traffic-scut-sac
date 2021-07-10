package it.uniroma1.metodologie.trafficGame;

/**
 * this class is used to enumerate directions
 *
 */
public enum Directions {
	UP(0,-1,-90),DOWN(0,1,+90),LEFT(-1,0,180),RIGHT(1,0,0);
	/**
	 * x multiplier used for moving
	 */
	private int x;
	/**
	 * y multiplier used for moving
	 */
	private int y;
	/**
	 * starting rotation used when a car is spawned
	 */
	private int startRotation;
	
	/**
	 * constructor of the class
	 * @param x x multiplier used for moving
	 * @param y y multiplier used for moving
	 * @param sr starting rotation used when a car is spawned
	 */
	Directions(int x, int y, int sr){
		this.x = x;
		this.y = y;
		this.startRotation = sr;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	/**
	 * returns true if the direction passed as parameter is oppoite to this one
	 * @param d2 direction to check
	 * @return true if the direction passed as parameter is oppoite to this one
	 */
	public boolean isOpposite(Directions d2) {
		return (this.x - d2.x == 0) || (this.y -d2.y == 0);
	}
	
	public int getStartingRotation() { return startRotation; }
	
	/**
	 * method that maps the property rotation (used by TrafficLights)  and Direction
	 * @param rotation
	 * @return
	 */
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

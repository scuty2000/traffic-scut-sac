package it.uniroma1.metodologie.trafficGame;

public enum Directions {
	UP(0,-1),DOWN(0,1),LEFT(-1,0),RIGHT(1,0);
	
	private int x;
	private int y;
	
	Directions(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
}

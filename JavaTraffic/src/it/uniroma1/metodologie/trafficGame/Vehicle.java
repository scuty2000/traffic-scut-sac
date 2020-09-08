package it.uniroma1.metodologie.trafficGame;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * this is the enum of all the vehicles in the game. Every veihcle has a booean that tells you if the vehicle can turn,
 * a field called shape that contains the shape of the vehicle and a Direction field. this field should be set when the
 * vehicle is spawned.  
 * @author Andrea
 *
 */


public enum Vehicle {
	CAR(true, 60, 40, Color.STEELBLUE),TIR(false ,110, 50, Color.BLACK),MOTORBIKE(true, 37, 16, Color.DARKCYAN);
	
	private boolean canTurn;
	
	private int width;
	
	private int heigh;
	
	private Color c;
	
	Vehicle(boolean canTurn, int width, int heigh, Color c) {
		this.canTurn = canTurn;
		this.width = width;
		this.heigh = heigh;
		this.c = c;
	}
	
	public Shape getShape() { return new Rectangle(width, heigh, c); }
	
	public int getWidth() { return width; }
	public int getHeigh() { return heigh; }
	
	public boolean canTurn() { return canTurn; }
	
}

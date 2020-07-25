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
	CAR(true, 60, 40),TIR(false,195, 50),MOTORBIKE(true, 35, 14);
	
	private boolean canTurn;
	
	private int width;
	
	private int heigh;
	
	Vehicle(boolean canTurn, int width, int heigh) {
		this.canTurn = canTurn;
		this.width = width;
		this.heigh = heigh;
	}
	
	public Shape getShape() { return new Rectangle(width, heigh, Color.LAWNGREEN); }
	
	public boolean canTurn() { return canTurn; }
	
}

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
	CAR(true, new Rectangle(60,40,Color.YELLOW)),TIR(false, new Rectangle(190,50,Color.YELLOW)),MOTORBIKE(true, new Rectangle(35,14,Color.YELLOW));
	
	private boolean canTurn;
	
	private Shape shape;
	
	Vehicle(boolean canTurn, Shape shape) {
		this.canTurn = canTurn;
		this.shape = shape;
	}
	
	public Shape getShape() { return shape; }
	
	public boolean canTurn() { return canTurn; }
	
}

package it.uniroma1.metodologie.trafficGame;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * This is the Enum of all the vehicles in the game. Every vehicle has a boolean that tells you if the vehicle can turn,
 * a field called shape that contains the shape of the vehicle and a Direction field.
 */

public enum Vehicle {
	CAR(true, 60, 40, Color.STEELBLUE),TIR(false ,110, 50, Color.BLACK),MOTORBIKE(true, 37, 16, Color.DARKCYAN);
	
	/**
	 * This indicates if the vehicle
	 * is able to turn
	 */
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

	/**
	 * This returns the shape of the specific vehicle
	 * @return
	 */
	public Shape getShape() { return new Rectangle(width, heigh, c); }
	
	public int getWidth() { return width; }
	public int getHeigh() { return heigh; }
	
	/**
	 * This returns if the car can move
	 * @return
	 */
	public boolean canTurn() { return canTurn; }
	
}

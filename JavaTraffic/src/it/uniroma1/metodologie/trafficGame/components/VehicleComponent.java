package it.uniroma1.metodologie.trafficGame.components;

import java.util.Random;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;

import it.uniroma1.metodologie.trafficGame.Directions;
import it.uniroma1.metodologie.trafficGame.Vehicle;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * TODO this class will contain the methods that defines the behavior of the vehicles (speed, decisions, etc...)
 * @author Andrea
 *
 */

public class VehicleComponent extends Component{
	/*
	 * speed of the veichle
	 */
	private double speed = 5.0;

	private Vehicle v;

	private boolean turning;

	private LocalTimer shootTimer;

	private double gapBetweenMove = 0.01;
	
	private LocalTimer accSlow;

	Directions d;

	public VehicleComponent(Vehicle v, Directions d) {
		this.v = v;
		this.d = d;
	}

	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		turnRadius = 0;
		accSlow = FXGL.newLocalTimer();
		accSlow.capture();
	}


	/*
	 * onUpdate the car has to be moved toward the direction setted.
	 */
	@Override
	public void onUpdate(double tpf) {

		if(entity.getX() < -100 || entity.getX() > 2600 || entity.getY() < -100 || entity.getY() > 2600) {
			entity.removeFromWorld();
			//System.out.println("Deleted");
		} else if(shootTimer.elapsed(Duration.seconds(gapBetweenMove))) {
			if(turning)
				turnAnimation();
			else 
				entity.translate(new Point2D(speed * d.getX(), speed * d.getY()));
				
			shootTimer.capture();
		}
	}
	
	private final static int SHORT_RADIUS = 60;
	private final static int LONG_RADIUS = 200;
	private final static int DIV = 9;
	private double mul;
	private int rot;
	
	private void turn(Directions d) {
		if(!(d.equals(this.d) || d.isOpposite(this.d))) {  //checks if the new direction is different from the old one
			turnRadius = 0;
			switch(this.d) {
			case UP : 
				if(d.equals(Directions.LEFT)) {
					xMovement = -LONG_RADIUS/DIV - 9;		//sub x, sub y
					yMovement = -LONG_RADIUS/DIV - 25;
					toAddX = 1;
					toAddY = 2.777778;
					mul = 0.5;
					rot = -10;
				}
				else {
					xMovement = SHORT_RADIUS/DIV;		//add x, sub y
					yMovement = -SHORT_RADIUS/DIV - 9;
					toAddX = 0;
					toAddY = 1;
					mul = 1;
					rot = +10;
				}
				break;
			case DOWN : 
				if(d.equals(Directions.LEFT)) {
					xMovement = -SHORT_RADIUS/DIV;
					yMovement = SHORT_RADIUS/DIV + 9;
					toAddX = 0;
					toAddY = -1;
					mul = 1;
					rot = +10;
				}
				else {
					xMovement = LONG_RADIUS/DIV + 9;
					yMovement = LONG_RADIUS/DIV + 25;
					toAddX = -1;
					toAddY = -2.777778;
					mul = 0.5;
					rot = -10;
				}
				break;
			case RIGHT : 
				if(d.equals(Directions.UP)) {
					xMovement = LONG_RADIUS/DIV + 25;
					yMovement = -LONG_RADIUS/DIV + 9;
					toAddX = -2.777778;
					toAddY = -1;
					mul = 0.5;
					rot = -10;
				}
				else {
					xMovement = SHORT_RADIUS/DIV + 9;
					yMovement = SHORT_RADIUS/DIV;
					toAddX = -1;
					toAddY = 0;
					mul = 1;
					rot = +10;
				}
				break;
			case LEFT : 
				if(d.equals(Directions.UP)) {
					xMovement = -SHORT_RADIUS/DIV - 9;
					yMovement = -SHORT_RADIUS/DIV;
					toAddX = 1;
					toAddY = 0;
					mul = 1;
					rot = +10;
				}
				else {
					xMovement = -LONG_RADIUS/DIV - 25;
					yMovement = LONG_RADIUS/DIV - 9;
					toAddX = 2.777778;
					toAddY = 1;
					mul = 0.5;
					rot = -10;
				}
				break;
			};
			this.d = d;
			this.turning = true;
			gapBetweenMove = 0.05;
		}
	}

	private double turnRadius; //the radius of the circumference that the vehicle has to follow in order to turn

	private double xMovement;

	private double yMovement;
	
	private double toAddX = 0;
	
	private double toAddY = 0;

	private void turnAnimation() {

		entity.rotateBy(rot*mul);
		entity.translate(xMovement*mul, yMovement*mul);
		xMovement += toAddX;
		yMovement += toAddY;
		//niceTraslation(entity, new Point2D(xMovement*mul, yMovement*mul));
		
		if(entity.getRotation()%90 == 0) {
			turning = false;
			gapBetweenMove = 0.01;
		}
		
	}
	
	public Directions getDirection() { return this.d; }

	public void setDirection(Directions d) { 
		turn(d); 
	}

	public Vehicle getVehicle() { return v; }
	
	public boolean isTurning() { return turning; }
	
	public void slowDown() {
		if(this.speed > 0 && accSlow.elapsed(Duration.seconds(0.1))) {
			this.speed--;
			accSlow.capture();
		}
	}
	
	public void accelerate() {
		
		if(this.speed < 5 && accSlow.elapsed(Duration.seconds(0.1))) {
			this.speed++;
			accSlow.capture();
		}
	}
	
	public double getSpeed() {
		return this.speed;
	}
}

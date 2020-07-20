package it.uniroma1.metodologie.trafficGame.components;

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

	Directions d;
	
	public VehicleComponent(Vehicle v) {
		this.v = v;
	}

	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		d = Directions.RIGHT;
		turnRadius = 0;
	}


	/*
	 * onUpdate the car has to be moved toward the direction setted.
	 */
	@Override
	public void onUpdate(double tpf) {
		
		if(entity.getX() < 0 || entity.getX() > 2500 || entity.getY() < 0 || entity.getY() > 2500) {
			entity.removeFromWorld();
			System.out.println("Deleted");
		}
		else if(shootTimer.elapsed(Duration.seconds(gapBetweenMove))) {
			if(turning)
				turnAnimation();
			else {
				Point2D velocity = new Point2D(speed * d.getX(), speed * d.getY());
				entity.translate(velocity);
				shootTimer.capture();
	//			System.out.println("moved");
			}
		}
	}

	public void turn(Directions d) {
		if(d.getX() != this.d.getX() && d.getY() != this.d.getY()) {  //checks if the new direction is different from the old one
			turnRadius = 0;
			switch(this.d) {
				case UP : turnRadius = d.equals(Directions.LEFT) ? 187 : 63; break;
				case DOWN : turnRadius = d.equals(Directions.LEFT) ? 63 : 187; break;
				case RIGHT : turnRadius = d.equals(Directions.UP) ? 187 : 63; break;
				case LEFT : turnRadius =d.equals(Directions.UP) ? 63 : 187; break;
			};
			this.d = d;
			this.turning = true;
			gapBetweenMove = 0.05;
		}
	}
	
	private double turnRadius; //the radius of the circumference that the vehicle has to follow in order to turn
	
	private double xTurn = 0;
	private double yTurn = 1;
	
	private void turnAnimation() {
		
		entity.rotateBy(d.getX()*5 + d.getY()*5);
		entity.translate(new Point2D(10, 10));
		shootTimer.capture();
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
}

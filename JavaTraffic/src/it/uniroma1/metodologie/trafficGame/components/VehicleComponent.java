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
	
	private LocalTimer shootTimer;

	Directions d;

	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		d = ((Vehicle)entity.getType()).getDirection();
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
		else if(shootTimer.elapsed(Duration.seconds(0.01))) {
			Point2D velocity = new Point2D(speed * d.getX(), speed * d.getY());
			entity.translate(velocity);
			shootTimer.capture();
			System.out.println("moved");
		}
	}

	public void turn(Directions d) {
		this.d = d; 
	}
}

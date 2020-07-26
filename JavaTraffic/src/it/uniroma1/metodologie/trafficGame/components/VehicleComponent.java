package it.uniroma1.metodologie.trafficGame.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.time.LocalTimer;

import it.uniroma1.metodologie.trafficGame.Directions;
import it.uniroma1.metodologie.trafficGame.Vehicle;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import tutorial.AndreaGameApp.EntityType;

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

	public VehicleComponent(Vehicle v, Directions d) {
		this.v = v;
		this.d = d;
	}

	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		turnRadius = 0;
	}


	/*
	 * onUpdate the car has to be moved toward the direction setted.
	 */
	@Override
	public void onUpdate(double tpf) {
		
		//TODO FIX THIS
		
//		ArrayList<Entity> semaforiVicini = (ArrayList<Entity>) FXGL.getGameWorld().getEntitiesByType(EntityType.SEMAFORO).stream().filter(x -> x.getPosition().distance(entity.getPosition()) < 200).collect(Collectors.toList());
//		ArrayList<Entity> incrociVicini = (ArrayList<Entity>) FXGL.getGameWorld().getEntitiesByType(EntityType.INCROCIO).stream().filter(x -> x.getPosition().distance(entity.getPosition()) < 600).collect(Collectors.toList());
//
//		if(semaforiVicini.size() > 0)
//			System.out.println(semaforiVicini.size()+" "+incrociVicini.size()+" "+d+" "+semaforiVicini.get(0).getPropertyOptional("direction").orElse(null));		
//		
//		if(semaforiVicini.size() == 1 && semaforiVicini.get(0).getComponent(TrafficLightAnimationComponent.class).isRed() && semaforiVicini.get(0).getPropertyOptional("direction").orElse(Directions.UP).equals(d)) {
//				speed = 0;
//		} else {
//			speed = 5.0;
//		}

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
	
	
	private void niceTraslation(Entity vehicle, Point2D destinationPoint) {
		
		ArrayList<Integer> xTranslations = new ArrayList<>();
		ArrayList<Integer> yTranslations = new ArrayList<>();
		
		for(int i = (int) vehicle.getX(); i != (int) destinationPoint.getX()+vehicle.getX();) {
			if(i < (int) destinationPoint.getX()+vehicle.getX()) {
				xTranslations.add(i++);
			} else if ((int) i > (int) destinationPoint.getX()+vehicle.getX()) {
				xTranslations.add(i--);
			}
			System.out.println("neither "+i+":"+(int) destinationPoint.getX()+vehicle.getX());
		}
		
		for(int i = (int) vehicle.getY(); (int) i != (int) destinationPoint.getY()+vehicle.getY();) {
			if(i< (int) destinationPoint.getY()+vehicle.getY()) {
				yTranslations.add(i);
				i = i + 1;
			} else if(i> (int) destinationPoint.getY()+vehicle.getY()) {
				yTranslations.add(i);
				i = i - 1;
			}
		}
		
		System.out.println("xCoords: "+xTranslations.toString()+" yCoords: "+yTranslations.toString()+" destX: "+ (int) destinationPoint.getX()+vehicle.getX()+" destY: "+ (int) destinationPoint.getY()+vehicle.getY());
		
		while (entity.getX() != destinationPoint.getX() && !xTranslations.isEmpty() && !yTranslations.isEmpty()) {
			entity.translate(new Point2D(xTranslations.get(0), yTranslations.get(0)));
			xTranslations.remove(0);
			yTranslations.remove(0);
		}
		System.out.println("should be translated");
	}

	public Directions getDirection() { return this.d; }

	public void setDirection(Directions d) { 
		turn(d); 
	}

	public Vehicle getVehicle() { return v; }
	
	/* 
	 * y = sqrt(r^2 - x^2)      semicircle's formula
	 */
	private double getPointOfRotation(double x) {
		return Math.sqrt(Math.pow(turnRadius,2) - Math.pow(x, 2));
	}
}

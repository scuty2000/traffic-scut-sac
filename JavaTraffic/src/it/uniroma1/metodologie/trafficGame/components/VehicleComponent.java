package it.uniroma1.metodologie.trafficGame.components;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;

import it.uniroma1.metodologie.trafficGame.Directions;
import it.uniroma1.metodologie.trafficGame.Vehicle;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;
import tutorial.AndreaGameApp.EntityType;

public class VehicleComponent extends Component{
	/*
	 * speed of the veichle
	 */
	private double speed = 5.0;

	private Vehicle v;

	private boolean turning;

	private Entity currentPath;

	private LocalTimer shootTimer;

	private double gapBetweenMove = 0.01;

	private LocalTimer accSlow;

	private Directions d;

	private List<Entity> pathList;
	
	private Point2D startPoint;
	
	private Point2D endPoint;
	
	private Point2D rotationCenter;
	
	private double radius;
	
	private double xIncrement;

	public VehicleComponent(Vehicle v, Directions d, List<Entity> pathList) {
		this.v = v;
		this.d = d;
		this.pathList = pathList;
		nextPath();
	}

	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		accSlow = FXGL.newLocalTimer();
		accSlow.capture();
		//System.out.println(currentPath);
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
			else {
				entity.translate(speed * d.getX(), speed * d.getY());
				if((d.equals(Directions.LEFT) || d.equals(Directions.RIGHT)) && Math.abs(entity.getCenter().getY() - currentPath.getY()) > 1) {
					entity.translateY(entity.getCenter().getY() - currentPath.getY() > 0 ?-0.2: 0.2);
					//System.out.println("Y changed to " + entity.getPosition().getY());
				}
				else if((d.equals(Directions.UP) || d.equals(Directions.DOWN)) && Math.abs(entity.getCenter().getX() - currentPath.getX()) > 1) {
					entity.translateX(entity.getCenter().getX() - currentPath.getX() > 0 ? -0.2 : 0.2);
					//System.out.println("X changed to " + entity.getPosition().getX() + "        -     current path x = " + currentPath.getX());
				}

			}
			//FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(entity.getX(), entity.getY(), 40, 40)).stream().filter(x -> x.getType().equals(EntityType.PATH))

			shootTimer.capture();
		}
	}

	private final static int SHORT_RADIUS = 50;
	private final static int LONG_RADIUS = 200;
	private final static int DIV = 9;
	private double mul;
	private int rot;

	private void turn(Directions d) {
		if(!(d.equals(this.d) || d.isOpposite(this.d))) {  //checks if the new direction is different from the old one
			this.startPoint = new Point2D(entity.getX(), entity.getY());
//			System.out.println(pathList.size());
//			this.endPoint = new Point2D(pathList.get(0).getX(), pathList.get(0).getY());
			Entity nearestIncrocio = FXGL.getGameWorld().getEntitiesByType(EntityType.SEMAFORO).stream().sorted((x, y) -> (int)(x.distance(entity)-y.distance(entity))).collect(Collectors.toList()).get(0);
			switch(this.d) {
			case UP : 
				if(d.equals(Directions.LEFT)) {
					
					this.xIncrement = -10;
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					
//					xMovement = -LONG_RADIUS/DIV - 9;		//sub x, sub y
//					yMovement = -LONG_RADIUS/DIV - 25;
//					toAddX = 1;
//					toAddY = 2.777778;
					mul = 0.5;
					rot = -10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = 10;
					
//					xMovement = SHORT_RADIUS/DIV;		//add x, sub y
//					yMovement = -SHORT_RADIUS/DIV - 9;
//					toAddX = 0;
//					toAddY = 1;
					mul = 1;
					rot = +10;
				}
				break;
			case DOWN : 
				if(d.equals(Directions.LEFT)) {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY());
					this.xIncrement = -10;
					
//					xMovement = -SHORT_RADIUS/DIV;
//					yMovement = SHORT_RADIUS/DIV + 9;
//					toAddX = 0;
//					toAddY = -1;
					mul = 1;
					rot = +10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY());
					this.xIncrement = 10;
					
//					xMovement = LONG_RADIUS/DIV + 9;
//					yMovement = LONG_RADIUS/DIV + 25;
//					toAddX = -1;
//					toAddY = -2.777778;
					mul = 0.5;
					rot = -10;
				}
				break;
			case RIGHT : 
				if(d.equals(Directions.UP)) {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY());
					this.xIncrement = 10;
					
//					xMovement = LONG_RADIUS/DIV + 25;
//					yMovement = -LONG_RADIUS/DIV + 9;
//					toAddX = -2.777778;
//					toAddY = -1;
					mul = 0.5;
					rot = -10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = 10;
					
//					xMovement = SHORT_RADIUS/DIV + 9;
//					yMovement = SHORT_RADIUS/DIV;
//					toAddX = -1;
//					toAddY = 0;
					mul = 1;
					rot = +10;
				}
				break;
			case LEFT : 
				if(d.equals(Directions.UP)) {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY());
					this.xIncrement = -10;
					
//					xMovement = -SHORT_RADIUS/DIV - 9;
//					yMovement = -SHORT_RADIUS/DIV;
//					toAddX = 1;
//					toAddY = 0;
					mul = 1;
					rot = +10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = -10;
					
//					xMovement = -LONG_RADIUS/DIV - 25;
//					yMovement = LONG_RADIUS/DIV - 9;
//					toAddX = 2.777778;
//					toAddY = 1;
					mul = 0.5;
					rot = -10;
				}
				break;
			};
			this.radius = this.rotationCenter.distance(entity.getPosition());
			this.d = d;
			this.turning = true;
			gapBetweenMove = 0.05;
		}
	}

	private double xMovement;

	private double yMovement;

	private double toAddX = 0;

	private double toAddY = 0;

	private void turnAnimation() {

		entity.rotateBy(rot*mul);
//		entity.translate(xMovement*mul, yMovement*mul);
		entity.translate(generateNextPoint());
		xMovement += toAddX;
		yMovement += toAddY;
		//niceTraslation(entity, new Point2D(xMovement*mul, yMovement*mul));

		if(entity.getRotation()%90 == 0) {
			turning = false;
			gapBetweenMove = 0.01;
		}
		
//		Point2D startPoint = null;
//		Point2D controlPoint = null;
//		Point2D endPoint = null;
//		QuadCurve qC = null;
//		
//		Entity nextPath = pathList.get(0);
//		
//		if(currentPath != null) {
//			if(currentPath.getPropertyOptional("direzione").orElse("RIGHT").equals("UP") || currentPath.getPropertyOptional("direzione").orElse("RIGHT").equals("DOWN")) {
//				startPoint = new Point2D(currentPath.getX(), currentPath.getBottomY());
//				if(currentPath.getProperties().getString("direzione").equals("UP"))
//					startPoint = new Point2D(startPoint.getX(), startPoint.getY()+currentPath.getHeight());
//				controlPoint = new Point2D(currentPath.getX(), nextPath.getY());
//				endPoint = new Point2D(nextPath.getX(), nextPath.getY());
//			}
//		}
		
	}
	
	private Point2D generateNextPoint() {
		double relativeX = entity.getX() + this.xIncrement - this.rotationCenter.getX();
		System.out.println(relativeX);
		double relativeY = Math.sqrt(Math.pow(this.radius, 2) - Math.pow(relativeX,2));
		
		System.out.println(relativeX+" "+relativeY+" "+radius);                                  
		
		return new Point2D(relativeX, relativeY);
	}

	public Directions getDirection() { return this.d; }

	public void nextPath() {
		currentPath = pathList.remove(0);
		turn(Directions.valueOf((String) currentPath.getPropertyOptional("direzione").orElse("DOWN")));
	}

	public Vehicle getVehicle() { return v; }

	public boolean isTurning() { return turning; }

	public void slowDown() {
		this.speed = 0;
		//		if(this.speed > 0 && accSlow.elapsed(Duration.seconds(0.1))) {
		//			this.speed--;
		//			accSlow.capture();
		//		}
	}

	public void accelerate() {
		this.speed = 5.0;
		//		if(this.speed < 5 && accSlow.elapsed(Duration.seconds(0.1))) {
		//			this.speed++;
		//			accSlow.capture();
		//		}
	}

	public double getSpeed() {
		return this.speed;
	}
}

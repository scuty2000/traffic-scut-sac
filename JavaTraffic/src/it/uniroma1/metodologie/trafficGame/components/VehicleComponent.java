package it.uniroma1.metodologie.trafficGame.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.time.LocalTimer;
import com.sun.source.doctree.EntityTree;

import it.uniroma1.metodologie.trafficGame.Directions;
import it.uniroma1.metodologie.trafficGame.Vehicle;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import tutorial.AndreaGameApp.EntityType;

public class VehicleComponent extends Component{
	/*
	 * speed of the veichle
	 */
	private double speed = 2.0;

	private Vehicle v;

	private boolean turning;

	private Entity currentPath;

	private LocalTimer shootTimer;

	private double gapBetweenMove = 0.01;

	private LocalTimer accSlow;

	private Directions d;
	
	private Directions oldDirection;

	private List<Entity> pathList;
	
	private Point2D startPoint;
	
	private Point2D endPoint;
	
	private Point2D rotationCenter;
	
	private double radius;
	
	private double xIncrement;
	
	ArrayList<Point2D> arrayPunti = new ArrayList<>();

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
		
		createPoints();
		
	}

	private void createPoints() {
		
		for (int i = 0; i < 70; ++i) {
			
		    final double angle = Math.toRadians(((double) i / 70) * 360d);

		    if(Math.cos(angle) * 16 < 0)
		    	break;
		    
	    	arrayPunti.add(new Point2D(
			        Math.cos(angle) * 16+entity.getHeight()/2, 
			        Math.sin(angle) * 16+entity.getWidth()/2
			    ));
		    	
		}
		
		System.out.println(arrayPunti.size());
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
					moveForward();
					//System.out.println("X changed to " + entity.getPosition().getX() + "        -     current path x = " + currentPath.getX());
				}

			}
			//FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(entity.getX(), entity.getY(), 40, 40)).stream().filter(x -> x.getType().equals(EntityType.PATH))

			shootTimer.capture();
		}
	}
	
	public void moveForward() {
		entity.translateX(entity.getCenter().getX() - currentPath.getX() > 0 ? -0.2 : 0.2);
	}

	private final static int SHORT_RADIUS = 50;
	private final static int LONG_RADIUS = 200;
	private final static int DIV = 9;
	private double mul;
	private int rot;
	
	public Entity getNearestSemaforo() {
		return getNearestByClass(EntityType.SEMAFORO);
	}
	
	public Entity getNearestIncrocio() {
		return getNearestByClass(EntityType.INCROCIO);
	}
	
	private Entity getNearestByClass(EntityType t) {
		return FXGL.getGameWorld().getEntitiesByType(t).stream().sorted((x, y) -> (int)(x.distance(entity)-y.distance(entity))).collect(Collectors.toList()).get(0);
	}

	private void turn(Directions d) {
		if(!(d.equals(this.d) || d.isOpposite(this.d))) {  //checks if the new direction is different from the old one
			this.startPoint = new Point2D(entity.getX(), entity.getY());
//			System.out.println(pathList.size());
			//this.endPoint = new Point2D(pathList.get(0).getX(), pathList.get(0).getY());
			Entity nearestIncrocio = getNearestSemaforo();
			switch(this.d) {
			case UP : 
				if(d.equals(Directions.LEFT)) {
					
					this.xIncrement = -10;
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					
					xMovement = -LONG_RADIUS/DIV - 9;		//sub x, sub y
					yMovement = -LONG_RADIUS/DIV - 25;
					toAddX = 1;
					toAddY = 2.777778;
					mul = 0.5;
					rot = -10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = 10;
					
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
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY());
					this.xIncrement = -10;
					
					xMovement = -SHORT_RADIUS/DIV;
					yMovement = SHORT_RADIUS/DIV + 9;
					toAddX = 0;
					toAddY = -1;
					mul = 1;
					rot = +10;
				} else {
//					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY());
					this.xIncrement = 10;
					
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
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY());
					this.xIncrement = 10;
					
					xMovement = LONG_RADIUS/DIV + 25;
					yMovement = -LONG_RADIUS/DIV + 9;
					toAddX = -2.777778;
					toAddY = -1;
					mul = 0.5;
					rot = -10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = 10;
					
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
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY());
					this.xIncrement = -10;
					
					xMovement = -SHORT_RADIUS/DIV - 9;
					yMovement = -SHORT_RADIUS/DIV;
					toAddX = 1;
					toAddY = 0;
					mul = 1;
					rot = +10;
				} else {
					
					this.rotationCenter = new Point2D(nearestIncrocio.getX()+nearestIncrocio.getWidth(), nearestIncrocio.getY()-nearestIncrocio.getHeight());
					this.xIncrement = -10;
					
					xMovement = -LONG_RADIUS/DIV - 25;
					yMovement = LONG_RADIUS/DIV - 9;
					toAddX = 2.777778;
					toAddY = 1;
					mul = 0.5;
					rot = -10;
				}
				break;
			};
			this.radius = this.rotationCenter.distance(entity.getPosition());
			this.oldDirection = this.d;
			this.d = d;
			this.turning = true;
			gapBetweenMove = 0.1;
		}
	}

	private double xMovement;

	private double yMovement;

	private double toAddX = 0;

	private double toAddY = 0;

	private void turnAnimation() {

		entity.rotateBy(rot*mul);
		
//		FXGL.entityBuilder()
//		.at(entity.getCenter())
//		.view(new Rectangle(10, 10, Color.GREEN))
//		.buildAndAttach();
		
		if(this.oldDirection.equals(Directions.RIGHT) && this.d.equals(Directions.DOWN)){
			if(entity.getRotation()%10 == 0 && !arrayPunti.isEmpty()) {
				entity.translate(arrayPunti.remove(0));
			} else {
				entity.translate(new Point2D(0, 0));
			}
		} else {
			entity.translate(xMovement*mul, yMovement*mul);
		}

//		System.out.println("I'm was pointing "+this.oldDirection.toString()+" and now I do point "+this.d.toString());
		xMovement += toAddX;
		yMovement += toAddY;

		if(entity.getRotation()%90 == 0) {
			turning = false;
			gapBetweenMove = 0.01;
		}
		
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
		this.speed = 2.0;
		//		if(this.speed < 5 && accSlow.elapsed(Duration.seconds(0.1))) {
		//			this.speed++;
		//			accSlow.capture();
		//		}
	}

	public double getSpeed() {
		return this.speed;
	}
}

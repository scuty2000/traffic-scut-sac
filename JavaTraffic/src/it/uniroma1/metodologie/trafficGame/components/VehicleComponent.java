package it.uniroma1.metodologie.trafficGame.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;

import it.uniroma1.metodologie.trafficGame.Directions;
import it.uniroma1.metodologie.trafficGame.EntityType;
import it.uniroma1.metodologie.trafficGame.TrafficApp;
import it.uniroma1.metodologie.trafficGame.Vehicle;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class VehicleComponent extends Component{
	
	/*
	 * DEBUG
	 */
	
	private boolean debugCurve = false;
	
	/*
	 * speed of the veichle
	 */
	private double speed = 2.4;
	
	private double acceleration = speed/6;
	
	private final double MAX_SPEED = speed;

	private Vehicle v;

	private boolean turning;
	
	private boolean accelerating = true;

	private Entity currentPath;

	private LocalTimer shootTimer;

	private double gapBetweenMove = 0.01;

	private LocalTimer accSlow;

	private Directions d;
	
	private Directions oldDirection;

	private List<Entity> pathList;
	
	private static ArrayList<ArrayList<Point2D>> arrayCurveBCK = new ArrayList<>();
	
	private ArrayList<ArrayList<Point2D>> arrayCurve = new ArrayList<>();

	public VehicleComponent(Vehicle v, Directions d, List<Entity> pathList) {
		this.v = v;
		this.d = d;
		this.pathList = pathList;
		nextPath();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAdded() {
		shootTimer = FXGL.newLocalTimer();
		shootTimer.capture();
		accSlow = FXGL.newLocalTimer();
		accSlow.capture();
		turnTimer = FXGL.newLocalTimer();
		TURN_GAP = gapBetweenMove * 2;
		
		for (ArrayList<Point2D> arrayList : arrayCurveBCK) {
			this.arrayCurve.add((ArrayList<Point2D>) arrayList.clone());
		}
	}
	
	private static final double COST = 4.5;
	
	public static void creaCurve() {
	    for(int j = 0; j < 8; j++) {
	    	arrayCurveBCK.add(new ArrayList<Point2D>());
	    }
		
		for (int i = 0; i < 356; ++i) {
			
		    final double angle = Math.toRadians(((double) i / 356) * 360d);

		    if(Math.toDegrees(angle)<90) {//0, 4
		    	calculatePoint(angle, 0, 4, i);
		    } else if(Math.toDegrees(angle)<180) {//1,5
	    		calculatePoint(angle, 1, 5, i);
		    } else if(Math.toDegrees(angle)<270) {//2,6
		    	calculatePoint(angle, 2, 6, i);
		    } else if(Math.toDegrees(angle)<360) {
		    	calculatePoint(angle, 3, 7, i);
		    }
		    		    	
		}
		
		arrayCurveBCK.get(0).add(new Point2D(0, 0));
		arrayCurveBCK.get(1).add(new Point2D(0, 0));
		arrayCurveBCK.get(2).add(new Point2D(0, 0));
		arrayCurveBCK.get(3).add(new Point2D(0, 0));
		arrayCurveBCK.get(5).add(new Point2D(0, 0));
		arrayCurveBCK.get(7).add(new Point2D(0, 0));
		
	}
	
	private static void calculatePoint(double angle, int number1, int number2, int i) {
		arrayCurveBCK.get(number1).add(new Point2D(
		        Math.cos(angle) * COST, 
		        Math.sin(angle) * COST
		    ));
	    	if(i%2==0)
	    		arrayCurveBCK.get(number2).add(new Point2D(
			        Math.cos(angle) * COST,
			        Math.sin(angle) * COST
			    ));
	}

	@Override
	public void onUpdate(double tpf) {
		if(debugCurve)
			FXGL.entityBuilder().at(entity.getAnchoredPosition()).view(new Rectangle(5,5,Color.GREEN)).buildAndAttach();

		if(entity.getX() < -500 || entity.getX() > FXGL.getAppWidth() + 500 || entity.getY() < -500 || entity.getY() > FXGL.getAppHeight() + 500) {
			currentPath.getComponent(PathComponent.class).removeCar(entity);
			entity.removeFromWorld();
		} else if(shootTimer.elapsed(Duration.seconds(gapBetweenMove)) && !turning) {
				moveForward();
			//FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(entity.getX(), entity.getY(), 40, 40)).stream().filter(x -> x.getType().equals(EntityType.PATH))
				
				shootTimer.capture();
		}
		else if(turning) {
			blinkArrow();
			turnAnimation();
		}
		if(accelerating)
			accelerate();
		else
			slowDown();
	}

	public void moveForward() {
		entity.translate(speed * d.getX(), speed * d.getY());
		if((d.equals(Directions.LEFT) || d.equals(Directions.RIGHT)) && Math.abs(entity.getAnchoredPosition().getY() - currentPath.getY()) > 0.5) {
			entity.translateY(entity.getAnchoredPosition().getY() - currentPath.getY() > 0 ? -0.2: 0.2);
			//System.out.println("Y changed to " + entity.getPosition().getY());
		}
		else if((d.equals(Directions.UP) || d.equals(Directions.DOWN)) && Math.abs(entity.getAnchoredPosition().getX() - currentPath.getX()) > 0.5) {
			entity.translateX(entity.getAnchoredPosition().getX() - currentPath.getX() > 0 ? -0.2 : 0.2);
			//System.out.println("X changed to " + entity.getPosition().getX() + "        -     current path x = " + currentPath.getX());
		}
	}
	
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
		if(!d.equals(this.d)) {
			switch(this.d) {
			case UP : 
				if(d.equals(Directions.LEFT)) {
					mul = 0.5;
					rot = -2;
				} else {
					mul = 1;
					rot = +2;
				}
				break;
			case DOWN : 
				if(d.equals(Directions.LEFT)) {
					mul = 1;
					rot = +2;
				} else {
					mul = 0.5;
					rot = -2;
				}
				break;
			case RIGHT : 
				if(d.equals(Directions.UP)) {
					mul = 0.5;
					rot = -2;
				} else {
					mul = 1;
					rot = +2;
				}
				break;
			case LEFT : 
				if(d.equals(Directions.UP)) {
					mul = 1;
					rot = +2;
				} else {
					mul = 0.5;
					rot = -2;
				}
				break;
			};
			this.oldDirection = this.d;
			this.d = d;
			this.turning = true;			
		}
	}
	
	private double TURN_GAP;
	private LocalTimer turnTimer;
	
	@SuppressWarnings("unchecked")
	private void turnAnimation() {
		if(turnTimer.elapsed(Duration.seconds(TURN_GAP))){
			entity.rotateBy(rot*mul);

			if(this.oldDirection.equals(Directions.RIGHT) && this.d.equals(Directions.DOWN)){
				entity.translate(arrayCurve.get(4).remove(0));
			} else if(this.oldDirection.equals(Directions.RIGHT) && this.d.equals(Directions.UP)) {
				entity.translate(arrayCurve.get(3).remove(arrayCurve.get(3).size()-1));
			} else if(this.oldDirection.equals(Directions.UP) && this.d.equals(Directions.RIGHT)) {
				entity.translate(arrayCurve.get(7).remove(0));
			} else if(this.oldDirection.equals(Directions.UP) && this.d.equals(Directions.LEFT)) {
				entity.translate(arrayCurve.get(2).remove(arrayCurve.get(2).size()-1));
			} else if(this.oldDirection.equals(Directions.LEFT) && this.d.equals(Directions.UP)) {
				entity.translate(arrayCurve.get(6).remove(0));
			} else if(this.oldDirection.equals(Directions.LEFT) && this.d.equals(Directions.DOWN)) {
				entity.translate(arrayCurve.get(1).remove(arrayCurve.get(1).size()-1));
			} else if(this.oldDirection.equals(Directions.DOWN) && this.d.equals(Directions.RIGHT)) {
				entity.translate(arrayCurve.get(0).remove(arrayCurve.get(0).size()-1));
			} else if(this.oldDirection.equals(Directions.DOWN) && this.d.equals(Directions.LEFT)) { // TODO tune this
				entity.translate(arrayCurve.get(5).remove(0));
			}
			
			if(debugCurve)
				FXGL.entityBuilder()
				.at(entity.getPosition())
				.view(new Rectangle(5,5, Color.BLUE))
				.buildAndAttach();

			if(entity.getRotation()%90 == 0) {
				turning = false;
				accelerate();
				this.arrayCurve.clear();
				for (ArrayList<Point2D> arrayList : arrayCurveBCK) {
					this.arrayCurve.add((ArrayList<Point2D>) arrayList.clone());
				}

				this.currentArrow.setVisible(false);
			}
			turnTimer.capture();
		}
	}

	public Directions getDirection() { return this.d; }

	public void nextPath() {
		Entity oldPath = currentPath;
		currentPath = pathList.remove(0);
		turn(Directions.valueOf((String) currentPath.getPropertyOptional("direzione").orElse("DOWN")));
		if(oldPath != null)
			setArrow(oldPath, currentPath);
	}
	
	private void setArrow(Entity op, Entity np) {
		Directions npd = Directions.valueOf((String) np.getPropertyOptional("direzione").orElseThrow());
		switch(Directions.valueOf((String) op.getPropertyOptional("direzione").orElseThrow())) {
		case UP:
			currentArrow = npd == Directions.RIGHT ? ra : la;
			break;
		case DOWN:
			currentArrow = npd == Directions.RIGHT ? la : ra;
			break;
		case LEFT:
			currentArrow = npd == Directions.UP ? ra : la;
			break;
		case RIGHT:
			currentArrow = npd == Directions.UP ? la : ra;
			break;
		}
	}

	public Vehicle getVehicle() { return v; }

	public boolean isTurning() { return turning; }

	public void slowDown() {
		//this.speed = 0;
		this.accelerating = false;
		if(this.speed >= 0.1 && accSlow.elapsed(Duration.seconds(0.08))) {
			this.speed -= acceleration;
			accSlow.capture();
		}
		speed = speed < 0 ? 0 : speed;
	}

	public void accelerate() {
		//this.speed = 2.0;
		this.accelerating = true;
		if(this.speed < MAX_SPEED && accSlow.elapsed(Duration.seconds(0.08))) {
			this.speed += acceleration;
			accSlow.capture();
		}
	}

	public double getSpeed() {
		return this.speed;
	}
	
	private Rectangle ra;
	private Rectangle la;
	private final double BLINK_TIME = 0.3;
	private Rectangle currentArrow;
	private LocalTimer blinkTimer = FXGL.newLocalTimer();
	
	public void addArrows(Rectangle la, Rectangle ra) {
		this.ra = ra;
		this.la = la;
	}
	
	private void blinkArrow() {
		if(isTurning() && blinkTimer.elapsed(Duration.seconds(BLINK_TIME))) {
			currentArrow.setVisible(currentArrow.isVisible() == true ? false : true);
			blinkTimer.capture();
		}
	}
	
	public void updateTrafficLights() {
		Entity i = FXGL.getGameWorld().getCollidingEntities(entity).parallelStream().filter(x -> x.getType().equals(EntityType.SEMAFORO)).findFirst().orElseThrow();
		Entity nextPath = getNextPath();
		if(i.getComponentOptional(TrafficLightAnimationComponent.class).orElseThrow().isGreen()
				|| entity.isColliding(getNearestIncrocio())) {
			if(!accelerating) {
				if(nextPath != null 
						&& nextPath.getComponent(PathComponent.class).isFree(entity)) {
					nextPath.getComponent(PathComponent.class).addCar(entity);
					accelerate();
				}
				else if(nextPath == null)
					accelerate();
			}
		}
		else
			slowDown();
	}

	public Entity getNextPath() {
		return pathList.size() > 0 ? pathList.get(0) : null;
	}

	public Entity getCurrentPath() {return currentPath; }
	
	public List<Entity> getPathList() { return pathList; }
	
	/*
	 * this method has to be used when the vehicle can not turn because it is a tir or because the street is not free
	 */
	
	public void generateNewStraightPath() {
		getNextPath().getComponent(PathComponent.class).removeCar(entity);
		List<Entity> paths = TrafficApp.getPathDirections(currentPath);
		paths.sort(Comparator.comparing(x -> Directions.valueOf((String) ((Entity)x).getPropertyOptional("direzione").orElseThrow()).equals(d) ? 0 : 1));
		Entity root = paths.get(0);
		pathList = TrafficApp.pathChooser(root, v.canTurn());
		System.out.println();
		System.out.println();
	}
}

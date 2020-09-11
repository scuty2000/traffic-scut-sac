package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * This is the animation used to make TrafficLights
 * go from green to red on the user input.
 */
public class TrafficLightAnimationComponent extends Component {

	/**
	 * This field indicates that the TrafficLight is red
	 */
	private boolean isRed = true;
	/**
	 * This field indicates that the TrafficLight
	 * needs to be switched
	 */
	private boolean toSwitch = false;	
	/*
	 * this field is false if a car is turning left on the other side of the cross. It becomes true when the car has finished turning
	 */
	private boolean canPass = true;
	
	/**
	 * This is the animated texture used
	 * for the animation
	 */
	private AnimatedTexture texture;
	/**
	 * This is the animation channel used
	 * to reproduce the animated texture.
	 */
	private AnimationChannel animRed, animGreen;
	
	/**
	 * This is the crossRoad associated to the
	 * TrafficLight
	 */
	private Entity crossRoad;

	/**
	 * This constructor assings the right texture to 
	 * the right TrafficLight given its position
	 * and rotation.
	 * @param tileID
	 */
	public TrafficLightAnimationComponent(int tileID) {
		animRed = new AnimationChannel(FXGL.image("semaforoRosso-0"+tileID+".png"), 1, 250, 250, Duration.seconds(1), 0, 0);
		animGreen = new AnimationChannel(FXGL.image("semaforoVerde-0"+tileID+".png"), 1, 250, 250, Duration.seconds(1), 0, 0);
		texture = new AnimatedTexture(animRed);
	}
	
	/**
	 * This adds the animation to the view
	 * component of the entity.
	 */
	@Override
	public void onAdded() {
		entity.getTransformComponent().setScaleOrigin(new Point2D(16,21));
		entity.getViewComponent().addChild(texture);
	}
	
	/**
	 * This updated the TrafficLight status.
	 */
	@Override
	public void onUpdate(double tpf) {
		if(isRed && toSwitch) {
			texture.loopAnimationChannel(animGreen);
			isCrossRoadFree = false;
			isRed = false;
			
		} else if (!isRed && toSwitch) {
			texture.loopAnimationChannel(animRed);
			isRed = true;
			
		}
		if(isGreen()) 
			updateAll();
		toSwitch = false;
		
	}
	
	/**
	 * Returns if the TrafficLight is red.
	 * @return
	 */
	public boolean isRed() {
		return isRed;
	}
	
	/*
	 * returns true if the light is green and no car is turning left on the other side of the cross
	 */
	public boolean isGreen() {
		if(!isCrossRoadFree)
			isCrossRoadFree = isCrossRoadFree();
		return !isRed && canPass && isCrossRoadFree;
	}

	public void waitACar() {
		canPass = false;
	}
	
	public void canPass() {
		canPass = true;
	}
	
	private boolean isCrossRoadFree = true;
	
	public void switchLight() {
		toSwitch = true;
	}
	
	/**
	 * Returns if the crossroad is free
	 * @return
	 */
	public boolean isCrossRoadFree() { return crossRoad.getComponent(CrossRoadComponent.class).isFree(); }
	
	/**
	 * Sets the crossroad
	 * @param cr
	 */
	public void setCrossRoad(Entity cr) { this.crossRoad = cr; }
	
	/**
	 * This part is used to communicate with cars.
	 */
	
	private List<Entity> cars = new LinkedList<>();
	
	public void registerCar(Entity car) { cars.add(car); }
	
	private void updateAll() { cars.forEach(c -> c.getComponent(VehicleComponent.class).updateTrafficLights()); }
	
	public void removeCar(Entity c) { cars.remove(c); }
}

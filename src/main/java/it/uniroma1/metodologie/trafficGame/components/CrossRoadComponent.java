package it.uniroma1.metodologie.trafficGame.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * This class represent the crossroad
 */ 
public class CrossRoadComponent extends Component{
	
	/**
	 * Indicates how much cars are on the
	 * crossroad
	 */
	private int carsOnCrossroad;
	
	/**
	 * Adds a car to the counter
	 */
	public void addCar() { carsOnCrossroad ++; }
	/**
	 * Removed a car from the counter
	 */
	public void subCar() { carsOnCrossroad --; }
	/**
	 * returns if the crossroad is free
	 * @return
	 */
	public boolean isFree() { return carsOnCrossroad == 0; }
}

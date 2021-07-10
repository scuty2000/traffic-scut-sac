package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * This represents the path object on the map
 */
public class PathComponent extends Component {
	
	/**
	 * This is the number of cars
	 * that can be contained in the path
	 */
	private int size;
	/**
	 * This represents the number of cars
	 * that are on the path
	 */
	private int current;
	/**
	 * This is the car list that contains
	 * the cars that are transiting on the
	 * path
	 */
	private List<Entity> carList = new LinkedList<>();
	/**
	 * Those are the pixels we have to account for the
	 * car to stop and not crash
	 */
	private int sF = 20;
	/**
	 * Show if a path is on the border
	 * of the map
	 */
	private boolean isOnBorder;
	
	/**
	 * When the path is added to the
	 * content root, this gets its
	 * position and checks if is
	 * on the border of the map and
	 * it's properties
	 */
	@Override
	public void onAdded() { 
		Point2D p = entity.getPosition();
		double x = p.getX();
		double y = p.getY();
		if(x == 0 || x + entity.getWidth() >= FXGL.getAppWidth()-250 || y == 0 || y + entity.getHeight() >= FXGL.getAppHeight()-250)
			isOnBorder = true;
		size = (int) Math.max(entity.getWidth(),entity.getHeight()) - 80;
		FXGL.newLocalTimer();
	}
	
	/**
	 * This adds a car to the path
	 * @param e
	 */
	public void addCar(Entity e) {
		if(!carList.contains(e)) {
			carList.add(e);
			current += calcWidth(e);
		}
	}

	/**
	 * This removes a car to the path
	 * @param e
	 */
	public void removeCar(Entity e) {
		if(carList.contains(e)) {
			current -= calcWidth(e);
			carList.remove(e);
		}
	}

	/**
	 * This returns if the path can get
	 * on itself other cars
	 * @param e
	 * @return
	 */
	public boolean isFree(Entity e) { 
		return size > current + calcWidth(e) || isOnBorder; }
	
	/**
	 * This calculates the real width of the path,
	 * keeping in mind the pixel necessary to the car
	 * to stop safely
	 * @param e
	 * @return
	 */
	private int calcWidth(Entity e) { return (int) (e.getWidth() - sF); }

}

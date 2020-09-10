package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;

import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PathComponent extends Component{

	private int size;
	private int current;
	private List<Entity> carList = new LinkedList<>();
	private int sF = 35; //devo togliere dalla width ella macchina lo spazio di frenata perché se venisse aggiunto a current il path si saturerebbe troppo presto rispetto alla sua attuale capacità
	private boolean isOnBorder;
	private LocalTimer updateTimer;
	
	@Override
	public void onAdded() { 
		Point2D p = entity.getPosition();
		double x = p.getX();
		double y = p.getY();
		if(x == 0 || x + entity.getWidth() >= FXGL.getAppWidth()-250 || y == 0 || y + entity.getHeight() >= FXGL.getAppHeight()-250)
			isOnBorder = true;
		size = (int) Math.max(entity.getWidth(),entity.getHeight()) - 70;
		updateTimer = FXGL.newLocalTimer();
	}
	
	public void addCar(Entity e) {
		if(!carList.contains(e)) {
			carList.add(e);
			current += calcWidth(e);
			//System.out.println("size : " + size + "----- this : " + current + "------ add : " + calcWidth(e));
		}
	}

	public void removeCar(Entity e) {
		if(carList.contains(e)) {
			current -= calcWidth(e);
			carList.remove(e);
			//System.out.println("size : " + size + "----- this : " + current + "------ remove : " + calcWidth(e));
		}
	}

	public boolean isFree(Entity e) { 
		//System.out.println("size : " + size + "----- this : " + current + "------ car : " + calcWidth(e));
		return size > current + calcWidth(e) || isOnBorder; }
	
	private int calcWidth(Entity e) { return (int) (e.getWidth() - sF); }

}

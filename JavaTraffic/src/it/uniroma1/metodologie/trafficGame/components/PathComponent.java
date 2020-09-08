package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class PathComponent extends Component{

	private int size;
	private int current;
	private List<Entity> carList = new LinkedList<>();
	private int sF = 10; //devo togliere dalla width ella macchina lo spazio di frenata perché se venisse aggiunto a current il path si saturerebbe troppo presto rispetto alla sua attuale capacità

	@Override
	public void onAdded() { size = (int) Math.max(entity.getWidth(),entity.getHeight()) - 70; }

	public void addCar(Entity e) {
		if(!carList.contains(e)) {
			carList.add(e);
			current += calcWidth(e);
		}
	}

	public void removeCar(Entity e) {
			current -= calcWidth(e);
			carList.remove(e); 
	}

	public boolean isFree(Entity e) { return size > current + calcWidth(e); }
	
	private int calcWidth(Entity e) { return (int) (e.getWidth() - sF); }

}

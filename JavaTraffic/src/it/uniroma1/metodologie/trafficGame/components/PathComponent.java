package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class PathComponent extends Component{

	private int size;
	private int current;
	private List<Entity> carList = new LinkedList<>();
	private int sF = 20; //devo togliere dalla width ella macchina lo spazio di frenata perch� se venisse aggiunto a current il path si saturerebbe troppo presto rispetto alla sua attuale capacit�

	@Override
	public void onAdded() {
		size = (int) Math.max(entity.getWidth(),entity.getHeight());
	}

	public void addCar(Entity e) {
		if(!carList.contains(e)) {
			carList.add(e);
			current += calcWidth(e);
			System.out.println("add size : " + size + "------current : " + current + "-----added : " + calcWidth(e));
		}
	}

	public void removeCar(Entity e) {
			current -= calcWidth(e);
			carList.remove(e);
			System.out.println("remove size : " + size + "------current : " + current + "-----removed : " + calcWidth(e));
	}

	public boolean isFree(Entity e) {
		System.out.println("passing,     size : " + size + "------current : " + current + "-----car : " + calcWidth(e) + "       " + (size > current + calcWidth(e)));
		return size > current + calcWidth(e);
	}
	
	private int calcWidth(Entity e) { return (int) (e.getWidth() - sF); }

}

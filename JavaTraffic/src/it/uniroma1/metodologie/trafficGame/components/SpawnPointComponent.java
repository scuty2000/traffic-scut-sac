package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Rectangle2D;
import tutorial.AndreaGameApp.EntityType;

public class SpawnPointComponent extends Component{
	
	private LinkedList<String> vehicleList;
	private LinkedList<SpawnData> spawnDataList;
	private static final int DEATH = 6;
	
	@Override
	public void onAdded() {
		vehicleList = new LinkedList<>();
		spawnDataList = new LinkedList<>();
	}
	
	@Override
	public void onUpdate(double tps) {
		spawnCar();
		if(hasLost())
			FXGL.getGameController().gotoMainMenu();
	}
	
	public void registerCar(String string, SpawnData sd) { 
		vehicleList.add(string);	
		spawnDataList.add(sd);
		}
	
	public boolean hasLost() { return vehicleList.size() >= DEATH; }
	
	public void spawnCar() {
		if(isFree() && !vehicleList.isEmpty()) {
			FXGL.getGameWorld().spawn(vehicleList.remove(0), spawnDataList.remove(0));
		}
	}
	
	private boolean isFree() { return !FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(entity.getX(), entity.getY(), 40, 40)).stream().anyMatch(x -> x.getType().equals(EntityType.VEHICLE)); }
}

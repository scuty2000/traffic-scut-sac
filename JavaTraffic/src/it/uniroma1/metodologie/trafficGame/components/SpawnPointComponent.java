package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import it.uniroma1.metodologie.trafficGame.TrafficApp;
import javafx.geometry.Rectangle2D;
import tutorial.AndreaGameApp.EntityType;

public class SpawnPointComponent extends Component{
	
	private int vehicles = 0;
	private LinkedList<SpawnData> spawnDataList;
	private static final int DEATH = 6;
	
	@Override
	public void onAdded() { spawnDataList = new LinkedList<>(); }
	
	@Override
	public void onUpdate(double tps) {
		spawnCar();
		if(hasLost()) {
			FXGL.getGameController().gotoMainMenu();
			TrafficApp mainApp = (TrafficApp) FXGL.getApp();
			FXGL.getAudioPlayer().stopMusic(mainApp.getGameMusic());
		}
	}
	
	public void registerCar(SpawnData sd) { 
		vehicles ++;
		spawnDataList.add(sd);
		}
	
	public boolean hasLost() { return vehicles >= DEATH; }
	
	public void spawnCar() {
		if(isFree() && vehicles > 0) {
			FXGL.getGameWorld().spawn("vehicle", spawnDataList.remove(0));
			vehicles --;
		}
	}
	
	private boolean isFree() { return !FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(entity.getX(), entity.getY(), 40, 40)).stream().anyMatch(x -> x.getType().equals(EntityType.VEHICLE)); }
}

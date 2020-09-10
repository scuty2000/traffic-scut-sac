package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.time.LocalTimer;
import it.uniroma1.metodologie.trafficGame.TrafficApp;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SpawnPointComponent extends Component{
	
	private int vehicles = 0;
	private LocalTimer counterTimer;
	private Entity queueCounter;
	
	private int isFree;

	private LinkedList<SpawnData> spawnDataList;
	private static final int DEATH = 6;
	private Point2D spawnPointPosition;
	
	private boolean hasLost;
	
	private static final Color NP_COLOR = Color.LIGHTBLUE;
	private static final Color WARNING_COLOR = Color.DARKGOLDENROD;
	private static final Color DANGER_COLOR = Color.DARKRED;
	
	@Override
	public void onAdded() { 
		spawnDataList = new LinkedList<>();
		spawnPointPosition = entity.getPosition();
	}
	
	@Override
	public void onUpdate(double tps) {
		spawnCar();
		if(hasLost) {
			//saving			
			FXGL.getGameScene().clearUINodes();//delete the score text
			FXGL.getGameController().gotoMainMenu();
			TrafficApp mainApp = (TrafficApp) FXGL.getApp();
		}
		if(counterTimer == null)
			counterTimer = FXGL.newLocalTimer();
		if(counterTimer.elapsed(Duration.seconds(0.5))) {
			spawnQueuedCount();
		}
	}
	
	public void registerCar(SpawnData sd) { 
		vehicles ++;
		spawnDataList.add(sd);
		hasLost = vehicles >= DEATH;
	}
	
	public void spawnCar() {
		if(vehicles > 0 && isFree()) {
			Entity e = FXGL.getGameWorld().spawn("vehicle", spawnDataList.remove(0));
			e.getComponent(VehicleComponent.class).getCurrentPath().getComponent(PathComponent.class).addCar(e);
			addCarToFree();
			vehicles --;
		}
	}
	

	private void spawnQueuedCount() {
		if(queueCounter != null)
			queueCounter.removeFromWorld();
		
		String direzione = (String) entity.getPropertyOptional("direzione").orElse("UP");
		int xTranslation = 0;
		int yTranslation = 0;
		
		int linkedTileX = (int)(spawnPointPosition.getX()/250) * 250 + 250;
		int linkedTileY = (int)(spawnPointPosition.getY()/250) * 250 + 250;
		
		Color color = null;
		
		switch(direzione) {
			case "UP":
				xTranslation = 5;
				yTranslation = -302;
				break;
			case "DOWN":
				xTranslation = -300;
				yTranslation = -245;
				break;
			case "RIGHT":
				xTranslation = -245;
				yTranslation = 5;
				break;
			case "LEFT":
				xTranslation = -300;
				yTranslation = -302;
				break;
		}
		
		if(vehicles < 2) {
			color = NP_COLOR;
		} else if(vehicles < 4) {
			color = WARNING_COLOR;
		} else {
			color = DANGER_COLOR;
		}
		
		VBox box = new VBox(FXGL.getUIFactoryService().newText(this.vehicles+"", 40.0));
		box.setBackground(new Background(new BackgroundFill(color, new CornerRadii(15), null)));
		box.setMinWidth(45);
		box.setMaxHeight(45);
		box.setAlignment(Pos.CENTER);
		
		queueCounter = FXGL.entityBuilder()
				.at(linkedTileX+xTranslation, linkedTileY+yTranslation)
				.viewWithBBox(box)
				.buildAndAttach();
		
	}
	
	
	
	private boolean isFree() { return isFree <= 0; }
	
	public void addCarToFree() { isFree ++; }
	public void subCarToFree() { isFree --; }
	
}

package it.uniroma1.metodologie.trafficGame.components;

import java.util.LinkedList;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class represents a SpawnPoint on the map.
 * It is responsible for the end of the game and
 * for the spawning of the Vehicles.
 */
public class SpawnPointComponent extends Component{
	
	/**
	 * Number of vehicles in queue
	 */
	private int vehicles = 0;
	/**
	 *  Local Timer used for spawn
	 */
	private LocalTimer counterTimer;
	/**
	 * Entity that show the number
	 * of vehicles in queue
	 */
	private Entity queueCounter;
	
	/**
	 * Shows if a spawnpoint is free
	 */
	private int isFree;

	/**
	 * List that contains all the entities waiting
	 * to be spawned
	 */
	private LinkedList<SpawnData> spawnDataList;
	/**
	 * Limit of vehicles that can be in queue
	 */
	private static final int DEATH = 6;
	/**
	 * Position of the spawnpoint on the map
	 */
	private Point2D spawnPointPosition;
	
	/**
	 * Used to indicate if the player
	 * has lost
	 */
	private boolean hasLost;
	
	/**
	 * Color used to indicate that the spawnpoint is ok
	 */
	private static final Color NP_COLOR = Color.LIGHTBLUE;
	/**
	 * Color used to indicate that the spawnpoint is
	 * a little bit full
	 */
	private static final Color WARNING_COLOR = Color.DARKGOLDENROD;
	/**
	 * Color used to indicate that the spawnpoint is
	 * dangerously full
	 */
	private static final Color DANGER_COLOR = Color.DARKRED;
	
	/**
	 * Instantiates the list and gets
	 * the position of the spawnpoint
	 * when it gets added
	 */
	@Override
	public void onAdded() { 
		spawnDataList = new LinkedList<>();
		spawnPointPosition = entity.getPosition();
	}
	
	/**
	 * Here the counter gets updated and
	 * checks if the player has lost the play
	 * 
	 * In that case it show a message box
	 * reporting the final score
	 */
	@Override
	public void onUpdate(double tps) {
		spawnCar();
		if(hasLost) {
			FXGL.getGameScene().clearUINodes();//delete the score text
//			FXGL.getDialogService().showBox("Hai perso!\nIl tuo punteggio finale è:", FXGL.getUIFactoryService().newText("--< "+FXGL.getWorldProperties().getInt("score")+" >--", Color.DARKRED, 50.0), new Button("Torna al menù principale"));
//			FXGL.getGameController().gotoMainMenu();
			FXGL.getDialogService().showMessageBox("Hai perso!\nIl tuo punteggio finale è:\n\n"+"--< "+FXGL.getWorldProperties().getInt("score")+" >--", () -> FXGL.getGameController().gotoMainMenu());
		}
		if(counterTimer == null)
			counterTimer = FXGL.newLocalTimer();
		if(counterTimer.elapsed(Duration.seconds(0.5))) {
			spawnQueuedCount();
		}
	}
	
	/**
	 * This registers every car to the list
	 * of cars
	 * @param sd
	 */
	public void registerCar(SpawnData sd) { 
		vehicles ++;
		spawnDataList.add(sd);
		hasLost = vehicles >= DEATH;
	}
	
	/**
	 * This spawns the cars waiting in queue if 
	 * the spawnpoint is free
	 */
	public void spawnCar() {
		if(vehicles > 0 && isFree()) {
			Entity e = FXGL.getGameWorld().spawn("vehicle", spawnDataList.remove(0));
			e.getComponent(VehicleComponent.class).getCurrentPath().getComponent(PathComponent.class).addCar(e);
			addCarToFree();
			vehicles --;
		}
	}
	

	/**
	 * This sets the position and the text
	 * shown to the user reporting the cars
	 * that are waiting in a particular
	 * spawnpoint
	 */
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
	
	/**
	 * This method sets isFree
	 * @return
	 */
	private boolean isFree() { return isFree <= 0; }
	
	public void addCarToFree() { isFree ++; }
	public void subCarToFree() { isFree --; }
	
}

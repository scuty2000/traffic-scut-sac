package it.uniroma1.metodologie.trafficGame;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;

import javafx.geometry.Point2D;
import javafx.scene.text.Text;
import tutorial.AndreaFactory;
import tutorial.AndreaGameApp.EntityType;

public class TrafficApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
    	settings.setDeveloperMenuEnabled(true); // Use 1 and 2 keys to access variables values and in-game console //TODO remove before production
    	settings.setVersion("Alpha 0.0"); // To update periodically
    	settings.setTitle("  Traffic  ");
    	settings.setWidth(3000);
    	settings.setHeight(3000);
    }
    
    
    private Entity player1;
    private Entity player2;
    
    private List<Entity> incrociList = new LinkedList<>();
    
    @Override
    protected void initGame() {
    	FXGL.getGameWorld().addEntityFactory(new TrafficFactory());
		
		FXGL.setLevelFromMap("tutorialMap.tmx");
		
		SpawnData vdata = new SpawnData(new Point2D(300,300)).put("direction", "RIGHT");
		
		FXGL.spawn("vehicle", vdata);
		
		incrociList = FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.INCROCIO)).sorted((s1,s2) ->(int)(distance(s1)-distance(s2))).collect(Collectors.toCollection(LinkedList::new));
		System.out.println(incrociList);
		
		player1 = FXGL.spawn("player",new SpawnData(incrociList.get(0).getPosition()).put("player", "player1"));
		
		System.out.println(player1.getPosition());
    }
    
    /**
     * this method returns the distance of an entity from the origin
     * @param e
     * @return
     */
    private double distance(Entity e) {
    	return Math.sqrt(Math.pow(e.getX(),2) + Math.pow(e.getY(),2));
    }

    @Override
    protected void initUI() {
        // 1. create any JavaFX or FXGL UI object
        Text uiText = new Text("Hello World!");

        // 2. add the UI object to game scene (easy way) at 100, 100
        FXGL.addUINode(uiText, 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
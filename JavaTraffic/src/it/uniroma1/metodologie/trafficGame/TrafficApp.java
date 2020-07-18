package it.uniroma1.metodologie.trafficGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import it.uniroma1.metodologie.trafficGame.components.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
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
    
    private String map = "tutorialMap.tmx";
    
    private HashMap<Integer, ArrayList<Entity>> matrixIncroci;
    
    @Override
    protected void initGame() {
    	FXGL.getGameWorld().addEntityFactory(new TrafficFactory());
		
		FXGL.setLevelFromMap("tutorialMap.tmx");
		
		SpawnData vdata = new SpawnData(new Point2D(300,300)).put("direction", "RIGHT");
		
		FXGL.spawn("vehicle", vdata);
		
		matrixIncroci = parseIncroci();
		
		player1 = FXGL.spawn("player",new SpawnData(matrixIncroci.get(0).get(0).getPosition()).put("player", "player1"));
		
		System.out.println(player1.getPosition());
    }
    
    private HashMap<Integer, ArrayList<Entity>> parseIncroci() {
    	List<Entity> list = getListaIncroci();
    	HashMap<Integer, ArrayList<Entity>> matrix = new HashMap<>();
    	Double old = null;
    	int c = 0;
    	for(Entity elem : list) {														//creating the HashMap with an integer representing Y as key and an ArrayList<Entity> which contains the elements with the same Y  
    		if(old == elem.getY())
    				matrix.get(c).add(elem);
    		else {
    			matrix.put(++c, new ArrayList<Entity>(List.of(elem)));
    			old = elem.getY();
    		}
    	}
    	matrix.forEach((k,v) -> v.sort((e1,e2) -> (int)((long)e1.getX() - e2.getX())));	//ordering the Lists
    	return matrix;
    }
    
    private List<Entity> getListaIncroci(){
    	return FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.INCROCIO)).sorted((s1,s2) ->(int)(distance(s1)-distance(s2))).collect(Collectors.toCollection(LinkedList::new));

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
    protected void initInput() {
    	Input i = FXGL.getInput();
    	
    	
    	
    	i.addAction(new UserAction("Move Right") {
    		
			@Override
			protected void onActionBegin() {
				int i = (int) player1.getPropertyOptional("pointerAtIncrociList").orElse(Integer.valueOf(0));
				if(i < incrociList.size() - 1) {
					player1.setPosition(incrociList.get(++i).getPosition());
					player1.setProperty("pointerAtIncrociList", i);;
				}
					
			}
		}, KeyCode.D);
    	i.addAction(new UserAction("Move Left") {
			@Override
			protected void onActionBegin() {
				int i = (int) player1.getPropertyOptional("pointerAtIncrociList").orElse(Integer.valueOf(0));
				if(i > 0) {
					player1.setPosition(incrociList.get(--i).getPosition());
					player1.setProperty("pointerAtIncrociList", i);;
				}
					
			}
		}, KeyCode.A);
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
package it.uniroma1.metodologie.trafficGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.sun.javafx.scene.traversal.Direction;

import it.uniroma1.metodologie.trafficGame.components.PlayerComponent;
import it.uniroma1.metodologie.trafficGame.components.VehicleComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import tutorial.AndreaFactory;
import tutorial.AndreaSemaforoComponent;
import tutorial.AndreaGameApp.EntityType;

public class TrafficApp extends GameApplication {

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setDeveloperMenuEnabled(true); // Use 1 and 2 keys to access variables values and in-game console //TODO remove before production
		settings.setVersion("Alpha 0.0"); // To update periodically
		settings.setTitle("  Traffic  ");
		settings.setWidth(2500);
		settings.setHeight(2500);
	}


	private Entity player1;
	private Entity player2;

	private String map = "tutorialMap.tmx";

	private HashMap<Integer, ArrayList<Entity>> matrixIncroci;

	@Override
	protected void initGame() {
		FXGL.getGameWorld().addEntityFactory(new TrafficFactory());

		FXGL.setLevelFromMap(map);

		SpawnData vdata = new SpawnData(new Point2D(2000,0)).put("direction", "RIGHT");

		FXGL.spawn("vehicle", vdata);

		matrixIncroci = parseIncroci();		//gets the grid of the semafori

		player1 = FXGL.spawn("player",new SpawnData(matrixIncroci.get(0).get(0).getPosition()).put("player", "player1"));

	}

	private HashMap<Integer, ArrayList<Entity>> parseIncroci() {
		List<Entity> list = getListaIncroci();
		HashMap<Integer, ArrayList<Entity>> matrix = new HashMap<>();
		Double old = -1.0;
		int c = -1;
		for(Entity elem : list) {														//creating the HashMap with an integer representing Y as key and an ArrayList<Entity> which contains the elements with the same Y  
			System.out.println(elem.getY());
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
		return FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.INCROCIO)).sorted((s1,s2) ->(int) s1.getY() - (int)s2.getY()).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	protected void initInput() {
		Input i = FXGL.getInput();



		i.addAction(new UserAction("Move Right") {

			@Override
			protected void onActionBegin() {
				move(Directions.RIGHT, x -> x < matrixIncroci.get(player1.getPropertyOptional("pointerY").orElse(0)).size() - 1, "pointerX");
			}
		}, KeyCode.D);

		i.addAction(new UserAction("Move Left") {
			@Override
			protected void onActionBegin() {
				move(Directions.LEFT, x -> x > 0, "pointerX");	
			}
		}, KeyCode.A);

		i.addAction(new UserAction("Move Up") {
			@Override
			protected void onActionBegin() {
				move(Directions.UP, y -> y > 0, "pointerY");	
			}
		}, KeyCode.W);

		i.addAction(new UserAction("Move Down") {
			@Override
			protected void onActionBegin() {
				move(Directions.DOWN, y -> y < matrixIncroci.size() - 1, "pointerY");	
			}
		}, KeyCode.S);
	}

	private void move(Directions d, Predicate<Integer> p, String pointer) {
		int i = (int) player1.getPropertyOptional(pointer).orElse(0);
		if(p.test(i)) {
			player1.setPosition(matrixIncroci.get((int)player1.getPropertyOptional("pointerY").orElse(0) + d.getY()).get(d.getX() + (int) player1.getPropertyOptional("pointerX").orElse(Integer.valueOf(0))).getPosition());
			player1.setProperty("pointerX", (int)player1.getPropertyOptional("pointerX").orElse(0) + d.getX());
			player1.setProperty("pointerY", (int)player1.getPropertyOptional("pointerY").orElse(0) + d.getY());
		}
	}

	@Override
	protected void initUI() {
		// 1. create any JavaFX or FXGL UI object
		//Text uiText = new Text("Hello World!");

		// 2. add the UI object to game scene (easy way) at 100, 100
		//FXGL.addUINode(uiText, 100, 100);
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initPhysics() {
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.INCROCIO) {

			@Override
			protected void onCollisionBegin(Entity v, Entity i) {
				Vehicle enumv = v.getComponentOptional(VehicleComponent.class).orElse(null).getVehicle();
				if(enumv.canTurn()) {
					Optional<String> o = i.getPropertyOptional("direzione");
					if(o.isEmpty()) 		//if the direction property is empty the incrocio has to be a incrocio4
						enumv.setDirection(Directions.values()[new Random().nextInt(4)]);
					else {
						Directions d = Directions.valueOf((String) o.orElse(null));	//d is the direction that can not be used
						while(d == enumv.getDirection())
							enumv.setDirection(Directions.values()[new Random().nextInt(4)]);
					}
				}
			}

		});
	}
}
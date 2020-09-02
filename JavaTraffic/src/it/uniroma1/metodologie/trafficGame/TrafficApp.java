package it.uniroma1.metodologie.trafficGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.LocalTimer;

import it.uniroma1.metodologie.trafficGame.components.TrafficLightAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.VehicleComponent;
import it.uniroma1.metodologie.trafficGame.ui.TrafficAppMenu;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import tutorial.AndreaGameApp.EntityType;

public class TrafficApp extends GameApplication {

	private ArrayList<Entity> incroci;

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setDeveloperMenuEnabled(true); // Use 1 and 2 keys to access variables values and in-game console //TODO remove before production
		settings.setVersion("Alpha 1.0"); // To update periodically
		settings.setTitle("  Traffic  ");
		settings.setWidth(2500);
		settings.setHeight(2500);
		settings.setApplicationMode(ApplicationMode.DEVELOPER);
		settings.setProfilingEnabled(true);
		settings.setMainMenuEnabled(true);
		settings.setManualResizeEnabled(true);
		settings.setPreserveResizeRatio(true);
		settings.setSceneFactory(new SceneFactory() {
			@Override
			public FXGLMenu newMainMenu() {
				return new TrafficAppMenu(MenuType.MAIN_MENU);
			}
		});
	}


	private Entity player1;
	//private Entity player2;

	private String map = "map-v2.tmx";

	private HashMap<Integer, ArrayList<Entity>> matrixIncroci;

	@Override
	protected void initGame() {

		GameWorld gw = FXGL.getGameWorld();

		gw.addEntityFactory(new TrafficFactory());

		FXGL.setLevelFromMap(map);

		Entity e = gw.getEntities().stream().filter(x -> x.getType().equals(EntityType.SPAWN)).findFirst().orElse(null);

		SpawnData vdata = new SpawnData(e.getPosition());

		vdata.put("direction", Directions.valueOf((String)e.getPropertyOptional("direzione").orElse("RIGHT")));

		//FXGL.spawn("vehicle", vdata);

		getPathTree();

		matrixIncroci = parseIncroci();		//gets the grid of the semafori

		player1 = FXGL.spawn("player",new SpawnData(matrixIncroci.get(0).get(0).getPosition()).put("player", "player1"));

		incroci = (ArrayList<Entity>) gw.getEntitiesByType(EntityType.INCROCIO);

		for (Entity semaforo : FXGL.getGameWorld().getEntitiesByType(EntityType.SEMAFORO)) {
			if((semaforo.getProperties().getInt("rotation") == 1 || semaforo.getProperties().getInt("rotation") == 3))
				semaforo.getComponent(TrafficLightAnimationComponent.class).switchLight();
		}
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

		i.addAction(new UserAction("Change trafficlight status") {
			@Override
			protected void onActionBegin() {
				ArrayList<Entity> semaforiAdiacenti = (ArrayList<Entity>) FXGL.getGameWorld()
						.getEntitiesByType(EntityType.SEMAFORO)
						.stream()
						.filter(x -> x.getPosition().distance(player1.getPosition()) <= 354)
						.collect(Collectors.toList());

				for (Entity entity : semaforiAdiacenti) {
					entity.getComponent(TrafficLightAnimationComponent.class).switchLight();
				}

			}
		}, KeyCode.F);

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
				moveUpDown(Directions.UP, y -> y > 0, "pointerY");	
			}
		}, KeyCode.W);

		i.addAction(new UserAction("Move Down") {
			@Override
			protected void onActionBegin() {
				moveUpDown(Directions.DOWN, y -> y < matrixIncroci.size() - 1, "pointerY");	
			}
		}, KeyCode.S);

		i.addAction(new UserAction("Spawn Car") {
			@Override
			protected void onActionBegin() {
				Entity e = FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.SPAWN)).collect(Collectors.toList()).get(new Random().nextInt(1));

				SpawnData vdata = new SpawnData(e.getPosition());

				vdata.put("pathList", pathChooser(e));

				vdata.put("direction", Directions.valueOf((String)e.getPropertyOptional("direzione").orElse("RIGHT")));

				FXGL.getGameWorld().spawn("vehicle", vdata);
			}
		}, KeyCode.SPACE);
	}

	private void moveUpDown(Directions d, Predicate<Integer> p, String pointer) {
		int i = (int) player1.getPropertyOptional(pointer).orElse(0);
		if(i > 1 && matrixIncroci.get(i + d.getY()).size() < matrixIncroci.get(i).size()) {
			List<Entity> l = matrixIncroci.get(i + d.getY());
			player1.setPosition(l.get(l.size()-1).getPosition());
			player1.setProperty("pointerX", (int)player1.getPropertyOptional("pointerX").orElse(0) + d.getX());
			player1.setProperty("pointerY", (int)player1.getPropertyOptional("pointerY").orElse(0) + d.getY());
		}
		else
			move(d, p, pointer);
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

	private Entity findCarBehind(Entity e1, Entity e2) {
		Directions direction = e1.getComponent(VehicleComponent.class).getDirection();
		//		if(!direction.equals(e2.getComponent(VehicleComponent.class).getDirection()))
		//			return null;
		Entity r = null;
		switch(direction) {
		case UP:
			r = e1.getY() > e2.getY() ? e1 : e2;break;
		case DOWN:
			r = e1.getY() > e2 .getY() ? e2 : e1;break;
		case LEFT:
			r = e1.getX() > e2 .getX() ? e1 : e2;break;
		case RIGHT:
			r = e1.getX() > e2 .getX() ? e2 : e1;break;
		}
		return r;
	}

	@Override
	protected void initPhysics() {



		/**
		 * Qui inizia il proto-collision dei veicoli. Fa altamente cagare e mi sento male solo a leggerlo, poi funziona pure male.
		 */

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.VEHICLE) {

			Entity carBehind;
			Entity carInFront;
			@Override
			protected void onCollisionBegin(Entity v, Entity i) {
				carBehind = findCarBehind(v, i);
				carInFront = carBehind == v ? i : v;
				if(carBehind != null)
					carBehind.getComponent(VehicleComponent.class).slowDown();
			}

			
			/*
			 *commentando l'on collision non c'� pi� il problema delle macchine che non ripartono 
			 */
//			@Override
//			protected void onCollision(Entity v, Entity i) {
//				if(carBehind != null) {
//					if(carBehind.getLocalAnchor().distance(carInFront.getLocalAnchor()) < carInFront.getWidth())
//						carBehind.getComponent(VehicleComponent.class).slowDown();
//					else
//						carBehind.getComponent(VehicleComponent.class).accelerate();
//				}
//			}

			@Override
			protected void onCollisionEnd(Entity a, Entity b) {
				a.getComponent(VehicleComponent.class).accelerate();
				b.getComponent(VehicleComponent.class).accelerate();
			}

		});

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.INCROCIO) {

			@Override
			protected void onCollisionBegin(Entity v, Entity i) {
				//				Entity e = FXGL.entityBuilder()
				//						.bbox(v.getBoundingBoxComponent().hitBoxesProperty().stream().filter(x -> x.getName().equals("__VIEW__")).findFirst().orElse(v.getBoundingBoxComponent().hitBoxesProperty().get(0)))
				//						.build();
				//				System.out.println(e.getBoundingBoxComponent().hitBoxesProperty().get(0).getName());
				//				if(i.isColliding(e))
				if(v.getComponent(VehicleComponent.class).getVehicle().canTurn())
					turnVehicle(v, i);
			}
		});		

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.SEMAFORO) {

			@Override
			protected void onCollisionBegin(Entity v, Entity i) {
				if(!i.getComponent(TrafficLightAnimationComponent.class).isGreen()
						&& !v.isColliding(v.getComponent(VehicleComponent.class).getNearestIncrocio()))
					v.getComponent(VehicleComponent.class).slowDown();
			}

			@Override
			protected void onCollision(Entity v, Entity i) {
				if(i.getComponentOptional(TrafficLightAnimationComponent.class).get().isGreen()
						|| v.isColliding(v.getComponent(VehicleComponent.class).getNearestIncrocio()))
					v.getComponentOptional(VehicleComponent.class).get().accelerate();
				else
					v.getComponentOptional(VehicleComponent.class).get().slowDown();

			}
		});	
	}

	private LocalTimer spawnTimer;

	private double spawnRate = 2;

	@Override
	protected void onUpdate(double tpf) {
		if(spawnTimer == null)
			spawnTimer = FXGL.newLocalTimer();
		if(spawnTimer.elapsed(Duration.seconds(spawnRate))) {
			Entity e = FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.SPAWN)).collect(Collectors.toList()).get(new Random().nextInt(6));

			SpawnData vdata = new SpawnData(e.getPosition());

			vdata.put("direction", Directions.valueOf((String)e.getPropertyOptional("direzione").orElse("RIGHT")));
			vdata.put("pathList", pathChooser(e));

			//FXGL.getGameWorld().spawn("vehicle", vdata);

			spawnTimer.capture();
		}
	}

	private void turnVehicle(Entity v, Entity i) {
		v.getComponent(VehicleComponent.class).nextPath();
	}

	HashMap<Entity,List<Entity>> pathMap;

	private HashMap<Entity, List<Entity>> getPathTree() {
		if(pathMap == null) {
			pathMap = new HashMap<>();
			List<Entity> pathList = FXGL.getGameWorld().getEntitiesByType(EntityType.PATH);
			pathList.sort(Comparator.comparing(x -> ((Entity)x).getY()));
			//pathList.forEach(x -> System.out.println(x.getProperties()));
			pathList.forEach(x -> pathMap.put(x, getPathDirections(x)));
		}
		return pathMap;
	}

	private List<Entity> getPathDirections(Entity e){
		return Arrays.stream(((String) e.getPropertyOptional("paths").orElse("")).split(","))
				.filter(s -> s.length() > 0)
				.map(x -> findPath(x))
				.collect(Collectors.toList());
	}

	private Entity findPath(String x) {
		return FXGL.getGameWorld().getEntityByID("path", Integer.parseInt(x)).orElse(null);
	}

	public List<Entity> pathChooser(Entity spawn){
		LinkedList<Entity> paths = new LinkedList<>();

		Entity root = FXGL.getGameWorld().getEntitiesByType(EntityType.PATH).stream().filter(x -> spawn.isColliding(x)).findFirst().get();

		paths.add(root);

		while(((String) paths.getLast().getPropertyOptional("paths").orElse("")).split(",").length > 1) {
			paths.add(nextPathFinder((String) root.getPropertyOptional("direzione").get(), ((String) paths.getLast().getPropertyOptional("paths").orElse("")).split(",")));
		}

		return paths;
	}

	private Entity nextPathFinder(String current, String[] strings) {
		//lista dei possibili path
		List<Entity> l = Arrays.stream(strings).map(x -> findPath(x)).filter(x -> x != null).collect(Collectors.toList());
		Random r = new Random();
		if(l.isEmpty()) return null;
		return l.get(r.nextInt(l.size()));
	}

}
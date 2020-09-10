package it.uniroma1.metodologie.trafficGame;

import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import it.uniroma1.metodologie.trafficGame.components.CrossRoadComponent;
import it.uniroma1.metodologie.trafficGame.components.PathComponent;
import it.uniroma1.metodologie.trafficGame.components.SpawnPointComponent;
import it.uniroma1.metodologie.trafficGame.components.TrafficLightAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.VehicleComponent;
import it.uniroma1.metodologie.trafficGame.ui.TrafficAppMenu;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tutorial.AndreaGameApp.EntityType;

public class TrafficApp extends GameApplication {

	private ArrayList<Entity> incroci;
	
	private Sound pointersound;
	
	private Music gameMusic;

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setDeveloperMenuEnabled(true); // Use 1 and 2 keys to access variables values and in-game console //TODO remove before production
		settings.setVersion("Alpha 1.0"); // To update periodically
		settings.setTitle("Traffic");
		settings.setWidth(2500);
		settings.setHeight(1750);
		settings.setApplicationMode(ApplicationMode.DEVELOPER);
		settings.setProfilingEnabled(true);
		settings.setMainMenuEnabled(true);
		settings.setManualResizeEnabled(true);
		settings.setPreserveResizeRatio(true);
		settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
		settings.setSceneFactory(new SceneFactory() {
			@Override
			public FXGLMenu newMainMenu() {
				return new TrafficAppMenu(MenuType.MAIN_MENU);
			}
			
		});
	}


	private Entity player1;
	//private Entity player2;

	private String map = "map-v3.tmx";

	private HashMap<Integer, ArrayList<Entity>> matrixIncroci;
	
	private List<Entity> spawnList;

	@Override
	protected void initGame() {
		

		GameWorld gw = FXGL.getGameWorld();

		gw.addEntityFactory(new TrafficFactory());

		FXGL.setLevelFromMap(map);
		
		pointersound = FXGL.getAssetLoader().loadSound("pointersound.wav");
		
		gameMusic = FXGL.getAssetLoader().loadMusic("mainsound.wav");
		
//		FXGL.getAudioPlayer().loopMusic(gameMusic);
		
		spawnList = gw.getEntities().stream().filter(x -> x.getType().equals(EntityType.SPAWN)).collect(Collectors.toList());

		//getPathTree();

		matrixIncroci = parseIncroci();		//gets the grid of the semafori

		player1 = FXGL.spawn("player",new SpawnData(matrixIncroci.get(0).get(0).getPosition()).put("player", "player1"));

		incroci = (ArrayList<Entity>) gw.getEntitiesByType(EntityType.INCROCIO);
		
		for(Entity i : incroci) {
			List<Entity> le = getSemaforiAdiacenti(i);
			le.forEach(x -> x.getComponent(TrafficLightAnimationComponent.class).setCrossRoad(i));
			System.out.println(i);
		}
		for (Entity semaforo : FXGL.getGameWorld().getEntitiesByType(EntityType.SEMAFORO)) {
			if((semaforo.getProperties().getInt("rotation") == 1 || semaforo.getProperties().getInt("rotation") == 3))
				semaforo.getComponent(TrafficLightAnimationComponent.class).switchLight();
		}
		
		this.SCORE_TIMER = FXGL.newLocalTimer();
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
	//####################################
	private List<Entity> getSemaforiAdiacenti(Entity p) {
		return checkSemafori((List<Entity>) FXGL.getGameWorld()
					.getEntitiesInRange(new Rectangle2D(p.getX(), p.getY(), 252, 252))
					.stream()
					.filter(x -> x.getType().equals(EntityType.SEMAFORO))
					.collect(Collectors.toList()), p);

	}
	
	private List<Entity> checkSemafori(List<Entity> sa, Entity e){ return sa.parallelStream().filter(x-> checkPosition(x, e)).collect(Collectors.toList()); }
	
	private boolean checkPosition(Entity s, Entity e) {
		switch(Directions.valueOf((String) s.getPropertyOptional("direzione").orElseThrow())) {
		case UP:
			return s.getY() > e.getY() && s.getX() > e.getX();
		case DOWN:
			return s.getY() < e.getY() && s.getX() < e.getX();
		case RIGHT:
			return s.getX() < e.getX() && s.getY() > e.getY();
		default:
			return s.getX() > e.getX() && s.getY() < e.getY();
		}
	}
	//####################################
	@Override
	protected void initInput() {
		Input i = FXGL.getInput();

		i.addAction(new UserAction("Change trafficlight status") {
			@Override
			protected void onActionBegin() {
				for (Entity entity : getSemaforiAdiacenti(player1)) {
					entity.getComponent(TrafficLightAnimationComponent.class).switchLight();
				}
				FXGL.getAudioPlayer().playSound(pointersound);
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
				move(Directions.UP, y -> y > 0, "pointerY");	
			}
		}, KeyCode.W);

		i.addAction(new UserAction("Move Down") {
			@Override
			protected void onActionBegin() {
				move(Directions.DOWN, y -> y < matrixIncroci.size() - 1, "pointerY");	
			}
		}, KeyCode.S);

		i.addAction(new UserAction("Spawn Car") {
			@Override
			protected void onActionBegin() {
				spawnCar();
			}
		}, KeyCode.SPACE);
	}

	private void move(Directions d, Predicate<Integer> p, String pointer) {
		int i = (int) player1.getPropertyOptional(pointer).orElse(0);
		if(p.test(i)) {
			player1.setProperty("pointerX", (int)player1.getPropertyOptional("pointerX").orElse(0) + d.getX());
			player1.setProperty("pointerY", (int)player1.getPropertyOptional("pointerY").orElse(0) + d.getY());
			try {
			player1.setPosition(matrixIncroci.get((int)player1.getPropertyOptional("pointerY").orElse(0)).get((int) player1.getPropertyOptional("pointerX").orElse(Integer.valueOf(0))).getPosition());
			}
			catch(IndexOutOfBoundsException e) {
				player1.setProperty("pointerX", matrixIncroci.get(player1.getPropertyOptional("pointerY").orElseThrow()).size()-1);
				List<Entity> row = matrixIncroci.get(player1.getPropertyOptional("pointerY").orElseThrow());
				Point2D newPosition = row.get(row.size() - 1).getPosition();
				player1.setPosition(newPosition);
			}
		}
	}

	@Override
	protected void initUI() {
		// 1. create any JavaFX or FXGL UI object
		Text uiText = new Text("Score : " + FXGL.getWorldProperties().getInt("score"));
		uiText.setFont(javafx.scene.text.Font.font(30));
		// 2. add the UI object to game scene (easy way) at 100, 100
		FXGL.addUINode(uiText, 10, 30);
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
				i.getComponent(CrossRoadComponent.class).addCar();
				if(v.getComponent(VehicleComponent.class).getVehicle().canTurn())
					turnVehicle(v, i);
			}
			
			@Override
			protected void onCollisionEnd(Entity v, Entity i) { i.getComponent(CrossRoadComponent.class).subCar(); }
			
		});		

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.SEMAFORO) {

			@Override
			protected void onCollisionBegin(Entity v, Entity i) {
				if((i.getComponent(TrafficLightAnimationComponent.class).isRed()
						&& !v.isColliding(v.getComponent(VehicleComponent.class).getNearestIncrocio()))
						||
						!v.getComponent(VehicleComponent.class).getNextPath().getComponent(PathComponent.class).isFree(v)) {
					v.getComponent(VehicleComponent.class).slowDown();
					i.getComponent(TrafficLightAnimationComponent.class).registerCar(v);
				}
				else
					v.getComponent(VehicleComponent.class).getNextPath().getComponent(PathComponent.class).addCar(v);
				
			}
			
			@Override
			protected void onCollisionEnd(Entity v, Entity i) {
				i.getComponent(TrafficLightAnimationComponent.class).removeCar(v);
			}
		});	
		
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.PATH) {
			
			@Override
			protected void onCollisionEnd(Entity v, Entity p) {
				p.getComponent(PathComponent.class).removeCar(v);
			}
			
		});
		
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.VEHICLE, EntityType.SPAWN) {

			@Override
			protected void onCollisionEnd(Entity v, Entity s) {
				s.getComponent(SpawnPointComponent.class).subCarToFree();;
			}
			
		});
	}
	
	private static final int EASY = 110;
	private static final int MEDIUM = 50;
	private static final int HARD = 30;

	private int spawnRate = 120;	//fps before spawn of a new car
	private final int minSpawnRate = EASY;	//min spawn fps
	private int counter;
	private LocalTimer SCORE_TIMER;
	@Override
	protected void onUpdate(double tpf) {
		if(SCORE_TIMER.elapsed(Duration.seconds(1))) {
			FXGL.getWorldProperties().increment("score", pointsPerSec/5);
			FXGL.getGameScene().clearUINodes();
			Text uiText = new Text("Score : " + FXGL.getWorldProperties().getInt("score"));
			uiText.setFont(Font.font(30));
			// 2. add the UI object to game scene (easy way) at 100, 100
			FXGL.addUINode(uiText, 10, 30);
			SCORE_TIMER.capture();
		}
		if(counter > spawnRate) {
			spawnCar();
			counter = 0;
			if(spawnRate > minSpawnRate)
				spawnRate--;
		}
		counter ++;
	}
	
	private int pointsPerSec;
	
	private void spawnCar() {
		pointsPerSec ++;
		Entity e = FXGL.getGameWorld().getEntities().stream().filter(x -> x.getType().equals(EntityType.SPAWN)).collect(Collectors.toList()).get(new Random().nextInt(spawnList.size()));
		SpawnData vdata = new SpawnData(e.getPosition());
		vdata.put("pathList", pathChooser(e, true, true));
		vdata.put("direction", Directions.valueOf((String)e.getPropertyOptional("direzione").orElse("RIGHT")));
		vdata.put("tir", (boolean) e.getPropertyOptional("tir").orElse(false));
		e.getComponent(SpawnPointComponent.class).registerCar(vdata);
	}

	private void turnVehicle(Entity v, Entity i) {
		v.getComponent(VehicleComponent.class).nextPath();
	}

//	HashMap<Entity,List<Entity>> pathMap;
//
//	private HashMap<Entity, List<Entity>> getPathTree() {
//		if(pathMap == null) {
//			pathMap = new HashMap<>();
//			List<Entity> pathList = FXGL.getGameWorld().getEntitiesByType(EntityType.PATH);
//			pathList.sort(Comparator.comparing(x -> ((Entity)x).getY()));
//			//pathList.forEach(x -> System.out.println(x.getProperties()));
//			pathList.forEach(x -> pathMap.put(x, getPathDirections(x)));
//		}
//		return pathMap;
//	}
	
	/*
	 * this method returns the paths connected to a path
	 */

	public static List<Entity> getPathDirections(Entity e){
		return Arrays.stream(((String) e.getPropertyOptional("paths").orElse("")).split(","))
				.filter(s -> s.length() > 0)
				.map(x -> findPath(x))
				.collect(Collectors.toList());
	}

	protected static Entity findPath(String x) {
		return FXGL.getGameWorld().getEntityByID("path", Integer.parseInt(x)).orElse(null);
	}
	
	
	/*
	 * this method generates a list of paths that the car will follow
	 * if left is false the list generated will not have a left turn
	 * if right is false the list generated will not have a right turn
	 */

	public static List<Entity> pathChooser(Entity spawn, boolean left, boolean right){
		LinkedList<Entity> paths = new LinkedList<>();

		Entity root = FXGL.getGameWorld().getEntitiesByType(EntityType.PATH).stream().filter(x -> spawn.isColliding(x)).findFirst().get();
		
		
		
		paths.add(root);

		while(((String) paths.getLast().getPropertyOptional("paths").orElse("")).split(",").length > 1) {
			paths.add(nextPathFinder((String) root.getPropertyOptional("direzione").get(), ((String) paths.getLast().getPropertyOptional("paths").orElse("")).split(","), Directions.valueOf((String) paths.get(paths.size() - 1).getPropertyOptional("direzione").orElseThrow())));
		}

		return paths;
	}

	private static Entity nextPathFinder(String current, String[] strings, Directions oldDir) {
		//lista dei possibili path
		List<Entity> l = Arrays.stream(strings).map(x -> findPath(x)).filter(x -> x != null).sorted(Comparator.comparing(x -> Directions.valueOf((String) ((Entity)x).getPropertyOptional("direzione").get()).equals(oldDir) ? 0 : 1)).collect(Collectors.toList());
		Random r = new Random();
		if(l.isEmpty()) return null;
		int i = r.nextInt(l.size() + 3);
		return l.get(i > l.size() - 1 ? 0 : i);
	}
	
	public Music getGameMusic() {
		return this.gameMusic;
	}
	
	@Override
	protected void initGameVars(Map<String, Object> vars) {
	    vars.put("score", 0);
	}
}
package it.uniroma1.metodologie.trafficGame;

import java.util.List;
import java.util.Random;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.ui.FXGLCheckBox;

import it.uniroma1.metodologie.trafficGame.components.CrossRoadComponent;
import it.uniroma1.metodologie.trafficGame.components.PathComponent;
import it.uniroma1.metodologie.trafficGame.components.PlayerAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.SpawnPointComponent;
import it.uniroma1.metodologie.trafficGame.components.TrafficLightAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.VehicleComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class TrafficFactory implements EntityFactory{
	
	private Entity player1;
	
	private Entity player2;
	
	@Spawns("player")
	public Entity getPlayer1(SpawnData data) {
		if(data.get("player").equals("player1")) {
			if(player1 == null)
				player1 = FXGL.entityBuilder()
				.at(data.getX(),data.getY())
				.type(EntityType.PLAYER)
				.with("pointerX", 0)
				.with("pointerY", 0)
				.with(new PlayerAnimationComponent())
				.build();
			return player1;
		}
		if(player2 == null)
			player2 = FXGL.entityBuilder()
			.type(EntityType.PLAYER)
			.at(data.getX(),data.getY())
			.with("pointerX", 0)
			.with("pointerY", 0)
			.viewWithBBox(new Rectangle(250,250, Color.RED))
			.build();
		return player2;
	}
	
	private Entity build(SpawnData data, Vehicle v) {
		//TODO a method that spawns cars, tirs and motorbikes based on the data passed (heigh, width, direction..
		Entity spawn = data.get("spawn");
		Entity e = FXGL.entityBuilder(data)
					.type(EntityType.VEHICLE)
					.collidable()
					.with(new VehicleComponent(v,data.<Directions>get("direction"), TrafficApp.pathChooser(FXGL.getGameWorld().getEntitiesByType(EntityType.PATH).stream().filter(x -> spawn.isColliding(x)).findFirst().get(), v.canTurn()))/*create a path based on the vehicle*/)
					.viewWithBBox(v.getShape())
					.rotate(data.<Directions>get("direction").getStartingRotation())
					.build();
		
		HitBox h = new HitBox("EYE", BoundingShape.box(v.getWidth() + vehicleHitboxLen, v.getHeigh()));
		e.translate(-v.getWidth()/2, -v.getHeigh()/2);
		e.getBoundingBoxComponent().addHitBox(h);
		
		//adding arrows
		Rectangle f1 = new Rectangle(v.getWidth()-10, 0, 10, 10);
		f1.setFill(Color.YELLOW);
		f1.setVisible(false);
		Rectangle f2 = new Rectangle(v.getWidth()-10, v.getHeigh() - 10, 10, 10);
		f2.setFill(Color.YELLOW);
		f2.setVisible(false);
		e.getViewComponent().addChild(f1);
		e.getViewComponent().addChild(f2);
		e.getComponent(VehicleComponent.class).addArrows(f1, f2);
		
		return e;
	}
	
	
	private int vehicleHitboxLen = 40;
	
	/**
	 * spawns a random vehicle at the point indicated by data
	 * @param data
	 * @return
	 */
	@Spawns("vehicle")
	public Entity getVehicle(SpawnData data) {
		Entity vehicle;
		int random = new Random().nextInt(10);
		if(random <= 1 && (boolean) data.get("tir"))
			vehicle = build(data, Vehicle.TIR);
		else if(random <= 3)
			vehicle = build(data, Vehicle.MOTORBIKE);
		else
			vehicle = build(data, Vehicle.CAR);
		Rectangle s = (Rectangle) vehicle.getComponent(VehicleComponent.class).getVehicle().getShape();
		vehicle.setLocalAnchor(new Point2D(s.getWidth()/2, s.getHeight()/2 ));

		return vehicle;
	}
	
	@Spawns("semaforo")
	public Entity getSemaforo(SpawnData data) {
		
		int i = (int) data.getData().getOrDefault("rotation", 1);
		
		Entity e = FXGL.entityBuilder(data)
				.type(EntityType.SEMAFORO)
				.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"),data.<Integer>get("height"))))
				.with(new TrafficLightAnimationComponent(i))
				.with(new CollidableComponent(true))
				.build();
		
		e.setProperty("direction", Directions.getDirectionFromRotation(i));
		
		Point2D p = getPoint(i);
		p.add(data.getX(), data.getY());
		
		HitBox h = new HitBox(p, BoundingShape.box(40, 40));

		e.getBoundingBoxComponent().addHitBox(h);
		
		return e;
	}
	
	private Point2D getPoint(int i) {
		switch(i) {
		case 1:
			return new Point2D(295,210);
		case 2:
			return new Point2D(0, 295);
		case 3:
			return new Point2D(-80, 0);
		default:
			return new Point2D(210, -80);
		}
	}
	
	@Spawns("incrocioA4")
	public Entity getIncrocioA4(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.INCROCIO)
					.bbox(new HitBox(BoundingShape.box(250, 250)))
					.with(new CrossRoadComponent())
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("incrocioA3")
	public Entity getIncrocioA3(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.INCROCIO)
					.bbox(new HitBox(BoundingShape.box(250, 250)))
					.with("direzione", data.get("direzione"))
					.with(new CollidableComponent(true))
					.with(new CrossRoadComponent())
					.build();
	}
	
	@Spawns("spawn")
	public Entity getSpawn(SpawnData data) {
		Entity e =  FXGL.entityBuilder(data)
				.type(EntityType.SPAWN)
				.with(new SpawnPointComponent())
				.collidable()
				.build();
		List<Object> list = calcOffsetSpawn(data.get("direzione"));
		HitBox h = new HitBox((Point2D) list.get(0), (BoundingShape) list.get(1));
		//HitBox h = new HitBox(BoundingShape.polygon(new Point2D(-45,-45), new Point2D(-45, 45), new Point2D(45,45), new Point2D(45, -45)));
		e.getBoundingBoxComponent().addHitBox(h);
		return e;
	}
	
	private final int SPAWN_WIDTH = 100;
	private List<Object> calcOffsetSpawn(String d) {
		switch(Directions.valueOf(d)) {
		case UP:return List.of(new Point2D(-5, -SPAWN_WIDTH),BoundingShape.box(10, SPAWN_WIDTH));
		case DOWN:return List.of(new Point2D(-5, 0),BoundingShape.box(10, SPAWN_WIDTH));
		case LEFT:return List.of(new Point2D(-SPAWN_WIDTH, -5),BoundingShape.box(SPAWN_WIDTH, 10));
		case RIGHT:return List.of(new Point2D(0, -5),BoundingShape.box(SPAWN_WIDTH, 10));
		}
		return null;
	}

	@Spawns("path")
	public Entity getPath(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.PATH)
					.with(new CollidableComponent(true))
					.opacity(0)
					.viewWithBBox(new Rectangle((int)data.get("width") == 0 ? 4 : (int)data.get("width"), (int)data.get("height") == 0 ? 4 : (int)data.get("height")))
					//.bbox(new HitBox(BoundingShape.chain(new Point2D((float)data.getX(),(float)data.getY()), new Point2D(data.getX() + (int)data.get("width"),data.getY() + (int)data.get("height")))))
					//.with("direzione", data.get("direzione"))
					.with(new PathComponent())
					.build();
					
	}
}

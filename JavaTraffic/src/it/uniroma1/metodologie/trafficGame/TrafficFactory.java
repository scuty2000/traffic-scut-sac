package it.uniroma1.metodologie.trafficGame;

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

import it.uniroma1.metodologie.trafficGame.components.PlayerAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.TrafficLightAnimationComponent;
import it.uniroma1.metodologie.trafficGame.components.VehicleComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import tutorial.AndreaGameApp.EntityType;

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
		//TODO a method that spawns cars, tirs and motorbikes based on the data passed (heigh, width, direction...)
		Entity e = FXGL.entityBuilder(data)
					.type(EntityType.VEHICLE)
					.collidable()
					.with(new VehicleComponent(v,data.<Directions>get("direction"), data.get("pathList")))
					.viewWithBBox(v.getShape())
					//.atAnchored(new Point2D(1000,1000), new Point2D(1000,1000))//new Point2D(data.getX() + v.getWidth()/2, data.getY() + v.getHeigh()/2), new Point2D(data.getX() + v.getWidth()/2, data.getY() + v.getHeigh()/2))
					.rotate(data.<Directions>get("direction").getStartingRotation())
					.build();
		
		HitBox h = new HitBox("EYE", BoundingShape.box(v.getWidth() + 20, v.getHeigh()));
		
		//HitBox frontHitBox = new HitBox("FRONT", BoundingShape.box(v.getWidth() + 30, v.getHeigh()+10));
		
		e.getBoundingBoxComponent().addHitBox(h);
		//e.getBoundingBoxComponent().addHitBox(frontHitBox);
		return e;
	}
	
	
	/**
	 * spawns a random vehicle at the point indicated by data
	 * @param data
	 * @return
	 */
	@Spawns("vehicle")
	public Entity getVehicle(SpawnData data) {
		Entity vehicle;
		int random = new Random().nextInt(7);
		if(random <= 3)
			vehicle = build(data, Vehicle.CAR);
		else if(random <= 5)
			vehicle = build(data, Vehicle.MOTORBIKE);
		else
			vehicle = build(data, Vehicle.TIR);
			Rectangle s = (Rectangle) vehicle.getComponent(VehicleComponent.class).getVehicle().getShape();
		vehicle.setAnchoredPosition(vehicle.getPosition().subtract(s.getWidth()/2, s.getHeight()/2 ));
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
		
		HitBox h = new HitBox(p, BoundingShape.box(70, 70));

		e.getBoundingBoxComponent().addHitBox(h);
		
		return e;
	}
	
	private Point2D getPoint(int i) {
		switch(i) {
		case 1:
			return new Point2D(250,180);
		case 2:
			return new Point2D(0,250);
		case 3:
			return new Point2D(-70, 0);
		default:
			return new Point2D(180, -70);
		}
	}
	
	@Spawns("incrocioA4")
	public Entity getIncrocioA4(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.INCROCIO)
					.bbox(new HitBox(BoundingShape.box(250, 250)))
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
					.build();
	}
	
	@Spawns("spawn")
	public Entity getSpawn(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.SPAWN)
				.bbox(new HitBox(BoundingShape.box(0, 0)))
				.collidable()
				.build();
	}
	
	@Spawns("path")
	public Entity getPath(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.PATH)
					.with(new CollidableComponent(true))
					.opacity(0.3)
					.viewWithBBox(new Rectangle((int)data.get("width") == 0 ? 4 : (int)data.get("width"), (int)data.get("height") == 0 ? 4 : (int)data.get("height")))
					//.bbox(new HitBox(BoundingShape.chain(new Point2D((float)data.getX(),(float)data.getY()), new Point2D(data.getX() + (int)data.get("width"),data.getY() + (int)data.get("height")))))
					//.with("direzione", data.get("direzione"))
					.build();
					
	}

}

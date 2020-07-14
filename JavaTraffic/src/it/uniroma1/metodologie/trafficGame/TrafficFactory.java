package it.uniroma1.metodologie.trafficGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import javafx.scene.paint.Color;

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
				.viewWithBBox(new Rectangle(250,250, Color.YELLOW))
				.build();
			return player1;
		}
		if(player2 == null)
			player2 = FXGL.entityBuilder()
			.type(EntityType.PLAYER)
			.at(data.getX(),data.getY())
			.viewWithBBox(new Rectangle(250,250, Color.RED))
			.build();
		return player2;
	}
	
	private Entity build(SpawnData data, Vehicle v) {
		//TODO a method that spawns cars, tirs and motorbikes based on the data passed (heigh, wdth, direction...)
		v.setDirection(Directions.valueOf((data.get("direction"))));
		return FXGL.entityBuilder(data)
					.type(EntityType.VEHICLE)
					.with("vehicle", v)
					.collidable()
					.viewWithBBox(v.getShape())
					.build();
	}
	
	
	/**
	 * spawns a random vehicle at the point indicated by data
	 * @param data
	 * @return
	 */
	@Spawns("vehicle")
	public Entity getVehicle(SpawnData data) {
		Entity vehicle;
		int random = (int)Math.random() * 7;
		if(random <= 3)
			vehicle = build(data, Vehicle.CAR);
		else if(random <= 5)
			vehicle = build(data, Vehicle.MOTORBIKE);
		else
			vehicle = build(data, Vehicle.TIR);
		return vehicle;
	}
	
	@Spawns("semaforo")
	public Entity getSemaforo(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.SEMAFORO)
					.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"),data.<Integer>get("height"))))
					.view("semaforoRosso-01.png")
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("incrocioA4")
	public Entity getIncrocioA4(SpawnData data) {
		return FXGL.entityBuilder()
					.type(EntityType.INCROCIO)
					.bbox(new HitBox(BoundingShape.box(250, 250)))
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("incrocioA3")
	public Entity getIncrocioA3(SpawnData data) {
		return FXGL.entityBuilder()
					.type(EntityType.INCROCIO)
					.bbox(new HitBox(BoundingShape.box(250, 250)))
					.with("direzione", data.get("direzione"))
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("spawn")
	public Entity getSpawn(SpawnData data) {
		return FXGL.entityBuilder(data)
					.build();
	}

}

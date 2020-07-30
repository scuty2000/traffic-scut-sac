package tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import tutorial.AndreaGameApp.EntityType;

public class AndreaFactory implements EntityFactory{
	
	private Entity player;
	
	@Spawns("semaforo")
	public Entity getSemaforo(SpawnData data) {
		return FXGL.entityBuilder(data)
					.type(EntityType.SEMAFORO)
					.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"),data.<Integer>get("height"))))
					.view("semaforoRosso-01.png")
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("player")
	public Entity getPlayer(SpawnData data) {
		if(player == null)
			player = FXGL.entityBuilder()
					.type(EntityType.PLAYER)
					.at(100, 100)
					.with(new AndreaSemaforoComponent())
					.with(new CollidableComponent(true))
					.build();
		return player;
	}
	
	@Spawns("incrocioA4")
	public Entity getIncrocioA4(SpawnData data) {
		return FXGL.entityBuilder().bbox(new HitBox(BoundingShape.box(250, 250))).with(new CollidableComponent(true)).build();
	}
	
	@Spawns("incrocioA3")
	public Entity getIncrocioA3(SpawnData data) {
		return FXGL.entityBuilder()
					.bbox(new HitBox(BoundingShape.box(250, 250)))
					.with("direzione", data.get("direzione"))
					.with(new CollidableComponent(true))
					.build();
	}
	
	@Spawns("spawn")
	public Entity getSpawn(SpawnData data) {
		return FXGL.entityBuilder(data).build();
	}
}

package tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;

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
					.view("semaforoRosso-01.png")
					.with(new CollidableComponent(true))
					.bbox(new HitBox(BoundingShape.box(50,50)))
					.build();
		return player;
	}
	
}

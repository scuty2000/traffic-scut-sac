package tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;

public class BasicGameFactory implements EntityFactory {

	@Spawns("roadBorder")
	public Entity newRoadBorder(SpawnData data) {	
		return FXGL.entityBuilder()
		.from(data)
		.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
		.with(new PhysicsComponent())
		.build();
	}
	
}

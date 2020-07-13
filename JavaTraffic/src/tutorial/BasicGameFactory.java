package tutorial;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BasicGameFactory implements EntityFactory {

	@Spawns("roadBorder")
	public Entity newRoadBorder(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(BasicGameType.WALL)
			.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
			.with(new PhysicsComponent())
            .build();
	}
	
}

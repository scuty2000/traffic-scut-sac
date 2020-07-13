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
	public Entity newRB(SpawnData data) {	
		PhysicsComponent physics = new PhysicsComponent();
		
		Entity entity = FXGL.entityBuilder()
		.from(data)
		.bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
		.build();
		
		FixtureDef fd = new FixtureDef();
		fd.setDensity(0.7f);
		fd.setRestitution(0.3f);
		physics.setFixtureDef(fd);
		
		entity.addComponent(physics);
		return entity;
	}
	
}

package it.uniroma1.metodologie.trafficGame.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.util.Duration;

public class PlayerAnimationComponent extends Component {

	private AnimatedTexture texture;
	private AnimationChannel animationSprite;
	
	public PlayerAnimationComponent() {
		
		animationSprite = new AnimationChannel(FXGL.image("pointer-sprite.png"), 9, 250, 250, Duration.seconds(1), 0, 8);
		texture = new AnimatedTexture(animationSprite);
		texture.loopAnimationChannel(animationSprite);
		
	}
	
	@Override
	public void onAdded() {
		entity.getViewComponent().addChild(texture);
	}
}

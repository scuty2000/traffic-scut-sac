package it.uniroma1.metodologie.trafficGame.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.util.Duration;

/**
 * This class represents the Animation Component
 * of the player pointer.
 */
public class PlayerAnimationComponent extends Component {

	/**
	 * This is the texture of the pointer
	 */
	private AnimatedTexture texture;
	/**
	 * This is the animation channel of the sprite
	 */
	private AnimationChannel animationSprite;
	
	/**
	 * This starts the animation
	 */
	public PlayerAnimationComponent() {
		animationSprite = new AnimationChannel(FXGL.image("pointer-sprite.png"), 9, 250, 250, Duration.seconds(1), 0, 8);
		texture = new AnimatedTexture(animationSprite);
		texture.loopAnimationChannel(animationSprite);
	}
	
	/**
	 * This adds the animation to the view
	 * component of the player
	 */
	@Override
	public void onAdded() {
		entity.getViewComponent().addChild(texture);
	}
}

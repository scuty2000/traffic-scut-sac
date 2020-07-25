package it.uniroma1.metodologie.trafficGame.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

public class TrafficLightAnimationComponent extends Component {

	private boolean isRed = true;
	private boolean toSwitch = false;
	
	private AnimatedTexture texture;
	private AnimationChannel animRed, animGreen;
	
	public TrafficLightAnimationComponent() {
		animRed = new AnimationChannel(FXGL.image("tile2.png"), 1, 250, 250, Duration.seconds(1), 1, 1);
		animGreen = new AnimationChannel(FXGL.image("tiles_sheet.png"), 1, 250, 250, Duration.seconds(1), 1, 1);
		
		texture = new AnimatedTexture(animRed);
	}
	
	@Override
	public void onAdded() {
		entity.getTransformComponent().setScaleOrigin(new Point2D(16,21));
		entity.getViewComponent().addChild(texture);
	}
	
	@Override
	public void onUpdate(double tpf) {
		if(isRed && toSwitch) {
			texture.loopAnimationChannel(animGreen);
			//System.out.println("toSwitch: "+toSwitch+". AnimationChannel: "+texture.getAnimationChannel()+".");
			isRed = false;
		} else if (!isRed && toSwitch) {
			texture.loopAnimationChannel(animRed);
			//System.out.println("toSwitch: "+toSwitch+". AnimationChannel: "+texture.getAnimationChannel()+".");
			isRed = true;
		}
		
		toSwitch = false;
	}
	
	public void switchLight() {
		toSwitch = true;
	}
	
}

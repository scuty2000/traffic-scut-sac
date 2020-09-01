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
	
	/*
	 * this field is false if a car is turning left on the other side of the cross. It becomes true when the car has finished turning
	 */
	private boolean canPass = true;
	
	private AnimatedTexture texture;
	private AnimationChannel animRed, animGreen;
	
	public TrafficLightAnimationComponent(int tileID) {
		
		// parameters: sprite sheet image, number of frames per row, single frame width, single frame height, duration of the animation channel, start frame and end frame (c'ho messo un ora a capirlo dio santo non è scritto da nessuna parte)
		animRed = new AnimationChannel(FXGL.image("semaforoRosso-0"+tileID+".png"), 1, 250, 250, Duration.seconds(1), 0, 0);
		animGreen = new AnimationChannel(FXGL.image("semaforoVerde-0"+tileID+".png"), 1, 250, 250, Duration.seconds(1), 0, 0);
		
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
	
	public boolean isRed() {
		return isRed;
	}
	
	/*
	 * returns true if the light is green and no car is turning left on the other side of the cross
	 */
	public boolean isGreen() {
		return !isRed && canPass;
	}
	
	public void waitACar() {
		canPass = false;
	}
	
	public void canPass() {
		canPass = true;
	}
	
	public void switchLight() {
		toSwitch = true;
	}
	
}

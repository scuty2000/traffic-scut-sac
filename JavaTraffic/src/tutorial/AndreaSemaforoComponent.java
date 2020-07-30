package tutorial;

import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class AndreaSemaforoComponent extends Component {
		
	private AnimatedTexture texture;
	
	private AnimationChannel red,green,current;
	
	public AndreaSemaforoComponent() {
		red = new AnimationChannel(new Image("assets\\semaforoRosso-01.png"),1,250,250,Duration.seconds(1),1,1);
		green = new AnimationChannel(new Image("assets\\semaforoVerde-01.png"),1,250,250,Duration.seconds(1),1,1);
		texture = new AnimatedTexture(red);
		current = red;
	}
	
	 @Override
	    public void onAdded() {
		 	entity.getViewComponent().addChild(texture);
	    }
	 
	 @Override
	    public void onUpdate(double tpf) {
		 texture.loopAnimationChannel(current);
	 }
	
	
	public void setColour() {
		current = texture.getAnimationChannel().equals(red) ? green : red;
		texture.loopAnimationChannel(current == red ? green : red);
	}
	
	public String getColour() {
		return current == red? "red" : "green";
	}
}

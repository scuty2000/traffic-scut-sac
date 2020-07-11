package tutorial;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.util.Map;

public class BasicGameApp extends GameApplication {

	/**
	 * We declare a generic "player" Entity.
	 */
	private Entity player;
	
	/**
	 * We use this method to change the game settings.
	 * Be aware, this cannot be tweaked during runtime.
	 */
	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(800);
	    settings.setHeight(600);
	    settings.setTitle("  Basic Game App  ");
	    settings.setVersion("0.1");
	    settings.setDeveloperMenuEnabled(true);
	    settings.setProfilingEnabled(true);
	}
	
	/**
	 * Here we setup everything that needs to be ready
	 * before the game starts.
	 */
	@Override
	protected void initGame() {
		
		FXGL.entityBuilder()
		.at(100, 100)
		.view("gioco-02.png")
		.scale(0.40, 0.40)
		.buildAndAttach();
		FXGL.entityBuilder()
		.at(200, 100)
		.view("gioco-02.png")
		.scale(0.40, 0.40)
		.buildAndAttach();
		FXGL.entityBuilder()
		.at(300, 100)
		.view("gioco-02.png")
		.scale(0.40, 0.40)
		.buildAndAttach();
		
		/**
		 * We use entityBuilder to create our entity(s).
		 */
		player = FXGL.entityBuilder()
				.at(100, 100)
				.view(new Rectangle(10,10, Color.ORANGE))
				.buildAndAttach();
		
	}
	
	/**
	 * We use this method to handle input handling code.
	 */
	@Override
	protected void initInput() {
		Input input = FXGL.getInput();
		int width = FXGL.getSettings().getWidth();
		int height = FXGL.getSettings().getHeight();
		
		input.addAction(new UserAction("Move Right") {
			@Override
			protected void onAction() {
				if(player.getX() < width-25) {
					//FXGL.entityBuilder()
					//.at(player.getX(), player.getY())
					//.view(new Rectangle(5,5, Color.BLUE))
					//.buildAndAttach();
					player.translateX(5);
					FXGL.getWorldProperties().increment("pixelsMoved", +5);
					FXGL.getWorldProperties().increment("userX", +5);
				}
			}
		}, KeyCode.D);
		
		input.addAction(new UserAction("Move Left") {
	        @Override
	        protected void onAction() {
	        	if(player.getX() > 4) {
	        		//FXGL.entityBuilder()
					//.at(player.getX(), player.getY())
					//.view(new Rectangle(5,5, Color.BLUE))
					//.buildAndAttach();
	        		player.translateX(-5);
	        		FXGL.getWorldProperties().increment("pixelsMoved", +5);
	        		FXGL.getWorldProperties().increment("userX", -5);
	        	}
	        }
	    }, KeyCode.A);

	    input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	        	if(player.getY() > 4) {
	        		//FXGL.entityBuilder()
					//.at(player.getX(), player.getY())
					//.view(new Rectangle(5,5, Color.BLUE))
					//.buildAndAttach();
	        		player.translateY(-5);
	        		FXGL.getWorldProperties().increment("pixelsMoved", +5);
	        		FXGL.getWorldProperties().increment("userY", -5);
	        	}
	        }
	    }, KeyCode.W);

	    input.addAction(new UserAction("Move Down") {
	        @Override
	        protected void onAction() {
	        	if(player.getY() < height-25) {
	        		//FXGL.entityBuilder()
					//.at(player.getX(), player.getY())
					//.view(new Rectangle(5,5, Color.BLUE))
					//.buildAndAttach();
	        		player.translateY(5);
	        		FXGL.getWorldProperties().increment("pixelsMoved", +5);
	        		FXGL.getWorldProperties().increment("userY", +5);
	        	}
	        }
	    }, KeyCode.S);
	}
	
	/**
	 * We use this method, you may have guessed, to handle
	 * the UI control.
	 */
	@Override
	protected void initUI() {
		
		Text staticT = new Text();
		staticT.setTranslateX(50);
		staticT.setTranslateY(50);
		staticT.setText("T:");
		FXGL.getGameScene().addUINode(staticT);
		
		Text textPixels = new Text();
		textPixels.setTranslateX(65);
		textPixels.setTranslateY(50);
		textPixels.textProperty().bind(FXGL.getWorldProperties().intProperty("pixelsMoved").asString());
		FXGL.getGameScene().addUINode(textPixels);
		
		Text staticX = new Text();
		staticX.setTranslateX(50);
		staticX.setTranslateY(70);
		staticX.setText("X:");
		FXGL.getGameScene().addUINode(staticX);
		
		Text userX = new Text();
		userX.setTranslateX(65);
		userX.setTranslateY(70);
		userX.textProperty().bind(FXGL.getWorldProperties().intProperty("userX").asString());
		FXGL.getGameScene().addUINode(userX);
		
		Text staticY = new Text();
		staticY.setTranslateX(50);
		staticY.setTranslateY(90);
		staticY.setText("Y:");
		FXGL.getGameScene().addUINode(staticY);
		
		Text userY = new Text();
		userY.setTranslateX(65);
		userY.setTranslateY(90);
		userY.textProperty().bind(FXGL.getWorldProperties().intProperty("userY").asString());
		FXGL.getGameScene().addUINode(userY);
	}
	
	/**
	 * We use this method as a "global variable" that is
	 * tied to the BasicGameApp instance.
	 */
	@Override
	protected void initGameVars(Map<String, Object> vars) {
	    vars.put("pixelsMoved", 0);
	    vars.put("userX", 100);
	    vars.put("userY", 100);
	}
	
	/**
	 * This method is just the main and launches the game app.
	 * @param args
	 */
	public static void main(String[] args) {
	    launch(args);
	}
	
}

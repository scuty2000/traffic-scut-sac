package tutorial;

import java.nio.file.Paths;
import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class AndreaGameApp extends GameApplication{
	
	public enum EntityType{
		PLAYER,COIN
	}
	
	/*
	 * this i sthe field where the player's info are stored.
	 * It will be initialized in the initSettings() method
	 */
	private Entity player;
	
	
	/*
	 * Every application in FXGL needs the method initSettings.
	 * This method is used to initialize settings.
	 * Once they are set, the settings cannot be changed 
	 * during runtime.
	 */
	@Override
	protected void initSettings(GameSettings arg0) {
		arg0.setWidth(600);
		arg0.setHeight(600);
		arg0.setTitle("AndreaGameApp");
		arg0.setVersion("0.1");
	}

	@Override
	protected void initGame() {
		player = FXGL.entityBuilder()
				.type(EntityType.PLAYER)
				.at(100, 100)
				//.view(new Rectangle(10,10, Color.BLACK))
				.with(new CollidableComponent(true))
				.viewWithBBox("gioco-01.png")
				.buildAndAttach();
		
		
		FXGL.entityBuilder()
			.type(EntityType.COIN)
			.at(500,200)
			.viewWithBBox(new Circle(15,15,15,Color.YELLOW))
			.with(new CollidableComponent(true))
			.buildAndAttach();
	}
	
	
	@Override
	protected void initPhysics() {
	    FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {

	        // order of types is the same as passed into the constructor
	        @Override
	        protected void onCollisionBegin(Entity player, Entity coin) {
	            coin.removeFromWorld();
	        }
	    });
	}
	
	@Override
	protected void initInput() {
	    Input input = FXGL.getInput();

	    input.addAction(new UserAction("Move Right") {
	        @Override
	        protected void onAction() {
	            player.translateX(5); // move right 5 pixels
	            
	            FXGL.getGameState().increment("pixelMoved" , +5);
	        }
	    }, KeyCode.D);

	    input.addAction(new UserAction("Move Left") {
	        @Override
	        protected void onAction() {
	            player.translateX(-5); // move left 5 pixels
	            
	            FXGL.getGameState().increment("pixelMoved" , +5);
	        }
	    }, KeyCode.A);

	    input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	            player.translateY(-5); // move up 5 pixels
	            
	            FXGL.getGameState().increment("pixelMoved" , +5);
	        }
	    }, KeyCode.W);

	    input.addAction(new UserAction("Move Down") {
	        @Override
	        protected void onAction() {
	            player.translateY(5); // move down 5 pixels
	            
	            FXGL.getGameState().increment("pixelMoved" , +5);
	        }
	    }, KeyCode.S);
	}
	
	
	@Override
	protected void initUI() {
		
		Text textPixels = new Text();
		
		textPixels.textProperty().bind(FXGL.getGameState().intProperty("pixelMoved").asString());
		textPixels.setTranslateX(50);
		textPixels.setTranslateY(100);
		
		FXGL.getGameScene().addUINode(textPixels);
		
		Texture testTexture = FXGL.getAssetLoader().loadTexture("gioco-01.png");
		testTexture.setTranslateX(200);
		testTexture.setTranslateY(200);
		FXGL.getGameScene().addUINode(testTexture);
	}
	
	protected void initGameVars(Map<String,Object> vars) {
		vars.put("pixelMoved", 0);
	}
		
	/**
	 * The main method is the method that launch the application.
	 * @param args the arguments to be used by the launch method
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}

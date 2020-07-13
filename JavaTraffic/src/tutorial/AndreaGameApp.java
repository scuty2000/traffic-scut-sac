package tutorial;

import java.nio.file.Paths;
import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class AndreaGameApp extends GameApplication{

	public enum EntityType{
		PLAYER,COIN,WALL,SEMAFORO
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
		arg0.setWidth(3000);
		arg0.setHeight(3000);
		arg0.setTitle("AndreaGameApp");
		arg0.setVersion("0.1");
	}

	@Override
	protected void initGame() {

		//Creating the player
		
		
		FXGL.getGameWorld().addEntityFactory(new AndreaFactory());
		
		FXGL.setLevelFromMap("tutorialMap.tmx");
		
		player = FXGL.spawn("player");
		
		
		/*
		//creating a wall
		FXGL.entityBuilder()
		.type(EntityType.WALL)
		.at(500,300)
		.viewWithBBox("aa")
		.with(new CollidableComponent(true))
		.buildAndAttach();

		//creating a coin
		FXGL.entityBuilder()
		.type(EntityType.COIN)
		.at(500,200)
		.viewWithBBox(new Circle(15,15,15,Color.YELLOW))
		.with(new CollidableComponent(true))
		.buildAndAttach();
		*/
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


		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.SEMAFORO) {
			
			@Override
			protected void onCollisionBegin(Entity player, Entity semaforo) {
				player.getComponent(AndreaSemaforoComponent.class).setColour();
				System.out.println(player.getComponent(AndreaSemaforoComponent.class).getColour());
			}
			
		});
	}

	@Override
	protected void initInput() {
		Input input = FXGL.getInput();

		input.addAction(new UserAction("Move Right") {
			@Override
			protected void onAction() {

				player.translateX(+5);
				FXGL.getWorldProperties().increment("pixelMoved" , +5);
			}
		}, KeyCode.D);

		input.addAction(new UserAction("Move Left") {
			@Override
			protected void onAction() {
				player.translateX(-5);
				FXGL.getWorldProperties().increment("pixelMoved" , +5);
			}
		}, KeyCode.A);

		input.addAction(new UserAction("Move Up") {
			@Override
			protected void onAction() {
				player.translateY(-5);
				FXGL.getWorldProperties().increment("pixelMoved" , +5);
			}
		}, KeyCode.W);

		input.addAction(new UserAction("Move Down") {
			@Override
			protected void onAction() {
				player.translateY(+5);
				FXGL.getWorldProperties().increment("pixelMoved" , +5);
			}
		}, KeyCode.S);
	}


	@Override
	protected void initUI() {

		Text textPixels = new Text();

		textPixels.textProperty().bind(FXGL.getWorldProperties().intProperty("pixelMoved").asString());
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

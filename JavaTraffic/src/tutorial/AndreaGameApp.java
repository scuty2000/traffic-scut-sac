package tutorial;

import java.awt.Paint;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import com.sun.scenario.Settings;
import javafx.scene.shape.Rectangle;

public class AndreaGameApp extends GameApplication{
	
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
		arg0.setAppIcon("gioco-06.png");		
	}

	@Override
	protected void initGame() {
		player = FXGL.entityBuilder()
				.at(100, 100)
				.view(new Rectangle(10,10, Color.BLACK))
				.buildAndAttach();		
	}
	
	
	@Override
	protected void initInput() {
	    Input input = FXGL.getInput();

	    input.addAction(new UserAction("Move Right") {
	        @Override
	        protected void onAction() {
	            player.translateX(5); // move right 5 pixels
	        }
	    }, KeyCode.D);

	    input.addAction(new UserAction("Move Left") {
	        @Override
	        protected void onAction() {
	            player.translateX(-5); // move left 5 pixels
	        }
	    }, KeyCode.A);

	    input.addAction(new UserAction("Move Up") {
	        @Override
	        protected void onAction() {
	            player.translateY(-5); // move up 5 pixels
	        }
	    }, KeyCode.W);

	    input.addAction(new UserAction("Move Down") {
	        @Override
	        protected void onAction() {
	            player.translateY(5); // move down 5 pixels
	        }
	    }, KeyCode.S);
	}
	
	/**
	 * The main method is the method that launch the application.
	 * @param args the arguments to be used by the launch method
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}

package it.uniroma1.metodologie.trafficGame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.text.Text;

public class TrafficApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
    	settings.setDeveloperMenuEnabled(true); // Use 1 and 2 keys to access variables values and in-game console //TODO remove before production
    	settings.setVersion("Alpha 0.0"); // To update periodically
    	settings.setTitle("  Traffic  ");
    	settings.setWidth(800);
    	settings.setHeight(600);
    }

    @Override
    protected void initUI() {
        // 1. create any JavaFX or FXGL UI object
        Text uiText = new Text("Hello World!");

        // 2. add the UI object to game scene (easy way) at 100, 100
        FXGL.addUINode(uiText, 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
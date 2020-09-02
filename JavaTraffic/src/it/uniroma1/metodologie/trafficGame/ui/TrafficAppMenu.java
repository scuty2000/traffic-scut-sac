package it.uniroma1.metodologie.trafficGame.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class TrafficAppMenu extends FXGLMenu {

	public TrafficAppMenu(MenuType type) {
		super(type);
		
		TrafficButton btnPlayGame = new TrafficButton("Avvia una partita", () -> fireNewGame());
		TrafficButton btnOptions = new TrafficButton("Impostazioni", () -> {});
		TrafficButton btnQuit = new TrafficButton("Esci dal gioco", () -> fireExit());
		
		var titleBox = new VBox(FXGL.texture("logo.png"));
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setTranslateX(FXGL.getAppWidth()/2-1750/2);
		titleBox.setTranslateY(200);
		
		var box = new VBox(10, 
				btnPlayGame, 
				btnOptions,
				new TrafficButton("Extras", () -> {}),
				new TrafficButton("Crediti", () -> {}),
				btnQuit,
				new Text(""),
				new Text(""),
				new Separator(Orientation.HORIZONTAL),
				FXGL.getUIFactoryService().newText("Use arrows and enter to use buttons.", Color.BLACK, 40.0));
		box.setTranslateX(200);
		box.setTranslateY(1000);
		
		getContentRoot().getChildren().addAll(titleBox, box);
	}

	@Override
	protected Button createActionButton(String arg0, Runnable arg1) {
		return new Button();
	}

	@Override
	protected Button createActionButton(StringBinding arg0, Runnable arg1) {
		return new Button();
	}

	@Override
	protected Node createBackground(double arg0, double arg1) {
		
		Texture texture = FXGL.texture("null.png");
		texture.setFitHeight(FXGL.getAppHeight());
		texture.setFitWidth(FXGL.getAppWidth());
		
		return texture;
	}

	@Override
	protected Node createProfileView(String arg0) {
		return new Rectangle();
	}

	@Override
	protected Node createTitleView(String arg0) {
		return new Rectangle();
	}

	@Override
	protected Node createVersionView(String arg0) {
		return new Rectangle();
	}
	
	private static class TrafficButton extends StackPane {
		private static final Color FOCUSED_COLOR = Color.BLACK;
		private static final Color NOT_FOCUSED_COLOR = Color.GRAY;
		
		private String text;
		private Runnable action;
		
		private Text name;
		private Rectangle icon;
		
		public TrafficButton(String text, Runnable action) {
			this.text = text;
			this.action = action;
			
			name = FXGL.getUIFactoryService().newText(text, Color.GRAY, 45.0);
			icon = new Rectangle(15, 40, Color.BLACK);
			icon.setTranslateX(-60);
			icon.visibleProperty().bind(focusedProperty());
			
			name.fillProperty().bind(
					
					Bindings.when(focusedProperty()).then(FOCUSED_COLOR).otherwise(NOT_FOCUSED_COLOR)
					
					);
			
			setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.ENTER) {
					action.run();
				}
			});
			
//			setOnMouseClicked(e -> {
//				action.run();
//			});
			
			setFocusTraversable(true);
			setAlignment(Pos.CENTER_LEFT);
			
			getChildren().addAll(icon, name);
		}
		
		
	}

}

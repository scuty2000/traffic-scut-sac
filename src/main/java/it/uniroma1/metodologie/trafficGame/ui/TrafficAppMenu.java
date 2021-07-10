package it.uniroma1.metodologie.trafficGame.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import it.uniroma1.metodologie.trafficGame.TrafficApp;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * This class represents the MainMenu used by TrafficApp.
 */
public class TrafficAppMenu extends FXGLMenu {
	
	/**
	 * Box that contains the bottons for the
	 * main menu.
	 */
	private VBox box;
	/**
	 * Box that contains the bottons for the
	 * main menu settings.
	 */
	private VBox settingsBox;
	/**
	 * Box that contains the bottons used to
	 * select game difficulty.
	 */
	private VBox difficultyBox;
	/**
	 * Box that contains the bottons for the
	 * map selection.
	 */
	private VBox mapBox;
	
	/**
	 * Buttons used to select the difficulty of
	 * the game.
	 */
	private TrafficButton easyBtn;
	private TrafficButton mediumBtn;
	private TrafficButton hardBtn;
	
	/**
	 * Main App.
	 */
	private TrafficApp app = (TrafficApp) FXGL.getApp();
	
	/**
	 * Translation factors for the menu elements.
	 */
	private final int xMenuPosition = 300;
	private final int yMenuPosition = 1200;
	
	/**
	 * Directory of maps.
	 */
	private File dir = new File("./assets/levels/");

	/**
	 * List of maps found in the directory.
	 */
	private List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles((dir, name) -> name.endsWith(".tmx"))));

	/**
	 * 
	 * This is the constructor of the menu.
	 * This creates all the buttons and Nodes,
	 * that are then collected and added to the
	 * content root. 
	 * 
	 * @param type
	 */
	public TrafficAppMenu(MenuType type) {
		super(type);
		TrafficButton btnPlayGame = new TrafficButton("Avvia una partita", () -> fireNewGame());
		TrafficButton btnOptions = new TrafficButton("Impostazioni", () -> toggleSettings());
		TrafficButton btnExtras = new TrafficButton("Extras", () -> {});
		TrafficButton btnCredits = new TrafficButton("Crediti", () -> {});
		TrafficButton btnQuit = new TrafficButton("Esci dal gioco", () -> fireExit());
		TrafficButton placeholder = new TrafficButton("", () -> {});
		placeholder.setFocusTraversable(false);
		TrafficButton placeholder2 = new TrafficButton("", () -> {});
		placeholder2.setFocusTraversable(false);
		TrafficButton placeholder3 = new TrafficButton("", () -> {});
		placeholder3.setFocusTraversable(false);
		TrafficButton placeholder4 = new TrafficButton("", () -> {});
		placeholder4.setFocusTraversable(false);
		TrafficButton placeholder5 = new TrafficButton("", () -> {});
		placeholder5.setFocusTraversable(false);
		
		btnExtras.setDisable(true);
		btnCredits.setDisable(true);
		
		Texture logo = FXGL.texture("logo.png");
		logo.setTranslateY(150);
		logo.setScaleX(0.8);
		logo.setScaleY(0.8);
		logo.setScaleZ(0.8);
		
		var titleBox = new VBox(logo);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setTranslateX(FXGL.getAppWidth()/2-1750/2);
		titleBox.setTranslateY(150);
		this.box = new VBox(10, 
				btnPlayGame, 
				btnOptions,
				btnExtras,
				btnCredits,
				btnQuit,
				new Text(""),
				new Separator(Orientation.HORIZONTAL),
				FXGL.getUIFactoryService().newText("Seleziona una voce del menù con le frecce direzionali e premi Invio.", Color.LIGHTGRAY, 20.0)
				);
		box.setTranslateX(xMenuPosition);
		box.setTranslateY(yMenuPosition);
		
		this.settingsBox = new VBox(10,
				new TrafficButton("Seleziona Mappa", () -> toggleMap()),
				new TrafficButton("Seleziona Difficoltà", () -> toggleDifficulty()),
				placeholder2,
				placeholder3,
				new TrafficButton("Indietro", () -> toggleSettings()),
				new Text(""),
				new Separator(Orientation.HORIZONTAL),
				FXGL.getUIFactoryService().newText("Modifica le impostazioni del gioco.", Color.LIGHTGRAY, 20.0)
				);
		
		this.settingsBox.setVisible(false);
		this.settingsBox.setTranslateX(xMenuPosition);
		this.settingsBox.setTranslateY(yMenuPosition);

		this.easyBtn = new TrafficButton("Facile", () -> setDifficulty("EASY"));
		this.mediumBtn = new TrafficButton("Normale", () -> setDifficulty("MEDIUM"));
		this.hardBtn = new TrafficButton("Difficile", () -> setDifficulty("HARD"));
		
		this.difficultyBox = new VBox(10,
				easyBtn,
				mediumBtn,
				hardBtn,
				placeholder,
				new TrafficButton("Indietro", () -> toggleDifficulty()),
				new Text(""),
				new Separator(Orientation.HORIZONTAL),
				FXGL.getUIFactoryService().newText("Seleziona il livello di difficoltà desiderato.", Color.LIGHTGRAY, 20.0)
				);
		
		this.difficultyBox.setVisible(false);
		this.difficultyBox.setTranslateX(xMenuPosition);
		this.difficultyBox.setTranslateY(yMenuPosition);
		
//		s = new Text("score : " + FXGL.getWorldProperties().getValueOptional("score").orElse(""));
//		s.setTranslateX(200);
//		s.setTranslateY(200);
//		s.setFont(Font.font(40));
//		s.setFill(Color.LIGHTGRAY);
		
		this.mapBox = new VBox(10);

		Collections.sort(files);
		
		for (File file : files) {
			mapBox.getChildren().add(new TrafficButton(file.getName().replace('_', ' ').replace(".tmx", ""), () -> setMap(file.getName())));
		}
		
		mapBox.getChildren().addAll(placeholder4,
				placeholder5,
				new TrafficButton("Indietro", () -> toggleMap()),
				new Text(""),
				new Separator(Orientation.HORIZONTAL),
				FXGL.getUIFactoryService().newText("Seleziona la mappa desiderata.", Color.LIGHTGRAY, 20.0));
		
		this.mapBox.setVisible(false);
		this.mapBox.setTranslateX(xMenuPosition);
		this.mapBox.setTranslateY(yMenuPosition);
		
		getContentRoot().getChildren().addAll(titleBox, box, settingsBox, difficultyBox, mapBox);
	}
	
	/**
	 * This sets the map to be used.
	 * @param file
	 */
	private void setMap(String file) {
		app.setMap(file);
		toggleMap();
		toggleSettings();
	}
	
	/**
	 * This toggles the setting secondary menu.
	 */
	private void toggleSettings() {
		this.box.setVisible(this.settingsBox.isVisible());
		this.settingsBox.setVisible(!this.box.isVisible());
	}
	
	/**
	 * This toggles the difficulty selection secondary menu.
	 */
	private void toggleDifficulty() {
		this.settingsBox.setVisible(this.difficultyBox.isVisible());
		this.difficultyBox.setVisible(!this.settingsBox.isVisible());
		if(this.difficultyBox.isVisible())
			focusSelectedOption();
	}
	
	/**
	 * This toggles the map selection secondary menu.
	 */
	private void toggleMap() {
		this.settingsBox.setVisible(this.mapBox.isVisible());
		this.mapBox.setVisible(!this.settingsBox.isVisible());
	}
	
	/**
	 * This sets the difficulty of the game choosen in
	 * TrafficApp.
	 * @param difficulty
	 */
	private void setDifficulty(String difficulty) {
		app.setMinSpawnRate(difficulty);
		focusSelectedOption();
	}
	
	/**
	 * This highlights the selected difficulty
	 * option in settings.
	 */
	private void focusSelectedOption() {
		switch(app.getMinSpawnRate()) {
			case "EASY":
				this.easyBtn.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2), null)));
				this.mediumBtn.setBorder(null);
				this.hardBtn.setBorder(null);
				break;
			case "MEDIUM":
				this.easyBtn.setBorder(null);
				this.mediumBtn.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2), null)));
				this.hardBtn.setBorder(null);
				break;
			case "HARD":
				this.easyBtn.setBorder(null);
				this.mediumBtn.setBorder(null);
				this.hardBtn.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2), null)));
				break;
			default:
				throw new IllegalArgumentException();
		};
	}
	
	@Override
	protected void onUpdate(double tpf) {
	}

	@Override
	protected Button createActionButton(String arg0, Runnable arg1) {
		return new Button();
	}

	@Override
	protected Button createActionButton(StringBinding arg0, Runnable arg1) {
		return new Button();
	}

	/**
	 * This sets the menu background.
	 */
	@Override
	protected Node createBackground(double arg0, double arg1) {
		Texture texture = FXGL.texture("bg.jpg");
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

	/**
	 * This method provides a banner warning about the alpha stage
	 * of this game.
	 */
	@Override
	protected Node createVersionView(String arg0) {
		Text text = FXGL.getUIFactoryService().newText("This game is in Alpha stage. Some bugs have to be expected.", Color.WHITE, 25.0);
		
		VBox versionBox = new VBox(
				text
				);
		
		versionBox.setAlignment(Pos.CENTER);
		
		versionBox.setMinWidth(FXGL.getAppWidth());
		
		versionBox.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, null)));
		
		return versionBox;
	}
	
	/**
	 * This class represents the button used in the menu
	 * and its style and actions.
	 */
	private class TrafficButton extends StackPane {
		/**
		 * This is the color used when the button is
		 * focused
		 */
		private final Color FOCUSED_COLOR = Color.WHITE;
		/**
		 * This is the color used when the button is
		 * not focused
		 */
		private final Color NOT_FOCUSED_COLOR = Color.LIGHTGRAY;
		
		/**
		 * This is the name of the button
		 */
		private Text name;
		/**
		 * This is the shape of the button
		 */
		private Rectangle icon;
		
		/**
		 * 
		 * This is the constructor of the button, that binds
		 * his color and visibility to other properties, like
		 * being focused or disabled/enabled.
		 * 
		 * It also executes the actions of the button.
		 * 
		 * @param text
		 * @param action
		 */
		public TrafficButton(String text, Runnable action) {
			name = FXGL.getUIFactoryService().newText(text, FOCUSED_COLOR, 40.0);
			icon = new Rectangle(7.5, 40, FOCUSED_COLOR);
			icon.setTranslateX(-30);
			icon.visibleProperty().bind(focusedProperty());
			
			name.fillProperty().bind(
					Bindings.when(focusedProperty()).then(FOCUSED_COLOR).otherwise(NOT_FOCUSED_COLOR)
					);

			setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.ENTER) {
					action.run();
				}
			});

			setOnMouseClicked(e -> {
				action.run();
			});
			
			setFocusTraversable(true);
			
			setAlignment(Pos.CENTER_LEFT);
			
			getChildren().addAll(icon, name);
		}
	}
}

package it.uniroma1.metodologie.trafficGame.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TrafficAppMenu extends FXGLMenu {
	
	private VBox box;
	private VBox settingsBox;
	private VBox difficultyBox;
	private VBox mapBox;
	
	private TrafficButton easyBtn;
	private TrafficButton mediumBtn;
	private TrafficButton hardBtn;
	
	private TrafficApp app = (TrafficApp) FXGL.getApp();
	
	private final int xMenuPosition = 200;
	private final int yMenuPosition = 750;
	
	private File dir = new File("src/assets/levels");
	private List<File> files = Arrays.asList(dir.listFiles((FilenameFilter) new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
	        return name.endsWith(".tmx");
	    }
	}));
	
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
		logo.setScaleX(0.5);
		logo.setScaleY(0.5);
		logo.setScaleZ(0.5);
		
		var titleBox = new VBox(logo);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setTranslateX(FXGL.getAppWidth()/2-1750/2);
		titleBox.setTranslateY(-100);
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
	
	private void setMap(String file) {
		app.setMap(file);
		toggleMap();
		toggleSettings();
	}

	
	private void toggleSettings() {
		this.box.setVisible(this.settingsBox.isVisible());
		this.settingsBox.setVisible(!this.box.isVisible());
	}
	
	private void toggleDifficulty() {
		this.settingsBox.setVisible(this.difficultyBox.isVisible());
		this.difficultyBox.setVisible(!this.settingsBox.isVisible());
		if(this.difficultyBox.isVisible())
			focusSelectedOption();
	}
	
	private void toggleMap() {
		this.settingsBox.setVisible(this.mapBox.isVisible());
		this.mapBox.setVisible(!this.settingsBox.isVisible());
	}
	
	private void setDifficulty(String difficulty) {
		app.setMinSpawnRate(difficulty);
		focusSelectedOption();
	}
	
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
	
	Text s;
	
	@Override
	protected void onUpdate(double tpf) {
		//((Text) getContentRoot().getChildren().stream().filter(x -> x == s).findFirst().orElseThrow()).setText("score : " + FXGL.getWorldProperties().getValueOptional("score").orElse(""));
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

	@Override
	protected Node createVersionView(String arg0) {
		Text text = FXGL.getUIFactoryService().newText("This game is in Alpha stage. A LOT of bugs have to be expected.", Color.WHITE, 10.0);
		
		VBox versionBox = new VBox(
				text
				);
		
		versionBox.setAlignment(Pos.CENTER);
		
		versionBox.setMinWidth(FXGL.getAppWidth());
		
		versionBox.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, null)));
		
		return versionBox;
	}
	
	private class TrafficButton extends StackPane {
		private final Color FOCUSED_COLOR = Color.WHITE;
		private final Color NOT_FOCUSED_COLOR = Color.LIGHTGRAY;
		
		private Text name;
		private Rectangle icon;
		
		public TrafficButton(String text, Runnable action) {
			name = FXGL.getUIFactoryService().newText(text, FOCUSED_COLOR, 25.0);
			icon = new Rectangle(7.5, 20, FOCUSED_COLOR);
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

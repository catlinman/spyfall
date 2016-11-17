package com.catlinman.spyfall;

// JavaFX imports.
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.Stage;

// Data file imports.
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

// Utility imports.
import java.util.ArrayList;
import java.util.HashMap;

// Main user interface application of this project.
public class Program extends Application {
	public static final boolean DEBUG   = true;  // If game information should be printed to the console.
	public static final boolean DEBUGUI = false; // If extra UI elements should be shown for debugging.

	// Constant application supported languages array.
	private static final String[] LANGUAGES = {
		"en",
		"de"
	};

	private static final String SEPARATOR  = ",";
	private static final String LOCALEFILE = "ui.csv";   // Localization filename. Lang prefix is prepended.
	private static HashMap<String, String> localization; // String map containing localization data.

	// TODO: These variables should be initialized through the user interface.
	public static String gamelang = "en"; // Game language static string.
	public static Game spyfall;           // The main game object.

	private ArrayList<TextField> playerFields;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Spyfall"); // Set the window title.

		spyfall = new Game(4, 5 * 60); // IDEA: Create a new game. Should be done in a handler later.

		// TODO: Create interface elements and fill in information.

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		for (int i = 0; i < 8; i++) {
			TextField tf = new TextField();
			tf.setPromptText("Player " + (i + 1));
			tf.setPrefColumnCount(16);
			tf.getText();
			// GridPane.setConstraints(tf, 0, 0);
			grid.getChildren().add(tf);
		}

		Label playerLabel    = new Label("Player setup");
		Label stopwatchLabel = new Label("Stopwatch settings");
		Label locationLabel  = new Label("Locations");

		Button startButton = new Button("Start game");
		Button pauseButton = new Button("Pause game");
		Button stopButton  = new Button("Stop game");

		// Text field element.
		Text text = new Text();
		text.setFont(new Font(20));
		text.setWrappingWidth(200);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setText(Long.toString(spyfall.getTimeLeft())); // FIXME: Testing line.

		StackPane root = new StackPane();
		root.getChildren().add(grid);
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();

		spyfall.start(); // Start the game of Spyfall. Should be handled separately later
	} /* start */

	@Override
	public void stop() {
		System.out.println("Game is closing..");
		spyfall.reset();
	}

	// Loads localized input data into the static data table used for locations and roles.
	private static void loadLocale(String lang) {
		// Fetch the resource location from the localized input file.
		String localepath = Program.class.getClassLoader().getResource(lang + "_" + LOCALEFILE).getPath();

		String content = ""; // Used as temporary storage for the input data.

		// Read the file contents with the right system encoding and combine it to a single string.
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(localepath));
			content = new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {}

		// Split each line into a new string.
		String[] lines = content.split("\n");

		// Initialize a new HashMap.
		localization = new HashMap<String, String>();

		if (DEBUG) System.out.println(lang.toUpperCase() + " UI CSV is loading.");

		// Iterate over each line. Split each at the separator. Clean up the string and insert it into the location data array.
		for (int i = 0; i < lines.length - 1; i++) {
			String[] fields = lines[i + 1].split(SEPARATOR, 2);
			try {
				localization.put(fields[0], fields[1].trim());

				if (DEBUG) System.out.println(i + ". " + fields[0] + " = " + fields[1].trim());

			} catch (IndexOutOfBoundsException e) {
				if (DEBUG) System.out.println(lang.toUpperCase() + " UI CSV: Line " + i
					  + " does not contain the right amount of fields (expected two fields).");
			}
		}
	} /* loadLocale */

	// Main program entry point.
	public static void main(String[] args) {
		loadLocale(gamelang);
		launch(args);
	}

}

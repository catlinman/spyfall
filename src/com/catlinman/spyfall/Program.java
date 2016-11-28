package com.catlinman.spyfall;

// JavaFX imports.
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Separator;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

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
	private static final String LOCALEFILE = "ui.csv"; // Localization filename. Lang prefix is prepended.

	private static String lang = "en";                   // Game language static string.
	private static HashMap<String, String> localization; // String map containing localization data.

	private static Game spyfall; // The main game object.

	// UI elements that require extra mangement should be prepared here.
	private ArrayList<TextField> playerFields = new ArrayList<TextField>();

	@Override
	public void start(Stage stage) {
		stage.setTitle("Spyfall"); // Set the window title.

		// Create the main stack and main scene.
		final StackPane mainStack = new StackPane();
		final Scene mainScene     = new Scene(mainStack, 800, 600);

		// Create the vertical alignment box which all elements will be stored in.
		final VBox verticalBox = new VBox(10);
		verticalBox.setPadding(new Insets(15, 10, 10, 10));
		verticalBox.setAlignment(Pos.TOP_CENTER);

		// Create the section heading and player text field grid.
		final Text playerText     = new Text(25, 25, "Player information");
		final GridPane playerGrid = new GridPane();

		playerGrid.setPadding(new Insets(10, 10, 10, 10));
		playerGrid.setVgap(10);
		playerGrid.setHgap(10);

		// Create the player fields.
		for (int i = 0; i < 8; i++) {
			TextField tf = new TextField();

			tf.setPromptText("Player " + (i + 1)); // Set the correct player id.
			tf.setPrefColumnCount(14);

			tf.addEventFilter(KeyEvent.KEY_TYPED, maxLength(20)); // Set the character limit listener (24).

			GridPane.setConstraints(tf, (int) Math.floor(i / 2), i % 2); // Create constraint based on player count.

			// Add the new text field to the grid and a list for easier management later on..
			playerGrid.getChildren().add(tf);
			playerFields.add(tf);
		}

		// Create the settings section heading.
		final Text settingsText = new Text(25, 25, "Game information");

		// Box for stopwatch setup and information.
		final HBox stopwatchBox = new HBox(10);
		stopwatchBox.setAlignment(Pos.CENTER);

		// Create the text and input field.
		final Text stopwatchText       = new Text("Stopwatch disabled");
		final TextField stopwatchInput = new TextField("5.0");

		// Add integer formatting to the textfield.
		final TextFormatter<Integer> intformatter = new TextFormatter<>(new IntegerStringConverter());
		stopwatchInput.setTextFormatter(intformatter);
		stopwatchInput.setPromptText("Seconds");
		stopwatchInput.setPrefWidth(60);
		stopwatchInput.setAlignment(Pos.CENTER);
		stopwatchInput.setDisable(true);
		stopwatchInput.addEventFilter(KeyEvent.KEY_TYPED, maxLength(6));

		final CheckBox stopwatchCheckBox = new CheckBox();

		stopwatchCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue< ? extends Boolean> ov,
			Boolean old_val, Boolean new_val) {
			    stopwatchText.setText(new_val ? "Stopwatch enabled" : "Stopwatch disabled");
			    stopwatchInput.setDisable(!new_val);
			}
		});

		// Add our items to the stopwatch box.
		stopwatchBox.getChildren().add(stopwatchCheckBox);
		stopwatchBox.getChildren().add(stopwatchText);
		stopwatchBox.getChildren().add(stopwatchInput);

		// Box for game information and stopwatch countdown. Is enabled after start.
		final HBox infoBox = new HBox(10);
		infoBox.setAlignment(Pos.CENTER);
		infoBox.setVisible(false);
		infoBox.setManaged(false);

		// Horizontal section box for all the buttons.
		final HBox buttonBox = new HBox(10);
		buttonBox.setPadding(new Insets(5, 10, 10, 10));
		buttonBox.setAlignment(Pos.CENTER);

		// Create the main game management buttons.
		final Button playButton  = new Button("Start game");
		final Button pauseButton = new Button("Pause game");
		final Button stopButton  = new Button("Stop game");

		// Disable buttons that don't have any use in the beginning.
		pauseButton.setDisable(true);
		stopButton.setDisable(true);

		// Assign all buttons to the button divider box.
		buttonBox.getChildren().add(playButton);
		buttonBox.getChildren().add(pauseButton);
		buttonBox.getChildren().add(stopButton);

		// Text header for the location section.
		final Text locationText     = new Text(25, 25, "Locations");
		final GridPane locationGrid = new GridPane();

		locationGrid.setPadding(new Insets(10, 10, 10, 10));
		locationGrid.setVgap(10);
		locationGrid.setHgap(10);

		// Get the locations from the loaded data.
		String[] locations = Location.getLocations();

		// Create the location fields.
		for (int i = 0; i < locations.length; i++) {
			// Truncate too long names.
			String n = locations[i].length() > 18 ? locations[i].substring(0, 18) : locations[i];

			// Create a box around the text to give the interface more structure.
			HBox box = new HBox();
			box.getChildren().add(new Text(25, 25, n));
			box.setStyle("-fx-border-color: gray; -fx-border-radius: 5 5 5 5;");
			box.setPadding(new Insets(5, 5, 5, 5));
			box.setAlignment(Pos.CENTER);

			// Constrain for a five column grid.
			GridPane.setConstraints(box, i % 5, (int) Math.floor(i / 5));

			// Add the new text field to the grid and a list for easier management later on..
			locationGrid.getChildren().add(box);
		}

		// Create alignment columns for all items.
		for (int i = 0; i < 5; i++) {
			ColumnConstraints column = new ColumnConstraints(145);
			locationGrid.getColumnConstraints().add(column);
		}

		// Separators. Used to split sections for easier overview.
		final Separator separator1 = new Separator();
		final Separator separator2 = new Separator();

		// Assign all elements to the vertical box we initially created.
		verticalBox.getChildren().add(playerText);
		verticalBox.getChildren().add(playerGrid);
		verticalBox.getChildren().add(separator1);
		verticalBox.getChildren().add(settingsText);
		verticalBox.getChildren().add(stopwatchBox);
		verticalBox.getChildren().add(infoBox);
		verticalBox.getChildren().add(buttonBox);
		verticalBox.getChildren().add(separator2);
		verticalBox.getChildren().add(locationText);
		verticalBox.getChildren().add(locationGrid);

		// Assign the vertical box to the main stack to have all elements wrap.
		mainStack.getChildren().add(verticalBox);

		StackPane.setAlignment(mainStack, Pos.TOP_CENTER);

		// Set the main scene.
		stage.setScene(mainScene);
		stage.show();
	} /* start */

	@Override
	public void stop() {
		System.out.println("Application is closing..");
		spyfall.reset();
	}

	public static String getLanguage() {
		return lang;
	}

	// Get the main game object.
	public static Game getGame() {
		return spyfall;
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

	// Key event handler for text boxes. Limits the amount of characters possible.
	public EventHandler<KeyEvent> maxLength(final Integer i) {
		return new EventHandler<KeyEvent>() {
				   @Override
				   public void handle(KeyEvent arg0) {
					   TextField tx = (TextField) arg0.getSource();

					   if (tx.getText().length() >= i) arg0.consume();
				   }

		};
	}

	// Main program entry point.
	public static void main(String[] args) {
		loadLocale(lang);

		// Create a new game of Spyfall. This also loads data for locations.
		spyfall = new Game();

		launch(args); // Start the main application.
	}

}

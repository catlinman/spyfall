package com.catlinman.spyfall;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import java.nio.file.NoSuchFileException;

// Main user interface application of this project.
public class Program extends Application {
    // Configuration file name and separator.
    private static final String CONFIGFILE  = "spyfall.cfg";
    private static final String CONFIGSPLIT = "=";

    // Configuration data.
    private static HashMap<String, String> CONFIG;

    // Base width and height values without taking location list in account.
    private static final int WIDTH  = 800;
    private static final int HEIGHT = 450;

    private static Game spyfall; // The main game object.

    private void readConfig() {
        // Initialize the configuration HashMap.
        CONFIG = new HashMap<String, String>();

        // Set default values.
        CONFIG.put("lang", "en");

        // Initialize the content string.
        String content = "";

        // Read the configuration file contents.
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("./" + CONFIGFILE));
            content = new String(encoded, Charset.defaultCharset());

        } catch (IOException e) {
            return;
        }

        if (Debug.DATA) System.out.println("Data: Loading Spyfall config file.");

        // Split each line into a new string.
        String[] lines = content.split("\n");

        // Iterate over lines and start inserting key & values into the HashMap.
        for (int i = 0; i < lines.length; i++) {
            String[] fields = lines[i].split(CONFIGSPLIT, 2);

            try {
                if (Debug.DATA) System.out.println(lines[i]);

                CONFIG.put(fields[0], fields[1].trim());

            } catch(IndexOutOfBoundsException e) {
                if (Debug.DATA) System.out.println(
                        "Data: Error in " + CONFIGFILE + " line " + i
                        + " does not contain the right amount of fields (expected two fields).");
            }
        }
    }

    private void writeConfig() {
        // Create a separate String for iterating of bytes.
        String writeString = "lang=" + Locale.getCurrent();

        // Write to the config file.
        try {
            Files.write(Paths.get("./" + CONFIGFILE), writeString.getBytes());

        } catch (IOException e) {
            if(Debug.DATA) e.printStackTrace();

            return;
        }

        if (Debug.DATA) System.out.println("Data: Wrote Spyfall config file.");
    }

    private void createHelpWindow() {
        // Prepare the new help window stage.
        final Stage helpStage = new Stage();

        // Set the correct window title.
        helpStage.setTitle("Spyfall - " + Locale.get("window-help-title"));

        // Create the help window stack stack.
        final StackPane helpStack = new StackPane();

        if (Debug.APP) System.out.println("Application: Showing game help window.");

        // Create a VBox for easier alignment.
        final VBox helpBox = new VBox(10);
        helpBox.setPadding(new Insets(10, 10, 10, 10));
        helpBox.setAlignment(Pos.TOP_CENTER);

        // Font used in this window.
        final Font headerFont = Font.font(null, 20);
        final Text helpHeader = new Text(Locale.get("window-help-header"));
        helpHeader.setFont(headerFont);

        // Split the main text into two parts and make sure they wrap and align correctly.
        final Text helpText1 = new Text(Locale.get("window-help-text1"));
        helpText1.setWrappingWidth(430);
        helpText1.setTextAlignment(TextAlignment.JUSTIFY);

        final Text helpText2 = new Text(Locale.get("window-help-text2"));
        helpText2.setWrappingWidth(430);
        helpText2.setTextAlignment(TextAlignment.JUSTIFY);

        // Create a clickable button for the source code link.
        final Hyperlink helpSource = new Hyperlink(Locale.get("window-help-source"));
        helpSource.setOnAction(event -> {
            if (Debug.APP) System.out.println("Application: Closing help window and showing opening source code page.");

            helpStage.close();
            getHostServices().showDocument("https://github.com/catlinman/spyfall");
        });

        // Set the correct link styling.
        helpSource.setStyle("-fx-border-color: null; -fx-border-style: solid; -fx-border-width: 0px; -fx-underline: true;");

        final Button helpCloseButton = new Button(Locale.get("window-help-close"));
        helpCloseButton.setMinWidth(430);

        // Add a close button to make things simpler for the user.
        helpCloseButton.setOnAction(event -> {
            if (Debug.APP) System.out.println("Application: Closing help window and returning focus.");

            helpStage.close();
        });

        // Segment separators to keep things cleaner.
        final Separator separatorHelp1 = new Separator();
        final Separator separatorHelp2 = new Separator();
        final Separator separatorHelp3 = new Separator();

        // Add components.
        helpBox.getChildren().add(helpHeader);
        helpBox.getChildren().add(separatorHelp1);
        helpBox.getChildren().add(helpText1);
        helpBox.getChildren().add(helpText2);
        helpBox.getChildren().add(separatorHelp2);
        helpBox.getChildren().add(helpSource);
        helpBox.getChildren().add(separatorHelp3);
        helpBox.getChildren().add(helpCloseButton);

        helpStack.getChildren().add(helpBox);

        // Set the scene for this window which we have prepared.
        helpStage.setScene(new Scene(helpStack, 450, 380));

        // Make sure that this window remains on top of others.
        helpStage.setAlwaysOnTop(true);

        // Show the stage and make sure to halt the main stage execution.
        helpStage.showAndWait();
    } /* createHelpWindow */

    /**
     * Creates an overlay window which displays player identity information.
     * @param int    player   Current player index.
     * @param String name     Custom name of the player. Can be left empty.
     * @param String location Location designation.
     * @param String role     Role of the specified player.
     */
    private void createRevealWindow(int player, String name, String location, String role) {
        // Prepare a new stage.
        final Stage revealStage = new Stage();

        // Set the correct window title.
        revealStage.setTitle("Spyfall - " + Locale.get("general-player") + " " + player);

        // Create the main stack.
        final StackPane revealStack = new StackPane();

        if (Debug.APP) System.out.println("Application: Showing identity card for player " + player + ".");

        // Create the vertical alignment box which all elements will be stored in.
        final VBox revealBox = new VBox(10);
        revealBox.setPadding(new Insets(10, 10, 10, 10));
        revealBox.setAlignment(Pos.TOP_CENTER);

        // Create a special VBox that contains the secret identity information.
        final VBox hiddenBox = new VBox(10);
        hiddenBox.setAlignment(Pos.TOP_CENTER);
        hiddenBox.setStyle("-fx-background-color: rgba(0, 0, 0, 1)");

        // Font used in this window.
        final Font revealFont = Font.font(null, 20);

        // Creat the texts used in this window.
        final Text playerText   = new Text(250, 250, Locale.get("general-player") + " " + player);
        final Text locationText = new Text(250, 250, " - " + location + " - ");
        final Text roleText     = new Text(250, 250, Locale.get("window-reveal-role") + " \"" + role + "\"");

        // If a name was supplied we add it to the header.
        if (!name.equals(""))
            playerText.setText(playerText.getText() + " - " + name);

        // If the player is the spy we make sure to hide location & role information.
        if (role == Locale.get("general-spy")) {
            locationText.setText(Locale.get("window-reveal-hidden"));
            roleText.setText(Locale.get("window-reveal-spy"));
        }

        // Set the custom font
        playerText.setFont(revealFont);
        locationText.setFont(revealFont);
        roleText.setFont(revealFont);

        // Hide the identity texts.
        locationText.setVisible(false);
        roleText.setVisible(false);

        // Identity texts are added to the special VBox.
        hiddenBox.getChildren().add(locationText);
        hiddenBox.getChildren().add(roleText);

        // Create buttons for the window.
        final Button showButton  = new Button(Locale.get("window-reveal-show"));
        final Button closeButton = new Button(Locale.get("window-reveal-close"));

        // Make the buttons fill the width of the window.
        showButton.setMinWidth(430);
        closeButton.setMinWidth(430);

        // Event listener for the press of the show button.
        showButton.pressedProperty().addListener((observable, wasPressed, pressed) -> {
            if (pressed) { // Remove the custom style from the VBox and show the identity texts.
                hiddenBox.setStyle("");
                locationText.setVisible(true);
                roleText.setVisible(true);

            } else { // Add styling back to the VBox and hide any information.
                hiddenBox.setStyle("-fx-background-color: rgba(0, 0, 0, 1)");
                locationText.setVisible(false);
                roleText.setVisible(false);
            }
        });

        // Add a close button to make things simpler for the user.
        closeButton.setOnAction(event -> {
            revealStage.close();
        });

        // Segment separators to keep things cleaner.
        final Separator separatorReveal1 = new Separator();
        final Separator separatorReveal2 = new Separator();

        // Start adding components to the VBox.
        revealBox.getChildren().add(playerText);
        revealBox.getChildren().add(separatorReveal1);
        revealBox.getChildren().add(hiddenBox);
        revealBox.getChildren().add(separatorReveal2);
        revealBox.getChildren().add(showButton);
        revealBox.getChildren().add(closeButton);

        // Add the main VBox to the stack.
        revealStack.getChildren().add(revealBox);

        // Set the scene for this window which we have prepared.
        revealStage.setScene(new Scene(revealStack, 450, 220));

        // Make sure that this window remains on top of others.
        revealStage.setAlwaysOnTop(true);

        // Show the stage and make sure to halt the main stage execution.
        revealStage.showAndWait();
    } /* createRevealWindow */

    /**
     * Creates a notification window prompting the player to start the game.
     */
    private void createReadyWindow() {
        // Prepare a new stage.
        final Stage readyStage = new Stage();

        // Set the correct window title.
        readyStage.setTitle("Spyfall - " + Locale.get("window-ready-title"));

        // Create the ready window stack.
        final StackPane readyStack = new StackPane();

        if (Debug.APP) System.out.println("Application: Showing game ready window.");

        // Create a VBox for easier alignment.
        final VBox readyBox = new VBox(10);
        readyBox.setPadding(new Insets(20, 10, 20, 10));
        readyBox.setAlignment(Pos.TOP_CENTER);

        final Button readyButton = new Button(Locale.get("window-ready-close"));
        readyButton.setMinWidth(430);

        // Add a close button to make things simpler for the user.
        readyButton.setOnAction(event -> {
            readyStage.close();
        });

        // Add components to each other.
        readyBox.getChildren().add(readyButton);
        readyStack.getChildren().add(readyBox);

        // Set the scene for this window which we have prepared.
        readyStage.setScene(new Scene(readyStack, 450, 60));

        // Make sure that this window remains on top of others.
        readyStage.setAlwaysOnTop(true);

        // Show the stage and make sure to halt the main stage execution.
        readyStage.showAndWait();
    } /* createReadyWindow */

    private void createResolutionWindow() {
        // Prepare a new stage.
        final Stage resolutionStage = new Stage();

        // Set the correct window title.
        resolutionStage.setTitle("Spyfall - " + Locale.get("window-resolution-title"));

        // Create the main stack.
        final StackPane resolutionStack = new StackPane();

        if (Debug.APP) System.out.println("Application: Showing game resolution window.");

        // Create the vertical alignment box which all elements will be stored in.
        final VBox resolutionBox = new VBox(10);
        resolutionBox.setPadding(new Insets(10, 10, 10, 10));
        resolutionBox.setAlignment(Pos.TOP_CENTER);

        // Font used in this window.
        final Font resolutionFont = Font.font(null, 20);

        // Get the current game location.
        Location location = spyfall.getLocation();

        // Creat the texts used in this window.
        final Text headerText = new Text(250, 250, Locale.get("window-resolution-header"));
        final Text locationText = new Text(250, 250, Locale.get("general-location") + ": " + location.getName());

        // Set the custom font
        headerText.setFont(resolutionFont);
        locationText.setFont(resolutionFont);

        // Create the close button for this window.
        final Button closeButton = new Button(Locale.get("window-resolution-close"));
        closeButton.setMinWidth(430); // Make the buttons fill the width of the window.

        // Add a close button to make things simpler for the user.
        closeButton.setOnAction(event -> {
            resolutionStage.close();
        });

        // Segment separators to keep things cleaner.
        final Separator separatorResolution1 = new Separator();
        final Separator separatorResolution2 = new Separator();

        // Add the header and first separator.
        resolutionBox.getChildren().add(headerText);
        resolutionBox.getChildren().add(separatorResolution1);
        resolutionBox.getChildren().add(locationText);

        // Retrieve the player array.
        Player[] players = spyfall.getPlayers();

        for (int i = 0; i < spyfall.getNumPlayers(); i++) {

            final Text playerText = new Text(250, 250, Locale.get("general-player") + " " + (i + 1) + ": " + players[i].getRole());
            resolutionBox.getChildren().add(playerText);

            resolutionStage.setHeight(160 + 52 + (26 * i));
        }

        // Add the second separator and close button.
        resolutionBox.getChildren().add(separatorResolution2);
        resolutionBox.getChildren().add(closeButton);

        // Add the main VBox to the stack.
        resolutionStack.getChildren().add(resolutionBox);

        // Set the scene for this window which we have prepared.
        resolutionStage.setScene(new Scene(resolutionStack, 450, 150));

        // Make sure that this window remains on top of others.
        resolutionStage.setAlwaysOnTop(true);

        // Show the stage and make sure to halt the main stage execution.
        resolutionStage.showAndWait();
    }

    private void init(Stage stage) {
        // Create a new game of Spyfall. This also loads data for locations.
        spyfall = new Game();

        if (Debug.APP) System.out.println("Application: Intializing user interface.");

        stage.setTitle("Spyfall"); // Set the window title.
        stage.setResizable(false); // Disable resizing.

        // Create the main stack and main scene.
        final StackPane mainStack = new StackPane();
        final Scene mainScene     = new Scene(mainStack, WIDTH, HEIGHT);

        Region veil = new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
        veil.setVisible(false);

        // Create the game settings section heading.
        final Text settingsText = new Text(25, 25, Locale.get("application-settings-label"));

        // Box for the application settings and buttons.
        final HBox settingsBox = new HBox(10);
        settingsBox.setPadding(new Insets(5, 10, 10, 10));
        settingsBox.setAlignment(Pos.CENTER);

        // Label for the language dropdown.
        final Text languageText = new Text(25, 25, Locale.get("general-language") + ":");

        // Dropdown menu for available languages.
        final ComboBox<String> languageDropdown = new ComboBox<String>();
        for (String l : Locale.getSupported()) languageDropdown.getItems().addAll(Locale.get("language-" + l));
        languageDropdown.getSelectionModel().select(Locale.get("language"));

        // Create the settings buttons.
        final Button saveButton = new Button(Locale.get("application-settings-save"));
        final Button helpButton = new Button(Locale.get("application-settings-help"));

        saveButton.setOnAction(event -> {
            // Load the new locale.
            Locale.initialize(Locale.getSupported()[languageDropdown.getSelectionModel().getSelectedIndex()]);

            // Write the change to the configuration file.
            writeConfig();

            if (Debug.APP) System.out.println("Application: Switching language key to "
                                              + Locale.getCurrent().toUpperCase()
                                              + " and restarting the application.");

            // Restart the main stage with the new locale changes.
            restart(stage);
        });

        // Set the action event for the help button.
        helpButton.setOnAction(event -> {
            veil.setVisible(true);

            createHelpWindow();

            veil.setVisible(false);
        });

        // Assign all buttons to the button divider box.
        settingsBox.getChildren().add(languageText);
        settingsBox.getChildren().add(languageDropdown);
        settingsBox.getChildren().add(saveButton);
        settingsBox.getChildren().add(helpButton);

        // Create the vertical alignment box which all elements will be stored in.
        final VBox verticalBox = new VBox(10);
        verticalBox.setPadding(new Insets(15, 10, 10, 10));
        verticalBox.setAlignment(Pos.TOP_CENTER);

        // Create the section heading and player text field grid.
        final Text playerText = new Text(25, 25, Locale.get("player-information-label"));

        // Player number counter for the game. Disables and enables players as needed.
        // Box for stopwatch setup and information.
        final HBox countBox = new HBox(10);
        countBox.setAlignment(Pos.CENTER);

        // Create the text and input field.
        final Text countText = new Text(Locale.get("player-information-numlabel"));

        // Dropdown menu for player slots.
        final ComboBox<Integer> countDropdown = new ComboBox<Integer>();
        countDropdown.getItems().addAll(3, 4, 5, 6, 7, 8);

        // Select the second item (4).
        countDropdown.getSelectionModel().select(1);

        // Add our items to the player counter box.
        countBox.getChildren().add(countText);
        countBox.getChildren().add(countDropdown);

        final GridPane playerGrid = new GridPane();

        playerGrid.setMaxWidth(784);
        playerGrid.setPadding(new Insets(10, 10, 10, 10));
        playerGrid.setVgap(10);
        playerGrid.setHgap(10);

        // Initialize the player fields array.
        TextField[] playerFields = new TextField[8];

        // Create the player fields.
        for (int i = 0; i < 8; i++) {
            TextField tf = new TextField();

            // Create the player text string from Locale.
            String playertext = Locale.get("general-player") + " " + (i + 1);

            // Set the correct player id placeholder and width of the textbox.
            tf.setPromptText(playertext);
            tf.setPrefWidth(188);

            if (i > 3) {
                tf.setText("");
                tf.setEditable(false);
                tf.setDisable(true);
            }

            tf.addEventFilter(KeyEvent.KEY_TYPED, maxLength(20)); // Set the character limit listener (20).

            GridPane.setConstraints(tf, i % 4, i / 4); // Create constraint based on player count.

            // Add the new text field to the grid and an array for easier management later on.
            playerGrid.getChildren().add(tf);
            playerFields[i] = tf;
        }

        // Change the player fields if the dropdown changed.
        countDropdown.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue< ? extends Integer> ov, Integer t, Integer t1) {
                for (int i = 0; i < 8; i++) {
                    TextField tf = playerFields[i];

                    String playertext = Locale.get("general-player") + " " + (i + 1);

                    if (i >= t1) {
                        tf.setText("");
                        tf.setEditable(false);
                        tf.setDisable(true);

                    } else {
                        tf.setEditable(true);
                        tf.setDisable(false);
                    }
                }
            }
        });

        // Create the game settings section heading.
        final Text informationText = new Text(25, 25, Locale.get("game-information-label"));

        // Box for stopwatch setup and information.
        final HBox stopwatchBox = new HBox(10);
        stopwatchBox.setAlignment(Pos.CENTER);

        // Create the text and input field.
        final Text stopwatchText       = new Text(Locale.get("game-information-endwatch"));
        final TextField stopwatchInput = new TextField(new IntegerStringConverter().toString(300));

        // Add integer formatting to the textfield.
        final TextFormatter<Integer> stopwatchintformatter = new TextFormatter<>(new IntegerStringConverter());

        // stopwatchInput.setTextFormatter(stopwatchintformatter);
        stopwatchInput.setPromptText("");
        stopwatchInput.setPrefWidth(60);
        stopwatchInput.setAlignment(Pos.CENTER);
        stopwatchInput.addEventFilter(KeyEvent.KEY_TYPED, maxLength(5));

        final CheckBox stopwatchCheckBox = new CheckBox();
        stopwatchCheckBox.setSelected(true);

        stopwatchCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue< ? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                stopwatchInput.setDisable(!new_val);
            }
        });

        // Add our items to the stopwatch box.
        stopwatchBox.getChildren().add(stopwatchCheckBox);
        stopwatchBox.getChildren().add(stopwatchText);
        stopwatchBox.getChildren().add(stopwatchInput);

        // Box for game information and stopwatch countdown. Is enabled after start.
        final VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setVisible(false);
        infoBox.setManaged(false);

        // Font used for the stopwatch.
        final Font stopwatchFont = Font.font(null, 20);

        final Text stopwatchCounter = new Text("");
        stopwatchCounter.setFont(stopwatchFont);

        infoBox.getChildren().add(stopwatchCounter);

        // Horizontal section box for all the buttons.
        final HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5, 10, 10, 10));
        buttonBox.setAlignment(Pos.CENTER);

        // Create the main game management buttons.
        final Button startButton = new Button(Locale.get("game-information-start"));
        final Button pauseButton = new Button(Locale.get("game-information-pause"));
        final Button stopButton  = new Button(Locale.get("game-information-end"));

        // Disable buttons that don't have any use in the beginning.
        pauseButton.setDisable(true);
        stopButton.setDisable(true);

        // Start button logic.
        startButton.setOnAction(event -> {
            // Get the player count value from the dropdown box used for the player fields.
            int playerCount = Integer.parseInt(countDropdown.getSelectionModel().getSelectedItem().toString());

            // Get the stopwatch time from the input box if it's enabled.
            int stopwatchTime = stopwatchCheckBox.isSelected() ? Integer.parseInt(stopwatchInput.getText()) : 0;

            // Prepare the game with the correct player count.
            spyfall.prepare(playerCount, stopwatchTime);

            // Drop focus of the main window.
            veil.setVisible(true);

            // Get information from the game.
            Player[] players = spyfall.getPlayers();
            String location = spyfall.getLocation().getName();

            // Show each player their identity card.
            for (int i = 0; i < playerCount; i++) {
                players[i].setName(playerFields[i].getText()); // Set the player name.
                createRevealWindow(i + 1, playerFields[i].getText(), location, players[i].getRole());
            }

            // Create an extra window after which the game will start.
            createReadyWindow();

            // Prepare the stopwatch callback and correct text.
            if (stopwatchCheckBox.isSelected() == true) {
                stopwatchCounter.setText(stopwatchInput.getText());

                spyfall.setStopwatchCallback((Long l) -> {
                    if (l != 0) {
                        stopwatchCounter.setText(Locale.get("game-information-timeleft") + ": " + l.toString());

                    } else {
                        stopwatchCounter.setText(Locale.get("game-information-gameover"));
                        pauseButton.setDisable(true);
                    }

                    return l;
                });

            } else {
                stopwatchCounter.setText(Locale.get("game-information-progress"));
            }

            // Return focus.
            veil.setVisible(false);

            // Start the game.
            spyfall.start();

            // Make sure that the gamestate is set to play.
            if (spyfall.getGamestate() == 2) { // Disable the main boxes used for program and game settings.
                settingsBox.setDisable(true);
                playerGrid.setDisable(true);
                countBox.setDisable(true);

                startButton.setDisable(true);
                stopButton.setDisable(false);

                stopwatchBox.setVisible(false);
                stopwatchBox.setManaged(false);

                infoBox.setVisible(true);
                infoBox.setManaged(true);

                if (stopwatchCheckBox.isSelected()) // Enable the pause button if the stopwatch is active.
                    pauseButton.setDisable(false);
            }
        });

        // Pause button logic.
        pauseButton.setOnAction(event -> {
            // Make sure that we are ingame for this logic.
            if (spyfall.getGamestate() == 2) {
                if (spyfall.getPaused() == true) {
                    spyfall.pause();
                    pauseButton.setText(Locale.get("game-information-resume"));

                } else {
                    spyfall.resume();
                    pauseButton.setText(Locale.get("game-information-pause"));
                }
            }
        });

        stopButton.setOnAction(event -> {
            // Enable the main menu elements again.
            settingsBox.setDisable(false);
            playerGrid.setDisable(false);
            countBox.setDisable(false);

            stopwatchBox.setVisible(true);
            stopwatchBox.setManaged(true);

            infoBox.setVisible(false);
            infoBox.setManaged(false);

            // Conclude the game by triggering the gameover state.
            spyfall.gameover();

            // Drop focus of the main window.
            veil.setVisible(true);

            // Open a window with the game conclusion information.
            createResolutionWindow();

            // Return focus.
            veil.setVisible(false);

            // Reset the game for further rounds.
            spyfall.reset();

            // Reset button states.
            startButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(true);

            // Reset the pause button in case it was changed.
            pauseButton.setText(Locale.get("game-information-pause"));
        });

        // Assign all buttons to the button divider box.
        buttonBox.getChildren().add(startButton);
        buttonBox.getChildren().add(pauseButton);
        buttonBox.getChildren().add(stopButton);

        // Text header for the location section.
        final Text locationText     = new Text(25, 25, Locale.get("general-location-plural"));
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

            stage.setHeight(HEIGHT + 60 + (38 * Math.floor(i / 5)));
        }

        // Create alignment columns for all items.
        for (int i = 0; i < 5; i++) {
            ColumnConstraints column = new ColumnConstraints(145);
            locationGrid.getColumnConstraints().add(column);
        }

        // Separators. Used to split sections for easier overview.
        final Separator separator1 = new Separator();
        final Separator separator2 = new Separator();
        final Separator separator3 = new Separator();

        // Assign all elements to the vertical box we initially created.
        verticalBox.getChildren().add(settingsText);
        verticalBox.getChildren().add(settingsBox);
        verticalBox.getChildren().add(separator1);
        verticalBox.getChildren().add(playerText);
        verticalBox.getChildren().add(countBox);
        verticalBox.getChildren().add(playerGrid);
        verticalBox.getChildren().add(separator2);
        verticalBox.getChildren().add(informationText);
        verticalBox.getChildren().add(stopwatchBox);
        verticalBox.getChildren().add(infoBox);
        verticalBox.getChildren().add(buttonBox);
        verticalBox.getChildren().add(separator3);
        verticalBox.getChildren().add(locationText);
        verticalBox.getChildren().add(locationGrid);

        // Assign the vertical box to the main stack to have all elements wrap.
        mainStack.getChildren().add(verticalBox);

        // Add the overlay veil to the main stack.
        mainStack.getChildren().add(veil);

        StackPane.setAlignment(mainStack, Pos.BASELINE_CENTER);

        // Make sure that closing this stage shuts down the program.
        stage.setOnCloseRequest(e -> Platform.exit());

        // Set the main scene.
        stage.setScene(mainScene);
        stage.show();
    } /* init */

    public void shutdown() {
        if (Debug.APP) System.out.println("Application: Closing and resetting Spyfall game states.");
        spyfall.reset();
    }

    public void restart(Stage stage) {
        stage.close();
        init(stage);
    }

    @Override
    public void start(Stage stage) {
        // Load the configuration.
        readConfig();

        // Initialize the Locale with the default key.
        Locale.initialize(CONFIG.get("lang"));

        // Start the main application.
        init(stage);
    }

    @Override
    public void stop() {
        shutdown();
    }

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
        launch(args); // Start the main application.
    }

}

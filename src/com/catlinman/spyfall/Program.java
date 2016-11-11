package com.catlinman.spyfall;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Program extends Application {
	// Constant application supported languages.
	private static final String[] LANGUAGES = {
		"en",
		"de"
	};

	public static Game spyfall; // The main game object.

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Spyfall"); // Window Title.

		spyfall = new Game(4, 5 * 60); // IDEA: Create a new game. Should be done in a handler later.

		// TODO: Create interface elements and fill in information.

		System.out.println(Paths.get("Game.ava"));

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(5);

		final TextField name = new TextField();
		name.setPromptText("Player 1");
		name.setPrefColumnCount(16);
		name.getText();
		GridPane.setConstraints(name, 0, 0);
		grid.getChildren().add(name);

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

	public static void main(String[] args) {
		launch(args);
	}
}

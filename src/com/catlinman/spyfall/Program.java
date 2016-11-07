package com.catlinman.spyfall;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Program extends Application {
	public Game spyfall;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Spyfall"); // Window Title.

		spyfall = new Game(4, 5 * 60); // IDEA: Create a new game. Should be done in a handler later.

		// TODO: Create interface elements and fill in information.

		// Text field element.
		Text text = new Text();
		text.setFont(new Font(20));
		text.setWrappingWidth(200);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setText(Long.toString(spyfall.getTimeLeft())); // FIXME: Testing line.

		StackPane root = new StackPane();
		primaryStage.setScene(new Scene(root, 800, 600));
		primaryStage.show();

		spyfall.start(); // Start the game of Spyfall. Should be handled separately later
	}

	@Override
	public void stop() {
		System.out.println("Game is closing..");
		spyfall.reset();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

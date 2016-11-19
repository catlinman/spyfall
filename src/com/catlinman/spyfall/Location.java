package com.catlinman.spyfall;

// File handling imports.
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

// Utility imports.
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Location {
	// These variables are relevant to reading and storing the data file.
	private static final String SEPARATOR = ",";
	private static final String DATAFILE  = "location.csv";
	private static String[][] data;

	private Game game; // Contains the current game instance and information.

	private int id;
	private String name;
	private String[] roles;

	Location(Game game) {
		this.game = game; // Bind the game instance to the class member.

		if (data == null) loadData(Program.gamelang);  // Load the data if it doesn't exist.

		// Locations are picked at random from the data set.
		this.id = ThreadLocalRandom.current().nextInt(1, data.length + 1);

		this.name  = data[this.id][0]; // First entry is always the location name.
		this.roles = new String[7];
		System.arraycopy(data[this.id], 1, this.roles, 0, 7);
	}

	// Sets a player's role in respect to other already assigned roles.
	void assignRole(Player p) {
		Player[] playerArray = this.game.getPlayers(); // Get the main game player array.

		// If no Spy picking is required we continue with the normal random selection method.
		Utilities.shuffle(this.roles); // Randomly sort the array contents.
		for (String r : this.roles) {
			boolean found = false;
			// Iterate over the players and check if their role matches up and is already taken.
			for (int i = 0; i < this.game.getNumPlayers(); i++)
				// FIXME: This currently throw a null pointer exception.
				if (playerArray[i].getRole() != null) {
					found = true;
					break; // Skip the iteration if the role was found.
				}

			// If the role is available we assign it to the given player.
			if (!found) {
				p.setRole(r);

				return;
			}
		}
	} /* assignRole */

	int getID() {
		return this.id;
	}

	String getName() {
		return this.name;
	}

	String[] getRoles() {
		return this.roles;
	}

	// Loads localized input data into the static data table used for locations and roles.
	// TODO: Possibly remove debugging lines or change the log system.
	private static void loadData(String lang) {
		// Fetch the resource location from the localized input file.
		String datapath = Location.class.getClassLoader().getResource(lang + "_" + DATAFILE).getPath();

		String content = ""; // Used as temporary storage for the input data.

		// Read the file contents with the right system encoding and combine it to a single string.
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(datapath));
			content = new String(encoded, Charset.defaultCharset());

		} catch (IOException e) {}

		// Split each line into a new string.
		String[] lines = content.split("\n");

		// Create a new three dimensional string array without the header and the right data length.
		data = new String[lines.length - 1][8];

		if (Program.DEBUG) System.out.println(lang.toUpperCase() + " DATA CSV is loading.");

		// Iterate over each line. Split each at the separator. Clean up the string and insert it into the location data array.
		for (int i = 0; i < lines.length - 1; i++) {
			if (Program.DEBUG) System.out.println(lines[i + 1]);

			try {
				String[] fields = lines[i + 1].split(SEPARATOR);

				for (int j = 0; j < fields.length; j++) data[i][j] = Utilities.capitalize(fields[j]);

			} catch (IndexOutOfBoundsException e) {
				if (Program.DEBUG) System.out.println(lang.toUpperCase() + " DATA CSV: Line " + i
					  + " does not contain the right amount of fields (expected 8 fields).");
			}
		}
	} /* loadData */

}

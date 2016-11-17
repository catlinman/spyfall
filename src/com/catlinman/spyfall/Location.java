package com.catlinman.spyfall;

import java.util.ArrayList;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

class Location {
	private static final String SEPARATOR = ",";
	private static final String DATAFILE  = "location.csv";
	private static String[][] data;

	String name    = "";
	String[] roles = new String[7];

	Location() {
		if (data == null) loadData(Program.gamelang);  // Load the data if it doesn't exist.

		// Locations are picked at random from the data set.
		int locationid = ThreadLocalRandom.current().nextInt(1, data.length + 1);

		this.name = data[locationid][0];
		System.arraycopy(data[locationid], 1, this.roles, 0, 7);

		if (Program.DEBUG) {
			System.out.println("Current game location: " + locationid + ". " + this.name);
			System.out.println("Roles: " + String.join(", ", roles));
		}
	}

	// Loads localized input data into the static data table used for locations and roles.
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

				for (int j = 0; j < fields.length; j++) data[i][j] = StringUtilities.capitalize(fields[j]);

			} catch (IndexOutOfBoundsException e) {
				if (Program.DEBUG) System.out.println(lang.toUpperCase() + " DATA CSV: Line " + i
					  + " does not contain the right amount of fields (expected 8 fields).");
			}
		}
	} /* loadData */

	// Sets a players role and removes it from the possible list of roles.
	String getRole(Player player) {
		// TODO: Assign the player to the given role from the list of roles.
		return "Role";
	}

}

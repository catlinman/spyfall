package com.catlinman.spyfall;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

// Localized location data is loaded from the key used by the Locale class.
class Location {
    // These variables are relevant to reading the data file.
    private static final String SEPARATOR    = ",";
    private static final String DATAFILENAME = "location.csv";

    private static String[][] data;    // Contains data (location + roles).
    private static String[] locations; // Contains all possible location names.

    private Game game; // Contains the current game instance and information.

    private int id;
    private String name;
    private String[] roles;

    Location(Game game) {
        this.game = game; // Bind the game instance to the class member.

        if (data == null) {
            if (Debug.DATA) System.out.println("No location data present. Aborting location instantiation.");
            return;
        }

        // Locations are picked at random from the data set.
        this.id = ThreadLocalRandom.current().nextInt(1, data.length + 1);

        this.name  = data[this.id][0]; // First entry is always the location name.
        this.roles = new String[7];

        System.arraycopy(data[this.id], 1, this.roles, 0, 7); // Copy the roles segment of the data array.
    }

    // Sets a player's role in respect to other already assigned roles.
    void assignRole(Player p) {
        // If no Spy picking is required we continue with the normal random selection method.
        Utilities.shuffle(this.roles); // Randomly sort the array contents.
        for (String r : this.roles) {
            boolean found = false;
            // Iterate over the players and check if their role matches up and is already taken.
            for (Player p2 : this.game.getPlayers()) {
                if (p2 != null)
                    if (p2.getRole() == r) {
                        found = true;
                        break; // Skip the iteration if the role was found.
                    }
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
    static void initialize() {
        // Get the currently set language key.
        String lang = Locale.getCurrent();

        // Reserve variables for our handling of the locale file location.
        URI dataURI; String dataPath;

        // Fetch the resource location from the localized input file. We originally receive a URL which we convert to a URI.
        try {
            dataURI = Program.class.getClassLoader().getResource(String.format("%s_%s", lang, DATAFILENAME)).toURI();
            dataPath = Paths.get(dataURI).toString();

        } catch(URISyntaxException e) {
            System.out.println(e);

            return;
        }

        String content = ""; // Used as temporary storage for the input data.

        // Read the file contents with the right system encoding and combine it to a single string.
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(dataPath));
            content = new String(encoded, Charset.defaultCharset());

        } catch (IOException e) {}

        // Split each line into a new string.
        String[] lines = content.split("\n");

        // Create a new three dimensional string array without the header and the right data length.
        data = new String[lines.length - 1][8];

        // Create the location name array.
        locations = new String[lines.length - 1];

        if (Debug.DATA) System.out.println("Data: Location information " + lang.toUpperCase() + " is loading.");

        // Iterate over each line. Split each at the separator. Clean up the string and insert it into the location data array.
        for (int i = 0; i < lines.length - 1; i++) {
            if (Debug.DATA) System.out.println(lines[i + 1]);

            try {
                String[] fields = lines[i + 1].split(SEPARATOR); // Split the line using our separator symbol and skip the header.

                // Create strings from the input data and make sure that they are formatted correctly.
                for (int j = 0; j < fields.length; j++) data[i][j] = Utilities.capitalize(fields[j]);

                locations[i] = data[i][0]; // Add this location name to the location array.

            } catch (IndexOutOfBoundsException e) {
                if (Debug.DATA) System.out.println(lang.toUpperCase() + " DATA CSV: Line " + i
                                                   + " does not contain the right amount of fields (expected 8 fields).");



            }
        }
    } /* loadData */

    static String[][] getData() {
        return data;
    }

    static String[] getLocations() {
        return locations;
    }
}

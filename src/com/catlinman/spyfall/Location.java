package com.catlinman.spyfall;

import java.util.ArrayList;

// TODO: Write JSON reader.

class Location {
	private static ArrayList<String> data;
	String name;

	Location() {}

	Location(String name) {
		// Pick the location by name. If it doesn't exist throw an error.
	}

	private static void loadData(String lang) {
		// TODO: Load location data CSV file here based on input language.
	}

	// Sets a players role and removes it from the possible list of roles.
	Role getRole(Player player) {
		// TODO: Assign the player to the given role in the list of roles.
		return new Role("Role");
	}
}

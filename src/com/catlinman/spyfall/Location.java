package com.catlinman.spyfall;

import java.util.ArrayList;
import java.io.FileReader;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;

class Location {
	private ArrayList<String> data;
	String name;

	private void loadData() {
		// this.data = new CSVReader("data.csv");
	}

	Location() {
		// Pick a random location from the data set.
	}

	Location(String name) {
		// Pick the location by name. If it doesn't exist throw an error.
	}

	// Sets a players role and removes it from the possible list of roles.
	Role getRole(Player player) {
		// TODO: Assign the player to the given role in the list of roles.
		return new Role("Role");
	}
}

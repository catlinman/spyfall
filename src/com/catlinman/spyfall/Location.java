// TODO: Location class. Handles role assignment and player tracking.

package com.catlinman.spyfall;

class Location {
	String data;

	String name;
	String[] roles;

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

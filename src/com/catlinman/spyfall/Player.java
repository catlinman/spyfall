package com.catlinman.spyfall;

// Proxy data class for maintaining player information.
class Player {
	String name;
	Location location;
	String role;

	Player(String n, Location l, String r) {
		this.name     = n;
		this.location = l;
		this.role     = r;
	}

}

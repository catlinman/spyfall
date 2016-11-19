package com.catlinman.spyfall;

// Proxy data class for maintaining player information.
class Player {
	private int id;
	private String name;
	private Location location;
	private String role;

	Player(int id) {
		this.id = id;
	}

	Player(int id, String n) {
		this.id   = id;
		this.name = n;
	}

	int getID() {
		return this.id;
	}

	void setID(int id) {
		this.id = id;
	}

	Location getLocation() {
		return this.location;
	}

	void setLocation(Location l) {
		this.location = l;
	}

	String getName() {
		return this.name;
	}

	void setName(String n) {
		this.name = n;
	}

	String getRole() {
		return this.role;
	}

	void setRole(String r) {
		this.role = r;
	}

}

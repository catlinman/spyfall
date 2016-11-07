package com.catlinman.spyfall;

class Player {
	Location location;
	Role role;

	Player(Location location) {
		this.location = location;
		this.role     = this.location.getRole(this);
	}
}

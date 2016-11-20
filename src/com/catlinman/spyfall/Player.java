package com.catlinman.spyfall;

// Proxy data class for maintaining player information.
class Player {
	private int id;
	private String name;
	private Location location;
	private String role;
	private Player suspect;
	private int votes = 0;

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

	void setSuspect(Player p) {
		this.suspect = p;
	}

	Player getSuspect() {
		return this.suspect;
	}

	void addVote(Player p) {
		p.setSuspect(this);
		this.votes += 1;
	}

	void removeVote(Player p) {
		p.setSuspect(null);
		this.votes -= 1;
	}

	int getVotes() {
		return this.votes;
	}

	void setVotes(int v) {
		this.votes = v;
	}

	void doVote(Player p) {
		if (p != this) {
			if (this.suspect != null) this.suspect.removeVote(this);

			this.suspect = p;
			this.suspect.addVote(this);
		}
	}

}

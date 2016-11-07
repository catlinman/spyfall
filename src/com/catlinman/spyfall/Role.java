package com.catlinman.spyfall;

class Role {
	Boolean isSpy = false;
	String name;

	Role(String name) {
		if (name == "Spy") this.isSpy = true;
		this.name = name;
	}
}

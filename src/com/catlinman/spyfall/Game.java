package com.catlinman.spyfall;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

// Main game entry point. Handles all game logic and timing as well as object interaction.
public class Game {
	private static final int MAXPLAYERS   = 8;   // Constant maximum players.
	private static final long DEFAULTTIME = 480; // Constant default time.

	private Boolean ingame = false; // Stores the current game state.
	private int numPlayers = 0;     // Stores the current amount of players.
	private Location location;      // Stores the current game location.
	private Player[] players;       // Stores the current player objects.
	private int spyID = 0;          // The player ID that has been assigned as spy.

	private Thread stopwatchThread;                 // Dedicated thread to handle the stopwatch.
	private Boolean stopwatchEnabled = false;       // If the stopwatch thread should be launched.
	private Boolean stopwatchActive  = false;       // If the stopwatch thread should run.
	private long stopwatchTime       = DEFAULTTIME; // Set a stopwatch time by default.

	public Game(int pcount, long time) {
		// This should be handled with a return event later on.
		if (pcount <= MAXPLAYERS)
			this.numPlayers = pcount;
		else
			this.numPlayers = MAXPLAYERS;

		this.players = new Player[pcount]; // Create the player array.

		// Setup stopwatch information.
		this.stopwatchTime    = time;
		this.stopwatchEnabled = this.stopwatchTime > 0 ? true : false;

		// Get and instantiate a new location from the location data.
		this.location = new Location(this);

		// Print game location information.
		if (Program.DEBUG) {
			System.out.println("Current game location: " + this.location.getID() + ". " + this.location.getName());
			System.out.println("Roles: " + String.join(", ", this.location.getRoles()));
		}

		// Start creating players and assigning roles.
		for (int i = 0; i < pcount; i++) {
			this.players[i] = new Player(i, "Player " + (i + 1));

			if (this.getSpyPlayer() == null) {
				if (ThreadLocalRandom.current().nextInt(1, this.numPlayers + 1) == 1)
					this.setSpyPlayer(this.players[i]);  // Random chance to be picked as the spy.

			} else {
				// If not picked as the spy assign a role. Returns null if all roles are taken.
				this.location.assignRole(this.players[i]);

				if (Program.DEBUG) {
					String role = this.players[i].getRole();

					if (role != null) System.out.print(
							"Player " + this.players[i].getID() + " has been assigned the role of "
							+ this.players[i].getRole());
				}
			}

			// If we're at the latest player we check if a spy has been picked. If not, assign this player as the spy.
			if (i == pcount)
				if (this.players[this.numPlayers - 1].getRole() == null && this.getSpyPlayer() == null)
					this.setSpyPlayer(this.players[this.numPlayers - 1]);
		}
	}

	/*
	 * Prepares game logic and starts the main timer if enabled.
	 */
	public void start() {
		this.ingame = true;

		// Stopwatch handling.
		if (this.stopwatchEnabled) {
			this.stopwatchActive = true;

			new Thread() {
				public void run() {
					while (ingame) {
						try {
							if (stopwatchActive) {
								stopwatchTime--;
								if (Program.DEBUG) System.out.print("Stopwatch seconds left: " + stopwatchTime + "\r");
							}

							Thread.sleep(1000);

						} catch (InterruptedException e) {}
					}
				}

			}.start();
		}
	}

	/*
	 * Resets all game variables to their default state.
	 */
	public void reset() {
		this.location         = null;
		this.players          = null;
		this.ingame           = false;
		this.stopwatchEnabled = false;
		this.stopwatchActive  = false;
		this.stopwatchTime    = 0;
	}

	/*
	 * Pauses the game and it's handling as well as the stopwatch.
	 */
	public void pause() {
		this.stopwatchActive = false;
	}

	/**
	 * Resumes the game after being paused.
	 */
	public void resume() {
		if (this.stopwatchEnabled) this.stopwatchActive = true;
	}

	/**
	 * Called by the stopwatch or when a final vote has been raised.
	 */
	public void gameover() {
		this.stopwatchActive = false;
	}

	/**
	 * Returns the current game location.
	 * @return Instantiated game location class.
	 */
	public Location getLocation() {
		return this.location;
	}

	/**
	 * Returns the player array.
	 * @return Instantiated player array.
	 */
	public Player[] getPlayers() {
		return this.players;
	}

	/**
	 * Returns the current player count.
	 * @return Integer count of currently instantiated players..
	 */
	public int getNumPlayers() {
		return this.numPlayers;
	}

	/**
	 * Returns the constant maximum amount of players supported by the game.
	 * @return The maximum count of supported players.
	 */
	public int getMaxPlayers() {
		return MAXPLAYERS;
	}

	public void setSpyPlayer(Player p) {
		if (Program.DEBUG)
			System.out.print("Player " + p.getID() + " has been picked as the Spy!");

		this.spyID = p.getID();
		p.setRole("Spy");
	}

	public Player getSpyPlayer() {
		return this.players[this.spyID];
	}

	/**
	 * Returns the given game's remaining stopwatch time.
	 * @return The remaining time in seconds.
	 */
	public long getTimeLeft() {
		return this.stopwatchTime;
	}

}

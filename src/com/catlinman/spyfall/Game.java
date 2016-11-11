package com.catlinman.spyfall;

import java.util.ArrayList;
import java.lang.Thread;

// Main game entry point. Handles all game logic and timing as well as object interaction.
public class Game {
	private static final int MAXPLAYERS   = 8;   // Constant maximum players.
	private static final long DEFAULTTIME = 480; // Constant default time.

	private Boolean ingame = false;    // Stores the current game state.
	private int numPlayers = 0;        // Stores the current amount of players.
	private Location location;         // Stores the current game location.
	private ArrayList<Player> players; // Stores the current player objects.

	private Thread stopwatchThread;           // Dedicated thread to handle the stopwatch.
	private Boolean stopwatchEnabled = false; // If the stopwatch thread should be launched.
	private Boolean stopwatchActive  = false; // If the stopwatch thread should run.
	private long stopwatchTime       = DEFAULTTIME;

	public Game(int players, long time) {
		// This should be handled with a return event later on.
		if (this.players.size() <= MAXPLAYERS)
			this.numPlayers = players;
		else
			this.numPlayers = MAXPLAYERS;

		this.stopwatchTime    = time;
		this.stopwatchEnabled = (this.stopwatchTime > 0) ? true : false;
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
								System.out.println(stopwatchTime);
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
	 * Returns the constant maximum amount of players supported by the game.
	 * @return The maximum count of supported players.
	 */
	public int getMaxPlayers() {
		return MAXPLAYERS;
	}

	/**
	 * Returns the given game's remaining stopwatch time.
	 * @return The remaining time in seconds.
	 */
	public long getTimeLeft() {
		return this.stopwatchTime;
	}
}

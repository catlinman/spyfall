package com.catlinman.spyfall;

import java.lang.Thread;

public class Game {
	private int maxPlayers = 10;
	private int numPlayers = 0;

	private Boolean ingame = false;

	private Location location;
	private Player[] players;

	private Thread stopwatchThread;
	private Boolean stopwatchEnabled = false;
	private Boolean stopwatchActive  = false;
	private long stopwatchTime       = 0;

	public Game(int players, long time) {
		// This should be handled with a return event later on.
		if (players <= maxPlayers)
			this.numPlayers = players;
		else
			this.numPlayers = maxPlayers;

		this.stopwatchTime    = time;
		this.stopwatchEnabled = (this.stopwatchTime > 0) ? true : false;
	}

	// Starts the game loop and the main timer if set.
	public void start() {
		this.ingame = true;

		if (this.stopwatchEnabled) {
			this.stopwatchActive = true;

			new Thread() {
				public void run() {
					while (ingame && stopwatchActive) {
						try {
							stopwatchTime--;
							System.out.println(stopwatchTime);
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					}
				}
			}.start();
		}
	}

	// Resets all game variables to their default state.
	public void reset() {
		this.location         = null;
		this.players          = null;
		this.ingame           = false;
		this.stopwatchEnabled = false;
		this.stopwatchActive  = false;
		this.stopwatchTime    = 0;
	}

	// Pauses the game and stopwatch.
	public void pause() {
		this.stopwatchActive = false;
	}

	// Resumes the game after being paused.
	public void resume() {
		if (this.stopwatchEnabled) this.stopwatchActive = true;
	}

	// Called by the stopwatch or when a final vote has been raised.
	public void gameover() {
		this.stopwatchActive = false;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public long getTimeLeft() {
		return this.stopwatchTime;
	}
}

package com.catlinman.spyfall;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;


// Main game entry point. Handles all game logic and timing as well as object interaction.
public class Game {
    private static final int MAXPLAYERS   = 8;   // Constant maximum players.
    private static final long DEFAULTTIME = 480; // Constant default time.

    private int gamestate  = 0;  // Stores the current game state.
    private int numPlayers = 0;  // Stores the current amount of players.
    private int spyID      = -1; // The player ID that has been assigned as spy.

    private Location location; // Stores the current game location.
    private Player[] players;  // Stores the current player objects.

    private Thread stopwatchThread;                 // Dedicated thread to handle the stopwatch.
    private boolean stopwatchEnabled = false;       // If the stopwatch thread should be launched.
    private boolean stopwatchActive  = false;       // If the stopwatch thread should run.
    private long stopwatchTime       = DEFAULTTIME; // Set a stopwatch time by default.

    private Function<Long, Long> stopwatchCallback; // Callback function triggered when the stopwatch value changes.

    public Game() {
        if (Debug.GAME) System.out.println(
                "Spyfall: New game intilizing. Loading location data for the language key of " + Locale.getCurrent().toUpperCase()
                + ".");

        Location.initialize();
    }

    /*   ,ad8888ba,                                                  88                            88
     *  d8"'    `"8b                                                 88                            ""
     * d8'                                                           88
     * 88             ,adPPYYba,  88,dPYba,,adPYba,    ,adPPYba,     88   ,adPPYba,    ,adPPYb,d8  88   ,adPPYba,
     * 88      88888  ""     `Y8  88P'   "88"    "8a  a8P_____88     88  a8"     "8a  a8"    `Y88  88  a8"     ""
     * Y8,        88  ,adPPPPP88  88      88      88  8PP"""""""     88  8b       d8  8b       88  88  8b
     *  Y8a.    .a88  88,    ,88  88      88      88  "8b,   ,aa     88  "8a,   ,a8"  "8a,   ,d88  88  "8a,   ,aa
     *   `"Y88888P"   `"8bbdP"Y8  88      88      88   `"Ybbd8"'     88   `"YbbdP"'    `"YbbdP"Y8  88   `"Ybbd8"'
     *                                                                                 aa,    ,88
     *                                                                                  "Y8bbdP"
     */

    public void prepare(int pcount, long time) {
        if (Debug.GAME) System.out.println("Spyfall: Preparing the game by selection a location and assigning roles.");

        // This should be handled with a return event later on.
        if (pcount <= MAXPLAYERS)
            this.numPlayers = pcount;
        else
            this.numPlayers = MAXPLAYERS;

        // Instantiate arrays with the right length.
        this.players = new Player[this.numPlayers];

        // Setup stopwatch information.
        this.stopwatchTime    = time;
        this.stopwatchEnabled = this.stopwatchTime > 0 ? true : false;

        // Get and instantiate a new location from the location data set.
        this.location = new Location(this);

        // Print game location information.
        if (Debug.GAME) {
            System.out.println(
                "Spyfall: Current game location is " + this.location.getID() + ". " + this.location.getName());
            System.out.println("Spyfall: The roles are " + String.join(", ", this.location.getRoles()));
        }

        this.setRoles(); // Assign roles to players.

        this.gamestate = 1; // Set that we have reached the preparing gamestate.
    }

    /*
     * Prepares game logic and starts the main timer if enabled.
     */
    public void start() {
        if (this.gamestate < 1) {
            if (Debug.GAME) System.out.println("Spyfall: Game can not start without being prepared first.");
            return;
        }

        if (Debug.GAME) System.out.println("Spyfall: Game started.");

        this.gamestate = 2;

        // Stopwatch handling.
        if (this.stopwatchEnabled) {
            this.stopwatchActive = true;

            new Thread() {
                public void run() {
                    stopwatchTime += 1;

                    while (gamestate == 2) {
                        try {
                            if (stopwatchActive == true) {
                                stopwatchTime--;
                                if (Debug.GAME) System.out.print(
                                        "Spyfall: Stopwatch seconds left: " + stopwatchTime + "\r");

                                if(stopwatchCallback != null)
                                    stopwatchCallback.apply(stopwatchTime);
                            }

                            if (stopwatchTime <= 0) {
                                if (Debug.GAME) System.out.print("\n");

                                gameover();
                            }

                            Thread.sleep(1000);

                        } catch (InterruptedException e) {}
                    }
                }

            }.start();
        }
    } /* start */

    /*
     * Resets all game variables to their default states.
     */
    public void reset() {
        this.gamestate        = 0;
        this.location         = null;
        this.players          = null;
        this.spyID            = -1;
        this.stopwatchEnabled = false;
        this.stopwatchActive  = false;
        this.stopwatchTime    = 0;

        if (Debug.GAME) System.out.println("Spyfall: The game has been reset.");
    }

    /*
     * Pauses the game and it's handling as well as the stopwatch.
     */
    public void pause() {
        if (this.gamestate == 2) {
            this.stopwatchActive = false;

            if (Debug.GAME) System.out.println("Spyfall: Game has been paused.");

        } else if (Debug.GAME) { System.out.println("Spyfall: Game can not be paused since it's not in progress."); }
    }

    /**
     * Resumes the game after being paused.
     */
    public void resume() {
        if (this.gamestate == 2) {
            this.stopwatchActive = true;

            if (Debug.GAME) System.out.println("Spyfall: Game has been resumed.");

        } else if (Debug.GAME) { System.out.println("Spyfall: Game can not be resumed since it's not in progress."); }
    }

    /**
     * Called by the stopwatch or when a final vote has been raised. Does not reveal information.
     */
    public void gameover() {
        this.gamestate       = 3; // Set the timeout gamestate.
        this.stopwatchActive = false;

        if (Debug.GAME) System.out.println("Spyfall: Game finished and waiting for conclusion.");
    }

    // TODO: Evaluate the game result and state including guesses and votes.
    public void conclude() {
        this.gamestate = 4; // Set the conclusion gamestate.

        if (Debug.GAME) System.out.println("Spyfall: Showing game resolution and outcome.");
    }

    /**
     * Casts a vote from a given player for a player. Voter and suspect must be different players.
     */
    public void vote(int voterid, int suspectid) {
        if (voterid < 0 || voterid > this.numPlayers - 1 || suspectid < 0 || suspectid > this.numPlayers - 1) {
            if (Debug.GAME) System.out.println("Spyfall: Invalid player id supplied during vote.");
            return;
        }

        if (voterid != suspectid) {
            this.players[voterid].doVote(this.players[suspectid]);

            if (Debug.GAME) System.out.println(
                    "Spyfall: Player " + (voterid + 1) + " voted for player " + (suspectid + 1)
                    + ". Player " + (suspectid + 1) + " now has " + this.players[suspectid].getVotes() + " votes.");
        }
    }

    /**
     * Evaluates and counts votes to return the highest voted player.
     * @return The highest voted player. If there is a tie it returns null.
     */
    public Player voteResult() {
        // Comparision values. These should be lower than base values.
        int v1  = -1;
        int id1 = -1;

        // Lower than the other comparision values to have them evaluated and possibly replaced.
        int v2  = -2;
        int id2 = -2;

        // Iterate over values and compare them. Get the two highest results and store them.
        for (int i = 0; i < this.numPlayers; i++)
            if (this.players[i].getVotes() >= v1) {
                v2 = v1;

                id2 = id1;

                v1  = this.players[i].getVotes();
                id1 = i;
            }

        if (v1 == v2)
            return null;  // If tied return null.

        else
            return this.players[id1];  // Otherwise return the player by their ID.
    }

    // Returns true if the location guess matches up.
    public boolean guessResult(String l) {
        return l.trim().toLowerCase() == this.location.getName().trim().toLowerCase() ? true : false;
    }

    /*  ,ad8888ba,                              ,adba,         ad88888ba
     *  d8"'    `"8b                ,d           8I  I8        d8"     "8b                ,d
     * d8'                          88           "8bdP'        Y8,                        88
     * 88              ,adPPYba,  MM88MMM       ,d8"8b  88     `Y8aaaaa,     ,adPPYba,  MM88MMM
     * 88      88888  a8P_____88    88        .dP'   Yb,8I       `"""""8b,  a8P_____88    88
     * Y8,        88  8PP"""""""    88        8P      888'             `8b  8PP"""""""    88
     *  Y8a.    .a88  "8b,   ,aa    88,       8b,   ,dP8b      Y8a     a8P  "8b,   ,aa    88,
     *   `"Y88888P"    `"Ybbd8"'    "Y888     `Y8888P"  Yb      "Y88888P"    `"Ybbd8"'    "Y888
     */

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

    /**
     * Sets a player to be the game's Spy.
     */
    public void setSpyPlayer(Player p) {
        this.spyID = p.getID();
        p.setRole(Locale.get("general-spy"));

        if (Debug.GAME)
            System.out.println("Spyfall: Player " + (p.getID() + 1) + " has been picked as the Spy!");
    }

    /**
     * Returns the game's Spy player. If none is set, returns null.
     * @return Spy player object. If none exists, returns null.
     */
    public Player getSpyPlayer() {
        // Default spyID is -1 meaning we'll go out of the array.
        try {
            return this.players[this.spyID];

        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the given game's remaining stopwatch time.
     * @return The remaining time in seconds.
     */
    public long getTimeLeft() {
        return this.stopwatchTime;
    }

    /**
     * Sets the callback for the stopwatch change.
     * @param Function<Long, Long> func Function to be called on stopwatch time change.
     */
    public void setStopwatchCallback(Function<Long, Long> func) {
        this.stopwatchCallback = func;
    }

    /**
     * Returns the game's pause state.
     * @return boolean value of the game pause state.
     */
    public boolean getPaused() {
        return this.stopwatchActive;
    }

    /**
     * Returns the current gamestate.
     * Gamestates:
     * 0 = Waiting
     * 1 = Prepared
     * 2 = Ingame
     * 3 = Completed
     * 4 = Resolution
     * @return Gamestate identifier.
     */
    public int getGamestate() {
        return this.gamestate;
    }

    /*
     * Assigns roles to players.
     */
    public void setRoles() {
        // Make sure a location has been initialized.
        if (this.location == null) {
            if (Debug.GAME)
                System.out.println("Spyfall: Can't prepare roles. Game location has not been initialized.");

            return;
        }


        // Start creating players and assigning roles.
        for (int i = 0; i < this.numPlayers; i++) {
            this.players[i] = new Player(i, "Player " + (i + 1));

            if (this.getSpyPlayer() == null && ThreadLocalRandom.current().nextInt(1, this.numPlayers + 1) == 1) {
                this.setSpyPlayer(this.players[i]); // Random chance to be picked as the spy.

            } else {
                // If not picked as the spy assign a role. Returns null if all roles are taken.
                this.location.assignRole(this.players[i]);

                // Print role information.
                if (Debug.GAME) {
                    String role = this.players[i].getRole();

                    if (role != null)
                        System.out.println("Spyfall: Player " + (this.players[i].getID() + 1)
                                           + " has been assigned the role of " + this.players[i].getRole());
                }
            }

            // If we're at the latest player we check if a spy has been picked. If not, assign this player as the spy.
            if (i == this.numPlayers - 1 && this.getSpyPlayer() == null)
                this.setSpyPlayer(this.players[this.numPlayers - 1]);
        }
    } /* setRoles */

}

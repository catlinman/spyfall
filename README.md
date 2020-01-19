
# Spyfall #

This repository contains a version of the game that has been adapted for use on
one machine for local multiplayer. It was written in Java over the course of a
single day and uses the JavaFX library for the user interface. The idea behind
this port was to have a locally playable version of the game without the need to
have any cards and other gameplay elements at hand.

## Rules ##

Spyfall is a game where players are each assigned a role and a general location.
One player is randomly selected and does not receive any information. This
player is the Spy. The Spy's goal is to find out the location in which the game
is currently taking place. At no point are normal players allowed to reveal
their role or the location. Players take turns asking other players questions,
attempting to find out who the spy is within a limited amount of time. Players
can make a majority vote where they decide on a player to be the spy. If the
given player is the spy after the vote successfully passes the spy loses and
everyone else wins. If the selection is not the spy, the real spy wins the game.
The spy can also win the game at any point by calling a timeout and attempting
to state the location that the game is taking place in.

## Requirements ##

This project depends on JavaFX which these days is not shipped with the JDK
anymore. Due to this, you will have to manually install it as a dependency
from the currently maintained repository. Additionally, there are builds
available for various platforms from the [project website](https://openjfx.io/).

Once you have downloaded the current JavaFX SDK, place it in an easily accessible
library directory.

For setup with an IDE read one of the project guides or general assistance and
further reference please refer to the
[JavaFX installation guide](https://openjfx.io/openjfx-docs/).

## Setup ##

To build the application run the following command in the *src* directory.

    $ javac com/catlinman/spyfall/Program.java

If you are running solely from the command line, you will have to append arguments
to include your JavaFX library. To keep things short on the prompt,we load all
available modules.

    $ javac --module-path [../JavaFX/lib] --add-modules=ALL-MODULE-PATH

Following up, to run the program from the command line.

    $ java -cp .:../res com.catlinman.spyfall.Program

Keep in mind on Windows you will have to use `;` as your classpath separator.

    $ java -cp ".;../res" com.catlinman.spyfall.Program

You can also build a jar file and package up the program which is recommended in
most cases. Don't forget to run ```javac``` beforehand.

    $ jar cfm Spyfall.jar manifest.mf com/catlinman/spyfall/*.class

From there on you can run the application using *```java -jar Spyfall.jar```*.

## Disclaimer ##

The original Spyfall was designed by Alexandr Ushan and published by Hobby
World. This is an unofficial  project, and is not endorsed or related in any way
by the designer or publisher.

## License ##

This repository is released under the MIT license. For more information please
refer to [LICENSE](https://github.com/catlinman/spyfall/blob/master/LICENSE)

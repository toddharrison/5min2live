5min2live
=========

Minecraft Bukkit plugin where you must complete each successive challenge in five minutes or die. A challenge consists of set of items (e.g. 3 dirt blocks) that must be taken to a specific location (the Oni) and sacrificed.

Dependencies
------------

* Testing with Bukkit 1.7.2-R0.3

Features
--------

* Configurable challenges based on the number that have already been completed (level of player)
* Uses the player level for progress through the challenges
* Uses the XP bar as a 5 minute countdown timer
* Message and audio alerts to players as they approach the deadline

TODO
----

* Built-in character switching doesn't handle inventory yet, only health, hunger, experience and location
* Allow Oni sacrifices to be given by what's in the players hand instead of opening up an inventory
* Configure the refresh rate of the timer to allow server-specific adjustments
* Debug ConcurrentModificationException in PlayerManager
* Documentation and plugin usage

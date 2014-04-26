5min2live
=========

Minecraft Bukkit plugin where you must complete each successive challenge in five minutes or die. A challenge consists of set of items (e.g. 3 dirt blocks) that must be taken to a specific location (the Oni) and sacrificed by clicking on the glass block in its mouth.

Dependencies
------------

* Testing with Bukkit 1.7.2-R0.3

Features
--------

* Configurable challenges based on the number that have already been completed (level of player)
* Uses the player level for progress through the challenges
* Uses the XP bar as a 5 minute countdown timer
* Message and audio alerts to players as they approach the deadline
* The player may right-click with an item in hand to sacrifice it directly or, if that doesn't match, it will open an inventory for the sacrifice

TODO
----

* Built-in character switching doesn't handle inventory yet, only health, hunger, experience and location
* Configure the refresh rate of the timer to allow server-specific adjustments
* Debug ConcurrentModificationException in PlayerManager
* Documentation and plugin usage
* Regenerate the world at specific intervals or when there are no current players
* Handle chests left by a player who restarts
* Some placeable items (like flowers) prevent the Oni inventory from opening

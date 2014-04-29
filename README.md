5min2live
=========

*This is the beta release of 5min2live. I'm looking for comments and feedback as well as new
features to implement.*

What is 5min2live? It is a Minecraft Bukkit plugin where you must complete each successive challenge
in five minutes or die. A challenge consists of set of items (e.g. 3 dirt blocks) that must be taken
to a specific location (the Oni) and sacrificed by clicking on the glass block in its mouth. Upon
completion of each challenge the count down is reset and the player level goes up with the
challenges becoming more difficult.

Dependencies
------------

* Testing with Bukkit 1.7.2-R0.3

Features
--------

**version 0.1**

* Challenges
  * Player level used to indicate how many challenges have been completed
  * Customizable challenges into challenge sets
    * Each can have a level range when they appear
    * Will not repeat the same challenge twice in a row
  * The player may right-click with an item in hand to sacrifice it directly or, if that doesn't
    match, it will open an inventory for the sacrifice to complete the challenge
  * Warns the player on an unsuccessful attempt to complete a challenge
* Count Down
  * Uses the XP bar as a 5 minute count down timer
  * Message and audio alerts to players as they approach the deadline
  * Update rate of the timer is configurable, defaults to 5 seconds
* Built-in character switching that saves inventory, health, hunger, experience, location, potion
  effects, custom book contents, etc.

TODO
----

* Documentation and plugin usage examples
* Regenerate the world at specific intervals or when there are no current players
* Some placeable items (like flowers) prevent the Oni inventory from opening
* Remove ability to enchant or repair items (would conflict with the game status indicators)
* Integrate scoreboard functionality (partially done already)
* Provide a timeout feature when players are disconnected (not exit) before regeneration
* Prevent enderman effects on the Oni platform

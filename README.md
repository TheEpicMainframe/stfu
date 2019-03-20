# Probably Octo Journey: CPSC 233 Project


# Running the demo

```
git clone https://github.com/jaredponn/probable-octo-journey
cd probable-octo-journey
./run.sh 
```

Note: this script only works with bash.

# Version number:
- 2019 February 24, Version 1.0 Pre-Alpha unstable release

- 2019 March 11, Version 1.1 Pre-Alpha half-stable release. Debug renderer is enabled and the red dots indicate collision box points.


# Architecture / UML diagrams
The architecture uses an entity component system that favors composition.

There are 2 UML diagrams provided, which can be found in the following locations:
```bash
./simplifiedumldiagram.png # simplified UML diagram without all the classes
./demo2umldiagram.png      # actual UML diagram with all the classes in it
```

# Announcements -- for Contributors:

## TODO List
- Powerups (more damage) - just need to handle spawning now (alex 03/20/19)
- Collectibles (HP refill, ammo refill, money) - just need to handle spawning now (alex 03/20/19)
- Finite ammo - DONE (alex 03/20/19)
- Buying ammo
- Aligning the aggro hit box properly 
- Actually using the aggro hit box
- Shrinking the PPhysicsHitBox of zombies and players
- Polishing zombie spawn points (perhaps 5 or 6 spawn points around the map. Also, for api design, consider lookingat the COOL_DOWN_KEYS variable in GameConfig.java for how to load the spawn points in.)
- Wave style of zombie spawning (e.g. a wave comes every 10 seconds and more zombies spawn as the game goes on)


- Boss zombies (may need more graphics from Ramiro for this)



- Melee attack
- Aligning hit boxes with the map


- Game over screen (restart / play again, go back to menu)


- Audio


- Turrets
- Bullet sprite
- Melee sprites with a more visible sword
- Slower zombie attacks


## Directory Hierarchy
The directory hierarchy is as follows:
```bash
src/    # source files
src/poj/ # game engine
```

## Coordinate System
The coordinate system is a little strange. The following diagram will illustrate:
```
     (-y / S)      (-x / W)
             \      /
              '    /
               \  /
                '/ (0,0)
                /\
               /  '
              /    \
             /      ' (+y / mapHeight / N )
  (+x / mapWidth / E)

- 1 mapWidth is one length of the x direction
- 1 mapHeight is one length of the y direction


```

## Using Optional in Java
Using Optional in Java (should be used for all values that may or may not exist):
```Java
Optional<Double> a = Optional.of(3d); // initilizes with value of 3
                // = Optional.empty(); // initlizes with no value
if (a.isEmpty())
{
	// handle error
}
else
{
	// do whatever
	a.get() // gets the value
}

// 2 ways to test if the value exists or not respectively
// .isPresent() / isEmpty()
```

# Credits
Tileset: https://opengameart.org/content/cave-tileset


Turret: https://opengameart.org/content/orange-defense-gun-isometric


Coin: https://opengameart.org/content/spinning-pixel-coin-0



Majority of the collision algorithms were from or built upon *Real-Time Collision Detection* by Christer Ericson, published by Morgan Kaufmann Publishers, Copyright 2005 Elsevier Inc.


The game engine design came from various posts and contributions from: https://jaredponn.github.io/ and from the project https://github.com/jaredponn/improved-octo-waffle


# Useful readings
Path-finding: http://www.cs.colorado.edu/~ralex/papers/PDF/OOPSLA06antiobjects.pdf


Collision detection and resolution: http://www.cs.colorado.edu/~ralex/papers/PDF/OOPSLA06antiobjects.pdf




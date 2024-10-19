# Inkball Project Feature List

## Window and Display
- [x] Window launches and shows level layout correctly (empty tiles, spawners, and walls).
- [x] Initial ball and hole display is correct.
- [x] Unspawned balls are shown in the top left corner (max 5) and move left 1px/frame when one spawns.

## Timers and Spawning
- [x] Ball spawn timer and level time are correct according to the configuration file.
- [x] Level time decreases each second.
- [x] Ball spawn timer decreases each second in increments of 0.1 seconds.
- [x] Balls spawn when the spawn timer reaches 0. A random spawner is chosen.

## Ball Movement and Collision
- [x] Balls have a random (x, y) trajectory when spawned that is (±2, ±2) px/frame and cannot be 0.
- [x] Balls collide with walls, and the new trajectory is calculated correctly to reflect the velocity vector off the surface.
- [x] No bugs exist with ball/wall collisions (i.e., balls cannot clip into walls or cling unnaturally to the edge of walls).

## Player Interaction
- [x] Players can draw lines in the game with the left mouse button, which are black and have a thickness of 10 units.
- [x] Players can remove drawn lines with the right mouse button or alternatively ctrl+left click.
- [ ] Player-drawn lines have a hitbox that reflects balls based on the normal vector of the line segment that’s hit. When a collision occurs, they are removed.

## Ball and Hole Mechanics
- [ ] When a ball comes close to a hole, it is attracted towards it with a force proportional to how close it is.
- [ ] When a ball comes close to a hole, its size reduces proportionally to how close it is to the hole.
- [ ] When a ball is directly above a hole, it is captured by the hole.
- [ ] When a ball of a different colour to the hole is captured by it, the ball enters the respawn queue, unless it is a grey ball or grey hole.

## Scoring
- [ ] Score changes correctly when balls are captured successfully or unsuccessfully, based on the colour of the ball and the score values specified in the config file, including the level multiplier.

## Game State and Controls
- [x] Spacebar causes the game to pause, and the top bar displays `*** PAUSED ***`.
- [ ] The current level ends in a win when no balls remain to be spawned, and no balls are currently on the game board.
  - [ ] Remaining time gets added to the player’s score at a rate of 1 unit every 0.067 seconds.
  - [ ] Yellow tiles originating in the top left corner and bottom right corner move around the edge of the game board in a clockwise direction at a rate of 1 tile every 0.067 seconds.
- [ ] When the level ends in a win, the next level is loaded.
- [ ] When the level timer reaches 0, the level ends in a loss, meaning balls stop moving and the player can no longer draw lines. Display `=== TIME’S UP ===` in the top bar.
- [ ] The player can press ‘r’ to restart a level at any time, including when time has run out.
- [ ] Once the game has ended, a player can restart the game by pressing ‘r’.

## Code Quality
- [ ] Ensure that your application does not repeat large sections of logic.
- [ ] Ensure that your application is bug-free.

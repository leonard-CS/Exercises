# To-Do List for Minesweeper Game

## Features to Implement

### Initial Setup
- [x] Window launches and shows initial layout correctly with blue cells.

### User Interactions
- [x] Hovering over tiles with cursor changes the blue tile image to highlight it.
- [x] Left clicking on a cell reveals it, showing the grey tile underneath.
- [x] On each frame, cells adjacent to a revealed blank cell are revealed.
- [ ] Right click on a blue cell flags it as containing a mine and shows the flag indicator. The tile can no longer be left-clicked to reveal it unless the flag is removed by right-clicking again.

### Game Mechanics
- [x] Mines are randomly placed on the board when the game starts based on the first command line argument (default 100 if not provided).
- [x] Timer counts up every second in the top-right corner of the screen and stops when the game ends.
- [ ] If a player reveals all tiles without clicking on any mines, they win the game.
- [x] Revealed tiles show the correct number and associated color depending on the count of adjacent mines (1-8 or blank).
- [x] If a player clicks on a mine, it explodes, and all other mines on the board explode as well. Display the message "You lost!".
- [x] Mine explosion is animated with each image lasting 3 frames.
- [ ] Mines explode in sequence such that each explosion begins 3 frames after the previous one begins.

### Restart Mechanism
- [x] Implement restart functionality where pressing the key ‘r’ restarts the game with a new set of random mines.

### Additional Notes
- [ ] Ensure that all features are tested to avoid breaking functionality.
- [ ] Verify that the game correctly handles edge cases and provides a smooth user experience.


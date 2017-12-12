Bots

RandomBot - changes a tile at a random position within the board, with a random color.

    Usage: java RandomBot <host name> <port number> <username>

ColorCycleBot - travels in a snake-like pattern eventually changing the entire board
to a certain color before moving on to the next color and starting at the beginning again.

    Usage: java ColorCycleBot <host name> <port number> <username>

ReplayBot - looks at a file of TILE_CHANGED requests "replay.txt", makes tile changes based
on the contents read.

    Usage: java ReplayBot <host name> <port number> <filename>

    Note: intended for use with 10x10 board, must use file path "src/place/replay.txt" as 3rd arg
    for filename


During tile changes, all changes are logged in a file "src/place/tile-changes.txt" which can then be
renamed "replay.txt" and used with ReplayBot
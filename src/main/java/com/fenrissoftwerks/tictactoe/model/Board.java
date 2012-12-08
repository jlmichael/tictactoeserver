package com.fenrissoftwerks.tictactoe.model;

import org.apache.log4j.Logger;

/**
 * Board - a representation of the tic-tac-toe model board
 * The board is represented internally as a 3x3 matrix of chars.  We also track the two Player sitting at the Board,
 * and whose turn it is to move.
 */
public class Board {

    private static Logger logger = Logger.getLogger(Board.class);
    private char[][] grid;
    private Player playerX;
    private Player playerO;

    private Player movingPlayer;

    /**
     * Crate a new, empty Board.
     */
    public Board() {
        grid = new char[3][3];
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                grid[i][j] = '-';
            }
        }
        movingPlayer = null;
    }

    /**
     * Reset the Board to an empty state, and clear both Players.
     */
    public void reset() {
        grid = new char[3][3];
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                grid[i][j] = '-';
            }
        }
        movingPlayer = null;
        playerX = null;
        playerO = null;
    }

    /**
     * get the grid representing the board
     * @return A 3x3 char array
     */
    public char[][] getGrid() {
        return grid;
    }

    /**
     * Set the board grid to the passed in 3x3 char array
     * @param grid The 3x3 array to set the grid to
     */
    public void setGrid(char[][] grid) {
        this.grid = grid;
    }

    /**
     * Get the player currently allowed to move
     * @return the player currently allowed to move
     */
    public Player getMovingPlayer() {
        return movingPlayer;
    }

    /**
     * Set the player currently allowed to move
     * @param movingPlayer The player to allow to move
     */
    public void setMovingPlayer(Player movingPlayer) {
        // Ignore attempts to set the moving player to a player other than one at the Board.
        if(movingPlayer != playerX && movingPlayer != playerO) {
            return;
        }
        this.movingPlayer = movingPlayer;
    }

    /**
     * Switch moving player to the next player
     */
    public void switchPlayer() {
        if(movingPlayer == playerX) {
            movingPlayer = playerO;
        } else {
            movingPlayer = playerX;
        }
        logger.debug("Switched moving player");
    }

    /**
     * Check if the given location on the board is empty
     * @param x the x coordinate of the space to check
     * @param y the y coordinate of the space to check
     * @return
     */
    public boolean spaceIsEmpty(int x, int y) {
        if(grid[x][y] == '-')
            return true;
        return false;
    }

    /**
     * Check the board state to see if the given Player has won the game.
     * @param playerLetter which Player letter (x or o) to check for the win
     * @return
     */
    public boolean playerWon(char playerLetter) {
        if((playerLetter == grid[0][0]) && (playerLetter == grid[0][1]) && (playerLetter == grid[0][2]))
            return true;
        if((playerLetter == grid[1][0]) && (playerLetter == grid[1][1]) && (playerLetter == grid[1][2]))
            return true;
        if((playerLetter == grid[2][0]) && (playerLetter == grid[2][1]) && (playerLetter == grid[2][2]))
            return true;
        if((playerLetter == grid[0][0]) && (playerLetter == grid[1][0]) && (playerLetter == grid[2][0]))
            return true;
        if((playerLetter == grid[0][1]) && (playerLetter == grid[1][1]) && (playerLetter == grid[2][1]))
            return true;
        if((playerLetter == grid[0][2]) && (playerLetter == grid[1][2]) && (playerLetter == grid[2][2]))
            return true;
        if((playerLetter == grid[0][0]) && (playerLetter == grid[1][1]) && (playerLetter == grid[2][2]))
            return true;
        if((playerLetter == grid[2][0]) && (playerLetter == grid[1][1]) && (playerLetter == grid[0][2]))
            return true;
        return false;
    }

    /**
     * Check to see if all spaces on the board are full.
     * @return
     */
    public boolean isFull() {
        if(grid[0][0] != '-' &&
                grid[0][1] != '-' &&
                grid[0][2] != '-' &&
                grid[1][0] != '-' &&
                grid[1][1] != '-' &&
                grid[1][2] != '-' &&
                grid[2][0] != '-' &&
                grid[2][1] != '-' &&
                grid[2][2] != '-') {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the given move is legal
     * To be legal, it must be the Player's turn, the space must be empty, and the coordinates must be valid
     * @param player The Player making the move
     * @param x the x coordinate of the move
     * @param y the y coordinate of the move
     * @return
     */
    public boolean isMoveLegal(Player player, int x, int y) {
        if((x < 0) || (x > 2)) {
            return false;
        }
        if((y < 0) || (y > 2)) {
            return false;
        }
        if(movingPlayer != player)
            return false;
        if(!spaceIsEmpty(x, y))
            return false;
        return true;
    }

    /**
     * Place the player's letter at the given spot
     * @param player The Player making the move
     * @param x the x coordinate of the move
     * @param y the y coordinate of the move
     * @return
     */
    public Boolean placeLetter(Player player, int x, int y) {
        if(isMoveLegal(player, x, y)) {
            grid[x][y] = player.getPlayerLetter();
            return true;
        }
        return false;
    }

    /**
     * Get the Player playing the X's
     * @return the Player playing the X's
     */
    public Player getPlayerX() {
        return playerX;
    }

    /**
     * Set the Player playing the X's
     * @param playerX the Player to set to Player X
     */
    public void setPlayerX(Player playerX) {
        this.playerX = playerX;
        playerX.setPlayerLetter('x');
    }

    /**
     * Get the Player playing the O's
     * @return the Player playing the O's
     */
    public Player getPlayerO() {
        return playerO;
    }

    /**
     * Set the Player playing the O's
     * @param playerO the Player to set to Player O
     */
    public void setPlayerO(Player playerO) {
        this.playerO = playerO;
        playerO.setPlayerLetter('o');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (movingPlayer != board.movingPlayer) return false;
        for(int i=0;i<3;i++) {
            for(int j=0;j<3;j++) {
                if(grid[i][j] != board.getGrid()[i][j]) return false;
            }
        }

        return true;
    }

}

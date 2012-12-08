package com.fenrissoftwerks.tictactoe.model;

import junit.framework.TestCase;
import org.junit.Test;

public class BoardTest extends TestCase {

    @Test
    public void testGetGrid() {
        Board board = new Board();
        char[][] grid = board.getGrid();
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                assertTrue(grid[x][y] == '-');
            }
        }

    }

    @Test
    public void testSetGrid() {
        char[][] grid = new char[3][3];
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                grid[x][y] = 'X';
            }
        }
        Board board = new Board();
        board.setGrid(grid);
        grid = board.getGrid();
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                assertTrue(grid[x][y] == 'X');
            }
        }
    }

    @Test
    public void testGetAndSetMovingPlayer() {
        Board board = new Board();
        Player player1 = new Player();
        Player player2 = new Player();
        board.setPlayerX(player1);
        board.setPlayerO(player2);
        assertNull(board.getMovingPlayer());
        board.setMovingPlayer(player1);
        assertEquals(player1, board.getMovingPlayer());
    }

    @Test
    public void testSwitchPlayer() {
        Board board = new Board();
        Player player1 = new Player();
        player1.setPlayerLetter('X');
        Player player2 = new Player();
        player2.setPlayerLetter('O');
        board.setPlayerX(player1);
        board.setPlayerO(player2);
        board.setMovingPlayer(player1);
        assertEquals(player1, board.getMovingPlayer());
        board.switchPlayer();
        assertEquals(player2, board.getMovingPlayer());
    }

    @Test
    public void testIsMoveLegal() {
        Board board = new Board();
        Player player1 = new Player();
        player1.setPlayerLetter('X');
        board.setPlayerX(player1);
        board.setMovingPlayer(player1);
        assertTrue(board.isMoveLegal(player1, 0, 0));
        board.placeLetter(player1, 0, 0);
        assertFalse(board.isMoveLegal(player1, 0, 0));
    }

    @Test
    public void testPlayerWon() {

    }
}

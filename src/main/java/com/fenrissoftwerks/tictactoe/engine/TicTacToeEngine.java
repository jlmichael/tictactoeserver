package com.fenrissoftwerks.tictactoe.engine;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.GameEngine;
import com.fenrissoftwerks.loki.commandhandler.AbstractCommandHandler;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import com.fenrissoftwerks.tictactoe.commandhandler.*;
import com.fenrissoftwerks.tictactoe.model.Board;
import com.fenrissoftwerks.tictactoe.model.Player;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Constructor;

/**
 * TicTacToeEngine - a Loki GameEngine subclass that just stores the game Board and handles client disconnect logic.
 */
public class TicTacToeEngine extends GameEngine {

    private static Logger logger = Logger.getLogger(TicTacToeEngine.class);
    public static final String COMMAND_NAME_DUMMY = "dummy";
    public static final String COMMAND_NAME_CREATE_PLAYER = "createPlayer";
    public static final String COMMAND_NAME_JOIN_BOARD = "joinBoard";
    public static final String COMMAND_NAME_LOGIN = "login";
    public static final String COMMAND_NAME_PLAYER_MOVE = "playerMove";

    private static Board board = new Board();


    public Board getBoard() {
        return board;
    }

    /**
     * Clean up game state in the event of a client disconnect
     * If a connected client disconnects, this method will remove them from the game if they are one of the players
     * and reset the game board state so that a new game can begin.
     * @param ctx
     */
    @Override
    public void handleClientDisconnect(ChannelHandlerContext ctx) {
        // Get the Player for this client, if any
        Player player = (Player)ctx.getAttachment();

        // If the disconnected player was active on the board, reset the board and notify the watchers.
        Player playerX = board.getPlayerX();
        Player playerO = board.getPlayerO();
        if(player != null && (player.equals(playerX) || player.equals(playerO))) {
            board.reset();
            Command command = new Command();
            String message = "Player " + player.getName() + " got disconnected.  The board has been reset.";
            command.setCommandName("displayAcknowledgment");
            command.setCommandArgs(new Object[] {message});
            GameServer.getInstance().sendCommandToWatchers(command, board);

            command.setCommandName("updateBoard");
            command.setCommandArgs(new Object[] {board});
            GameServer.getInstance().sendCommandToWatchers(command, board);
        }
    }
}

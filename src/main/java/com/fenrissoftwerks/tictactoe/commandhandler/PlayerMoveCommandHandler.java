package com.fenrissoftwerks.tictactoe.commandhandler;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.GameEngine;
import com.fenrissoftwerks.loki.commandhandler.AbstractCommandHandler;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import com.fenrissoftwerks.tictactoe.engine.TicTacToeEngine;
import com.fenrissoftwerks.tictactoe.model.Board;
import com.fenrissoftwerks.tictactoe.model.Player;
import com.fenrissoftwerks.tictactoe.util.ClientMessageUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * PlayerMoveCommandHandler - A class for executing player moves on the game board.
 */
public class PlayerMoveCommandHandler extends AbstractCommandHandler {
    private static Logger logger = Logger.getLogger(PlayerMoveCommandHandler.class);

    public PlayerMoveCommandHandler(ChannelHandlerContext ctx, GameEngine engine) {
        this.ctx = ctx;
        this.engine = engine;
    }

    /**
     * Execute the given command
     * This handler checks to see if the player is authenticated, and if it is there turn to place a letter on the
     * game board.  If it is, and the move is valid, it updates the Board and notifies watchers of the change.
     * @param command
     */
    @Override
    public void executeCommand(Command command) {
        GameServer server = GameServer.getInstance();

        // check if we are authenticated
        Player player = (Player)ctx.getAttachment();
        if(player == null) {
            // Not authenticated
            String message = "You must be logged in to join a board.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // Get the move
        Object[] args = command.getCommandArgs();
        if(args == null || args.length != 2) {
            String message = "Invalid move.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }
        
        // Get the board from the server
        Board board = ((TicTacToeEngine)engine).getBoard();

        // Check the space
        Integer x = (Integer)args[0];
        Integer y = (Integer)args[1];

        // Attempt the move
        if(!board.placeLetter(player, x, y)) {
            String message = "Not a valid move.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // Move was legal and made.  Switch players.
        board.switchPlayer();

        // If the player has won, send notification
        String winner = null;
        if(board.playerWon('x')) {
            winner = "X";
        } else if(board.playerWon('o')) {
            winner = "O";
        }
        if(winner != null) {
            board.reset();
            String message = "Game over, " + winner + " wins!";
            Command gameOverCommand = new Command();
            gameOverCommand.setCommandName("displayAcknowledgment");
            gameOverCommand.setCommandArgs(new Object[] {message});
            server.sendCommandToWatchers(gameOverCommand, board);
        } else if(board.isFull()) {
            board.reset();
            String message = "Game over, it's a draw.";
            Command gameOverCommand = new Command();
            gameOverCommand.setCommandName("displayAcknowledgment");
            gameOverCommand.setCommandArgs(new Object[] {message});
            server.sendCommandToWatchers(gameOverCommand, board);
        }

        // Notify clients to update board
        Command updateBoardCommand = new Command();
        updateBoardCommand.setCommandName("updateBoard");
        updateBoardCommand.setCommandArgs(new Object[] {board});
        server.sendCommandToWatchers(updateBoardCommand, board);

    }
}
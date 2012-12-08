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
 * JoinBoardCommandHandler - A CommandHandler for letting a Player join the Board as one of the two players.
 */
public class JoinBoardCommandHandler extends AbstractCommandHandler {
    private static Logger logger = Logger.getLogger(JoinBoardCommandHandler.class);

    public JoinBoardCommandHandler(ChannelHandlerContext ctx, GameEngine engine) {
        this.ctx = ctx;
        this.engine = engine;
    }

    /**
     * Execute the given Command
     * This handler first checks to see if the Player is authenticated/logged in.  If they are, and there is space at
     * the game board, they are assigned one of the two spots at the table.  It then sends notifications to all
     * watchers of the Board.
     * @param command The Command to execute
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

        // Get the board from the server
        Board board = ((TicTacToeEngine)engine).getBoard();

        // If no player X, set this player to player x
        if(board.getPlayerX() == null) {
            logger.debug("Setting player to X");
            board.setPlayerX(player);
            board.setMovingPlayer(player);
        } else if(board.getPlayerO() == null) {
            logger.debug("Setting player to O");
            board.setPlayerO(player);
        } else {
            // No room for the player.
            logger.debug("No room for player");
            String message = "The board is full";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        server.addWatcherForObject(board, ctx.getChannel());

        // Update both players with board state
        Command updateBoardCommand = new Command();
        updateBoardCommand.setCommandName("updateBoard");
        updateBoardCommand.setCommandArgs(new Object[] {board});
        server.sendCommandToWatchers(updateBoardCommand, board);
    }
}

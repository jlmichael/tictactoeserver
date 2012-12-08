package com.fenrissoftwerks.tictactoe.commandhandler;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.GameEngine;
import com.fenrissoftwerks.loki.commandhandler.AbstractCommandHandler;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import com.fenrissoftwerks.tictactoe.engine.TicTacToeEngine;
import com.fenrissoftwerks.tictactoe.model.Board;
import com.fenrissoftwerks.tictactoe.util.ClientMessageUtil;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * SyncBoardCommandHandler - A CommandHandler for notifying clients of the state of the game Board.
 */
public class SyncBoardCommandHandler extends AbstractCommandHandler {

    public SyncBoardCommandHandler(ChannelHandlerContext ctx, GameEngine engine) {
        this.ctx = ctx;
        this.engine = engine;
    }

    /**
     * Execute the given command
     * This handler creates a new command (named updateBoard) and stores the current state of the Board in it.
     * It then sends it to the associated client.
     * @param command
     */
    @Override
    public void executeCommand(Command command) {
        GameServer server = GameServer.getInstance();
        Board board = ((TicTacToeEngine)engine).getBoard();
        
        Command updateBoardCommand = new Command();
        updateBoardCommand.setCommandName("updateBoard");
        updateBoardCommand.setCommandArgs(new Object[] {board});
        server.sendCommandToClient(updateBoardCommand, ctx.getChannel());
    }
}

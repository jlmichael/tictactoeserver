package com.fenrissoftwerks.tictactoe.commandhandler;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.GameEngine;
import com.fenrissoftwerks.loki.commandhandler.AbstractCommandHandler;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import com.fenrissoftwerks.tictactoe.engine.TicTacToeEngine;
import com.fenrissoftwerks.tictactoe.model.Board;
import com.fenrissoftwerks.tictactoe.model.Player;
import com.fenrissoftwerks.tictactoe.util.ClientMessageUtil;
import com.fenrissoftwerks.tictactoe.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * LoginHandler - a CommandHandler for logging a player in to the server
 */
public class LoginHandler extends AbstractCommandHandler {
    private static Logger logger = Logger.getLogger(PlayerMoveCommandHandler.class);

    public LoginHandler(ChannelHandlerContext ctx, GameEngine engine) {
        this.ctx = ctx;
        this.engine = engine;
    }

    /**
     * Execute the given command
     * This command gets the Player name and unsalted pw from the Command object, salts and hashes the password,
     * and verifies that it matches what is in the DB for that Player.  If they match, the Client's
     * ChannelWatcherContext has the Player object attached (this is how the game determines if a given client is
     * authenticated)
     * @param command
     */
    @Override
    public void executeCommand(Command command) {
        GameServer server = GameServer.getInstance();

        // Check that we have a name and password
        Object[] args = command.getCommandArgs();
        if(args == null || args.length != 2) {
            logger.error("Got null args in LoginHandler");
            String message = "Login error: one or more parameters is missing.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // Get args
        String playerName = (String)args[0];
        String unsaltedPassword = (String)args[1];

        // Get the player by name
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Player WHERE name = :name");
        query.setString("name", playerName);
        Player player = (Player)query.uniqueResult();
        if(player == null) {
            // No player
            String message = "Authentication error.  Please check your username and password.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // Check passwords
        String saltedPassword = Player.saltAndHashPassword(unsaltedPassword);
        if(!player.getPassword().equals(saltedPassword)) {
            String message = "Authentication error.  Please check your username and password.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // They match, store the Player as an attachment to this channel handler context
        ctx.setAttachment(player);

        // Send acknowlegement message
        String message = "You have successfully logged in.";
        ClientMessageUtil.sendAcknowledgmentToClient(server, ctx, message);

        // Bind the client to the Player and Board objects
        server.addWatcherForObject(player, ctx.getChannel());
        Board board = ((TicTacToeEngine)engine).getBoard();
        server.addWatcherForObject(board, ctx.getChannel());

        // Notify the client that they have successfully logged in
        Command loginSuccessfulCommand = new Command();
        loginSuccessfulCommand.setCommandName("loginSuccessful");
        loginSuccessfulCommand.setCommandArgs(new Object[] {player});
        server.sendCommandToWatchers(loginSuccessfulCommand, player);

    }
}

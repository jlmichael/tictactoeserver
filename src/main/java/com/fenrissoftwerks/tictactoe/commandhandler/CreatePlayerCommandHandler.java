package com.fenrissoftwerks.tictactoe.commandhandler;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.GameEngine;
import com.fenrissoftwerks.loki.commandhandler.AbstractCommandHandler;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import com.fenrissoftwerks.tictactoe.model.Player;
import com.fenrissoftwerks.tictactoe.util.ClientMessageUtil;
import com.fenrissoftwerks.tictactoe.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * CreatePlayerCommandHandler - A CommandHandler for creating a player account
 */
public class CreatePlayerCommandHandler extends AbstractCommandHandler {
    private static Logger logger = Logger.getLogger(CreatePlayerCommandHandler.class);

    public CreatePlayerCommandHandler(ChannelHandlerContext ctx, GameEngine engine) {
        this.ctx = ctx;
        this.engine = engine;
    }

    /**
     * Execute the passed in Command
     * This will fetch the player name and pw from the Command, salt and hash the pw, and store the player object
     * in the DB.  It will then send a confirmation message to the client.
     * @param command The Command to execute
     */
    @Override
    public void executeCommand(Command command) {
        GameServer server = GameServer.getInstance();

        // Verify required info is in command
        Object[] args = command.getCommandArgs();
        if(args.length != 2) {
            logger.error("Incorrect number of arguments to CreatePlayerCommandHandler");
            // Send a displayErrorCommand
            Command displayErrorCommand = new Command();
            displayErrorCommand.setCommandName("displayError");
            String message = "Incorrect number of arguments to CreatePlayerCommandHandler";
            displayErrorCommand.setCommandArgs(new Object[] {message});
            server.sendCommandToClient(displayErrorCommand, ctx.getChannel());
            return;
        }

        String playerName = (String)args[0];

        // Verify that player name isn't already taken
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Player WHERE name = :name");
        query.setString("name", playerName);
        if(query.uniqueResult() != null) {
            // Already in use
            String message = "The name you have chosen is already in use.  Please try a different name.";
            ClientMessageUtil.sendErrorMessageToClient(server, ctx, message);
            return;
        }

        // Name isn't taken, create and serialize the Player object
        String saltedPassword = Player.saltAndHashPassword((String)args[1]);
        Player player = new Player();
        player.setName(playerName);
        player.setPassword(saltedPassword);
        session.beginTransaction();
        session.save(player);
        session.getTransaction().commit();

        // Send the acknowledgement
        String message = "Player " + playerName + " created.  Login to begin playing.";
        ClientMessageUtil.sendAcknowledgmentToClient(server, ctx, message);
        return;

    }
}

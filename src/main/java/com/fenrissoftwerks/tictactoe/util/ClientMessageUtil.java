package com.fenrissoftwerks.tictactoe.util;

import com.fenrissoftwerks.loki.Command;
import com.fenrissoftwerks.loki.gameserver.GameServer;
import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * ClientMessageUtil - A library for sending error and acknowledgements to clients
 */
public class ClientMessageUtil {

    /**
     * Send an error message to the client identified by ctx, connected to server
     * @param server The GameServer the client is connected to
     * @param ctx The ChannelHandlerContext identifying the client
     * @param message The message to send
     */
    public static void sendErrorMessageToClient(GameServer server, ChannelHandlerContext ctx, String message) {
        Command displayErrorCommand = new Command();
        displayErrorCommand.setCommandName("displayError");
        displayErrorCommand.setCommandArgs(new Object[] {message});
        server.sendCommandToClient(displayErrorCommand, ctx.getChannel());
    }
    /**
     * Send an acknowledgement message to the client identified by ctx, connected to server
     * @param server The GameServer the client is connected to
     * @param ctx The ChannelHandlerContext identifying the client
     * @param message The message to send
     */
    public static void sendAcknowledgmentToClient(GameServer server, ChannelHandlerContext ctx, String message) {
        Command displayErrorCommand = new Command();
        displayErrorCommand.setCommandName("displayAcknowledgment");
        displayErrorCommand.setCommandArgs(new Object[] {message});
        server.sendCommandToClient(displayErrorCommand, ctx.getChannel());
    }


}

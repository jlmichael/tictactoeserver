package com.fenrissoftwerks.tictactoe;

import com.fenrissoftwerks.loki.gameserver.GameServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * TicTacToe - The main entry point to the Game.  Implemented as a HttpServlet (though it does not accept any
 * HTTP verbs...).  At initialization, it just fetches the instance of the TicTacToe GameServer implementation and
 * calls startServer() on it.
 */
public class TicTacToe extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("Starting up GameServer instance");
        GameServer server = GameServer.getInstance();

        try {
            server.startServer();
        } catch (Exception e) {
            System.out.println("Unhandled exception while trying to start server");
            e.printStackTrace();
        }
    }

}

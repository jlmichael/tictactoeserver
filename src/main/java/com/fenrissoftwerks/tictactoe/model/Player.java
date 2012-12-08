package com.fenrissoftwerks.tictactoe.model;

import com.fenrissoftwerks.loki.util.SkipWhenSerializing;
import org.apache.log4j.Logger;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Player - a class representing a Player in a game of TicTacToe
 */
@Entity
public class Player {

    private static String SALT = "You probably want to change this.";

    @Transient
    private static Logger logger = Logger.getLogger(Player.class);

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id")
    private int playerId;

    private String name;

    @SkipWhenSerializing
    private String password;

    @Transient
    @SkipWhenSerializing
    private char playerLetter;

    public Player() {
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public char getPlayerLetter() {
        return playerLetter;
    }

    public void setPlayerLetter(char playerLetter) {
        this.playerLetter = playerLetter;
    }

    public static String saltAndHashPassword(String unsaltedPassword) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Couldn't generate SHA1 hash");
        }
        digest.update((unsaltedPassword + SALT).getBytes());

        return bytes2String(digest.digest());
    }
    
    private static String bytes2String(byte[] bytes) {
        StringBuilder string = new StringBuilder();
        for (byte b: bytes) {
                String hexString = Integer.toHexString(0x00FF & b);
                string.append(hexString.length() == 1 ? "0" + hexString : hexString);
        }
        return string.toString();
    }

}

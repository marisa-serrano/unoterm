package org.academiadecodigo.wizards;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringSetInputScanner;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
user name
hand - tipo cards

 */
public class Player implements Runnable {

    private String name;
    private LinkedList<Card> hand;
    private Socket playerSocket;
    private Prompt prompt; //só um prompt e vários scanners;

    private BufferedReader in;
    private BufferedWriter out;

    public Player(Socket playerSocket) {
        this.playerSocket = playerSocket;
        hand = new LinkedList<>();
        try {
            prompt = new Prompt(playerSocket.getInputStream(), new PrintStream(playerSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(playerSocket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void chooseName() {
        StringInputScanner nameScanner = new StringInputScanner();
        nameScanner.setMessage("What's your user name?");
        name = prompt.getUserInput(nameScanner);
    }

    public String getName() {
        return name;
    }

    public void chooseCard() throws IOException {
        Set<String> handSet = new HashSet<>();
        for (Card card : hand) {
            handSet.add(card.toString());
        }
        StringSetInputScanner cardScanner = new StringSetInputScanner(handSet);
        cardScanner.setMessage("Play a card.");
        cardScanner.setError("You don't have that card!");
        String cardString = prompt.getUserInput(cardScanner).toUpperCase();
        out.write(cardString);
    }

    public LinkedList<Card> getHand() {
        return hand;
    }

    @Override
    public void run() {

    }

}

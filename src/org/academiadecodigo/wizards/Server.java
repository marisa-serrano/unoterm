package org.academiadecodigo.wizards;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
TODO: instanciar a thread;
TODO: menu;
TODO: após adicionar as theards e ter vários clientes:
  TODO: método showHand;
    - ter vários jogadores - À medida que se vão juntando até chegra a 4
    - o primeiro jogador só joga quando todos os players tiverem colocado o user name - dar print para play;

------------ COMPLEXIDADE --------------------
- ter que dizer UNO para ganhar;
- anunciar o numero da cartas de cada jogador a cada jogada;
- add: reverse, +2 e block;

 */
public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    public static int port;
    public static int playerNum;
    private LinkedList<Player> players;
    private ExecutorService fixedPool;
    private BufferedReader in;
    private BufferedWriter out;
    private Deck deck;
    private LinkedList<Card> discardedPile;

    private Card lastCardPlayed;
    private Card cardPlayed;

    public static void main(String[] args) {
        // Grab port number and max player count from args, else initialize with some defaults
        if (args.length != 2) {
            port = 8080;
            playerNum = 4;
        }   else {
            port = Integer.parseInt(args[0]);
            playerNum = Integer.parseInt(args[1]);
        }

        Server server = new Server();
        try {
            server.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(port);
            players = new LinkedList<>();
            deck = new Deck();
            deck.createDeck();
            discardedPile = new LinkedList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fixedPool = Executors.newFixedThreadPool(playerNum);
    }

    public void listen() throws IOException {
        while (players.size() != playerNum) {
            clientSocket = serverSocket.accept();
            Player player = new Player(clientSocket);
            players.add(player);
            fixedPool.submit(player);
            player.getOut().write("You have successfully joined!\n Waiting for other players...");
            player.getOut().flush();
        }
    }

    public void gameLoop() {
        /*getFirstCard();
        giveHands();
        player.chooseCard();
        compareCards(player);
        playCard(player);*/
    }

    public void getFirstCard() {
        int num = (int) (Math.random() * deck.getCards().size());
        lastCardPlayed = deck.getCards().get(num);
        deck.getCards().remove(num);
    }

    public void giveHands() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < 4; j++) {
                int num = (int) (Math.random() * deck.getCards().size());
                players.get(i).getHand().add(deck.getCards().get(num));
                deck.getCards().remove(num);
            }
        }
    }

    public void compareCards(Player player) throws IOException {
        String cardPlayedString = in.readLine();
        for (int i = 0; i < deck.getCards().size(); i++) {
            if (deck.getCards().get(i).toString().equals(cardPlayedString)) {
                cardPlayed = deck.getCards().get(i);
                break;
            }
        }
        if (cardPlayed.getColor().equals(lastCardPlayed.getColor()) || cardPlayed.getNum() == lastCardPlayed.getNum()) {
            playCard(player);
        } else {
            out.write("You can't play that card!");
            player.chooseCard();
            compareCards(player);
        }
    }

    public void playCard(Player player) {
        discardedPile.add(cardPlayed);
        lastCardPlayed = cardPlayed;
        for (int i = 0; i < player.getHand().size(); i++) {
            if (player.getHand().get(i).equals(cardPlayed)) {
                player.getHand().remove(i);
                break;
            }
        }
    }

    public void checkWin(){
        for (Player p : players) {
            if(p.getHand().size()==0){
                System.out.println(p.getName() + " won!");
            }

        }
    }

}

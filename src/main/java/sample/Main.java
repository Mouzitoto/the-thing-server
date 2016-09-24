package sample;

import com.esotericsoftware.kryonet.Connection;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.stage.Stage;
import sample.game.*;
import sample.network.NetworkServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public static List<Player> players = new ArrayList<Player>();
    public static Map<Player, Channel> playerConnections= new HashMap<Player, Channel>();
    public static int moveDirection = 1;
    public static List<Card> deck;
    public static String nowMovingPlayerName;
    public static NetworkServer server;
    public static boolean isGameStarted = false;
    public static boolean isAbleToGetCard = false;


    @Override
    public void start(Stage primaryStage) throws Exception{
        server = new NetworkServer().start();
    }

    public static void main(String[] args) {
        System.out.println("Server started");
        launch(args);
    }

    public static void startTheGame() {
        deck = Utils.createDeck(players.size());
        deck = Utils.give4CardsToPlayers(deck, players.size());
    }
}

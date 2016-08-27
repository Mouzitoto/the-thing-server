package sample;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.binding.ExpressionHelperBase;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import sample.game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public static List<Player> players = new ArrayList<Player>();
    public static List<Player> alivePlayers;
    public static Map<Player, Connection> playerConnections= new HashMap<Player, Connection>();
    public static int moveDirection = 1;
    public static List<Card> deck;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Server server = new Server();
        server.start();
        server.bind(27015, 27016);

        Kryo kryo = server.getKryo();
        kryo.register(NetworkMessage.class, 111);
        kryo.register(Player.class, 112);
        kryo.register(ArrayList.class, 113);
        kryo.register(Point2D.class, 114);
        kryo.register(Card.class, 115);
//        kryo.register(CardTypes.class, 116);
        kryo.register(CardActions.class, 117);



        server.addListener(new ServerListener());
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

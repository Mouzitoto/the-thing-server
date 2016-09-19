package sample;

import com.esotericsoftware.kryonet.Connection;
import javafx.application.Application;
import javafx.stage.Stage;
import sample.game.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import sample.network.NetworkServer;

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
    public static String nowMovingPlayerName;
    public static NetworkServer server;


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

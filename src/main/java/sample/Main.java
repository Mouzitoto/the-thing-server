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
import sample.game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    public static List<Player> players = new ArrayList<Player>();
    public static Map<Player, Connection> playerConnections= new HashMap<Player, Connection>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Server server = new Server();
        server.start();
        server.bind(27015, 27016);

        Kryo kryo = server.getKryo();
        kryo.register(NetworkMessage.class);
        kryo.register(Player.class);
        kryo.register(ArrayList.class);
        kryo.register(Point2D.class);
        kryo.register(Class.class);

        server.addListener(new ServerListener());
    }

    public static void main(String[] args) {
        System.out.println("Server started");
        launch(args);
    }
}

package sample;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import javafx.application.Application;
import javafx.stage.Stage;

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

        server.addListener(new ServerListener());
    }

    public static void main(String[] args) {
        System.out.println("Server started");
        launch(args);
    }
}

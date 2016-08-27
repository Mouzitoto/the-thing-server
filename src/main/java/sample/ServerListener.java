package sample;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import sample.game.Player;
import sample.game.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ruslan.babich on 26.07.2016.
 */
public class ServerListener extends Listener {
    public void received(Connection connection, Object object) {
        if (object instanceof NetworkMessage) {
            NetworkMessage message = (NetworkMessage) object;

            //HandShake
            if (message.getType().equals(NetworkMessage.HANDSHAKE)) {
                System.out.println(NetworkMessage.HANDSHAKE + " received from "
                        + connection.getRemoteAddressTCP().getHostString() + " "
                        + message.getPlayer().getName());

                Player player = new Player();
                player.setName(message.getPlayer().getName());
                //todo: add playerID from message to player

                //if it is the first person in lobby
                if (Main.players.size() == 0)
                    player.setGameOwner(true);

                Main.players.add(player);
                Main.playerConnections.put(player, connection);

                NetworkMessage messageOut = new NetworkMessage();
                messageOut.setType(NetworkMessage.NEW_PLAYER);
                messageOut.setPlayers(Main.players);

                broadcastAll(messageOut);
            }

            //Send message to chat window
            if (message.getType().equals(NetworkMessage.SEND_CHAT_MESSAGE)) {
                System.out.println(NetworkMessage.SEND_CHAT_MESSAGE + " received from "
                        + connection.getRemoteAddressTCP().getHostString() + " "
                        + message.getPlayer().getName());

                broadcastAll(message);
            }

            //START GAME
            if (message.getType().equals(NetworkMessage.START_GAME)) {
                System.out.println(NetworkMessage.START_GAME + " received from "
                        + connection.getRemoteAddressTCP().getHostString() + " "
                        + message.getPlayer().getName());

                Main.alivePlayers = new ArrayList<Player>(Main.players);
                Main.nowMovingPlayerName = Main.alivePlayers.get(0).getName();

                message.setNowMovingPlayerName(Main.nowMovingPlayerName);
                message.setPlayers(Main.players);
                message.setAlivePlayers(Main.alivePlayers);


                broadcastAll(message);

                Main.startTheGame();
            }
        }

    }

    @Override
    public void disconnected (Connection connection) {
        super.disconnected(connection);

        if (Main.playerConnections.size() == 0)
            return;

        Iterator it = Main.playerConnections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (connection.getID() == ((Connection) pair.getValue()).getID()) {
                Main.players.remove(pair.getKey());
                if (((Player) pair.getKey()).isGameOwner())
                    if(Main.players.size() > 0)
                        Main.players.get(0).setGameOwner(true);
                it.remove();
            }
        }

        NetworkMessage message = new NetworkMessage();
        message.setType(NetworkMessage.PLAYER_QUIT);
        message.setPlayers(Main.players);
        broadcastAll(message);

    }


    public void broadcast(NetworkMessage message, Connection connection) {
        System.out.println("broadcasting...");
        for (Player player : Main.players) {
            if (Main.playerConnections.get(player) != connection) {
                Main.playerConnections.get(player).sendTCP(message);
                System.out.println("message was sent to " + Main.playerConnections.get(player).getRemoteAddressTCP().getHostString() + " " + player.getName());
            }
        }
    }

    public void broadcastAll(NetworkMessage message) {
        System.out.println("broadcasting to all...");
        for (Player player : Main.players) {
            Main.playerConnections.get(player).sendTCP(message);
            System.out.println("message was sent to " + Main.playerConnections.get(player).getRemoteAddressTCP().getHostString() + " " + player.getName());
        }
    }
}

package sample;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.sun.org.apache.xpath.internal.SourceTree;

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

                broadcastAll(message);
            }
        }

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
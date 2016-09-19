package sample.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sample.Main;
import sample.game.Player;

import java.net.InetSocketAddress;

/**
 * Created by ruslan.babich on 019 19.09.2016.
 */
public class NetworkServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof NetworkMessage) {
            NetworkMessage message = (NetworkMessage) msg;
            String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
            String playerName = message.getPlayer().getName();

            //HandShake
            if (message.getType().equals(NetworkMessage.HANDSHAKE)) {
                System.out.println(message.getType() + " received from " + host + " " + playerName);

                Player player = new Player();
                player.setName(message.getPlayer().getName());
                //todo: add playerID from message to player

                //if it is the first person in lobby
                if (Main.players.size() == 0)
                    player.setGameOwner(true);

                Main.players.add(player);
                Main.playerConnections.put(player, ctx.channel());

                NetworkMessage messageOut = new NetworkMessage();
                messageOut.setType(NetworkMessage.NEW_PLAYER);
                messageOut.setPlayers(Main.players);

                broadcastAll(messageOut);
            }
        }
    }

    public void broadcastAll(NetworkMessage message) {
        System.out.println("broadcasting to all...");
        for (Player player : Main.players) {
            Main.playerConnections.get(player).writeAndFlush(message);
            String host = ((InetSocketAddress) Main.playerConnections.get(player).remoteAddress()).getAddress().getHostAddress();
            System.out.println("    message was sent to " + host + " " + player.getName());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

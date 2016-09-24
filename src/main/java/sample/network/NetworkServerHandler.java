package sample.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sample.Main;
import sample.game.CardTypes;
import sample.game.Player;
import sample.game.Utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

            //HANDSHAKE
            //HANDSHAKE
            //HANDSHAKE
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

            //SEND CHAT MESSAGE
            //SEND CHAT MESSAGE
            //SEND CHAT MESSAGE
            if (message.getType().equals(NetworkMessage.SEND_CHAT_MESSAGE)) {
                System.out.println(message.getType() + " received from " + host + " " + playerName);

                broadcastAll(message);
            }

            //START GAME
            //START GAME
            //START GAME
            if (message.getType().equals(NetworkMessage.START_GAME)) {
                System.out.println(message.getType() + " received from " + host + " " + playerName);

//                Main.alivePlayers = new ArrayList<Player>(Main.players);

                Main.nowMovingPlayerName = Main.players.get(0).getName();
                Main.isAbleToGetCard = true;

                message.setNowMovingPlayerName(Main.nowMovingPlayerName);
                message.setPlayers(Main.players);
//                message.setAlivePlayers(Main.alivePlayers);

                broadcastAll(message);
            }

            //NEW PLAYER POSITIONS
            if (message.getType().equals(NetworkMessage.NEW_PLAYER_POSITIONS)) {
                System.out.println(message.getType() + " received from " + host + " " + playerName);

                for (Player player : message.getPlayers())
                    for (Player p : Main.players)
                        if (p.getName().equals(player.getName()))
                            p.setTabletopPosition(player.getTabletopPosition());

                if (!Main.isGameStarted) {
                    Main.isGameStarted = true;
                    Main.startTheGame();
                }
            }


            //GET CARD FROM DECK
            //GET CARD FROM DECK
            //GET CARD FROM DECK
            if (message.getType().equals(NetworkMessage.GET_CARD_FROM_DECK)) {
                System.out.println(message.getType() + " received from " + host + " " + playerName);

                if (message.getPlayer().getName().equals(Main.nowMovingPlayerName) && Main.isAbleToGetCard) {
                    if (Main.deck.get(0).getType().equals(CardTypes.event.name())) {
                        //send event card to player (now moving)
                        Main.deck = Utils.giveCardToPlayer(Main.deck, Utils.findPlayerByConnection(ctx));
                        //send to others that player has +1 event card
                        message.setType(NetworkMessage.OTHER_PLAYER_GET_EVENT_CARD_FROM_DECK);
                        message.setPlayers(Main.players);
                        broadcastAllExceptMe(message, ctx);
                    } else {
                        //send panic card to player (now moving)
                        Main.deck = Utils.giveCardToPlayer(Main.deck, Utils.findPlayerByConnection(ctx));
                        //send to others that player has +1 panic card
                        message.setType(NetworkMessage.OTHER_PLAYER_GET_PANIC_CARD_FROM_DECK);
                        message.setCard(Main.deck.get(0));
                        broadcastAllExceptMe(message, ctx);
                    }
                    Main.isAbleToGetCard = false;
                }
            }
        }
    }

    public void broadcastAll(NetworkMessage message) {
        System.out.println("broadcasting all '" + message.getType() + "' to all...");
        for (Player player : Main.players) {
            Main.playerConnections.get(player).writeAndFlush(message);
            String host = ((InetSocketAddress) Main.playerConnections.get(player).remoteAddress()).getAddress().getHostAddress();
            System.out.println("    message was sent to " + host + " " + player.getName());
        }
    }

    public void broadcastAllExceptMe(NetworkMessage message, ChannelHandlerContext ctx) {
        System.out.println("broadcasting all '" + message.getType() + "' to all except me...");
        for (Player player : Main.players) {
            if (Main.playerConnections.get(player).id() != ctx.channel().id()) {
                Main.playerConnections.get(player).writeAndFlush(message);
                String host = ((InetSocketAddress) Main.playerConnections.get(player).remoteAddress()).getAddress().getHostAddress();
                System.out.println("    message was sent to " + host + " " + player.getName());
            }
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

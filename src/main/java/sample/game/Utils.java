package sample.game;

import sample.Main;
import sample.NetworkMessage;

import java.util.*;

/**
 * Created by ruslan.babich on 017 17.08.2016.
 */
public class Utils {

    public static final String CARD_TYPE_EVENT = "event";
    public static final String CARD_TYPE_PANIC = "panic";

    public static List<Card> createDeck(int playersCount) {
        List<Card> fullDeck = createFullDeck();
        List<Card> deck = new ArrayList<Card>();

        //only for debugging
        //todo: do not allow to start the game if playersCount < 4
        if (playersCount < 4)
            playersCount = 4;

        for (Card card : fullDeck) {
            if (card.getPlayersCount() <= playersCount)
                deck.add(card);
        }

        return deck;
    }

    public static List<Card> giveCardToPlayer(List<Card> deck, Player player) {
        NetworkMessage message = new NetworkMessage();
        message.setType(NetworkMessage.GET_CARD_FROM_DECK);
        message.setCard(deck.get(0));

        Main.playerConnections.get(player).sendTCP(message);

        List<Card> deckWithout1Card = new ArrayList<Card>(deck);
        deckWithout1Card.remove(0);

        return deckWithout1Card;
    }

    public static List<Card> give4CardsToPlayers(List<Card> deck, int playersCount) {
        //prepare deck
        List<Card> preparedDeck = new ArrayList<Card>();

        //get only events without infection
        for (Card card : deck)
            if (card.getType().equals(CARD_TYPE_EVENT))
                if (!card.getAction().equals(CardActions.infection))
                    preparedDeck.add(card);

        //shuffle
        preparedDeck = shuffleDeck(preparedDeck);

        //only for debugging
        //todo: do not allow to start the game if playersCount < 4
        if (playersCount < 4)
            playersCount = 4;

        //add theThing to Decks first positions depends on playersCount
        int firstCards = playersCount * 4 - 1;
        for (Card card : preparedDeck) {
            if (card.getAction().equals(CardActions.theThing)) {
                if (preparedDeck.indexOf(card) > firstCards) {
                    Random random = new Random();
                    int theThingNewIndex = random.nextInt(firstCards);
                    Collections.swap(preparedDeck, preparedDeck.indexOf(card), theThingNewIndex);
                }
                break;
            }
        }

        //loop all players
        //loop four times and give one card to player
        for (Player player : Main.players) {
            for (int i = 0; i < 4; i++) {
                preparedDeck = giveCardToPlayer(preparedDeck, player);
            }
        }

        //combine remaining cards with infection and panic
        for (Card card : deck) {
            if (card.getType().equals(CARD_TYPE_PANIC))
                preparedDeck.add(card);
            if (card.getAction().equals(CardActions.infection))
                preparedDeck.add(card);
        }

        //shuffle
        preparedDeck = shuffleDeck(preparedDeck);

        return preparedDeck;
    }

    private static List<Card> shuffleDeck(List<Card> deck) {
        List<Card> shuffledDeck = new ArrayList<Card>();

        Random random = new Random();
        List<Integer> usedIds = new ArrayList<Integer>();


        for (Card card : deck) {
            boolean isDuplicate = true;

            while (isDuplicate) {
                Integer newId = random.nextInt(500);
                if (!usedIds.contains(newId)) {
                    isDuplicate = false;
                    card.setId(newId);
                    usedIds.add(newId);
                }
            }

            shuffledDeck.add(card);
        }

        Collections.sort(shuffledDeck, getComparatorById());

        return shuffledDeck;
    }

    private static List<Card> createFullDeck() {
        List<Card> deck = new ArrayList<Card>();

        int idInc = 0;

        //EVENT
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 10));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 10));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 8));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "infection", CardActions.infection, 11));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "The Thing", CardActions.theThing, 1));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "analyze", CardActions.analyze, 5));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "analyze", CardActions.analyze, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "analyze", CardActions.analyze, 9));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "flamethrower", CardActions.flamethrower, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "flamethrower", CardActions.flamethrower, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "flamethrower", CardActions.flamethrower, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "flamethrower", CardActions.flamethrower, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "flamethrower", CardActions.flamethrower, 11));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 8));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "suspicion", CardActions.suspicion, 10));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "axe", CardActions.axe, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "axe", CardActions.axe, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "perseverance", CardActions.perseverance, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "perseverance", CardActions.perseverance, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "perseverance", CardActions.perseverance, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "perseverance", CardActions.perseverance, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "perseverance", CardActions.perseverance, 10));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "whiskey", CardActions.whiskey, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "whiskey", CardActions.whiskey, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "whiskey", CardActions.whiskey, 10));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "changePlaces", CardActions.changePlaces, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "changePlaces", CardActions.changePlaces, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "changePlaces", CardActions.changePlaces, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "changePlaces", CardActions.changePlaces, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "changePlaces", CardActions.changePlaces, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "lookAround", CardActions.lookAround, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "lookAround", CardActions.lookAround, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 10));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "temptation", CardActions.temptation, 8));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "cutBait", CardActions.cutBait, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "cutBait", CardActions.cutBait, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "cutBait", CardActions.cutBait, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "cutBait", CardActions.cutBait, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "cutBait", CardActions.cutBait, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "iAmHereNotBad", CardActions.iAmHereNotBad, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "iAmHereNotBad", CardActions.iAmHereNotBad, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "iAmHereNotBad", CardActions.iAmHereNotBad, 11));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "fear", CardActions.fear, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "fear", CardActions.fear, 8));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "fear", CardActions.fear, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "fear", CardActions.fear, 5));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noBarbecue", CardActions.noBarbecue, 4));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noBarbecue", CardActions.noBarbecue, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noBarbecue", CardActions.noBarbecue, 11));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "miss", CardActions.miss, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "miss", CardActions.miss, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "miss", CardActions.miss, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noReallyThanks", CardActions.noReallyThanks, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noReallyThanks", CardActions.noReallyThanks, 8));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noReallyThanks", CardActions.noReallyThanks, 6));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "noReallyThanks", CardActions.noReallyThanks, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "boardedUpDoor", CardActions.boardedUpDoor, 11));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "boardedUpDoor", CardActions.boardedUpDoor, 7));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "boardedUpDoor", CardActions.boardedUpDoor, 4));

        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "quarantine", CardActions.quarantine, 9));
        deck.add(new Card(++idInc, CARD_TYPE_EVENT, "quarantine", CardActions.quarantine, 5));

        // PANIC
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "chainReaction", CardActions.chainReaction, 4));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "chainReaction", CardActions.chainReaction, 9));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "forgetfulness", CardActions.forgetfulness, 4));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "goAway", CardActions.goAway, 5));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "andUCallThisAParty", CardActions.andUCallThisAParty, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "andUCallThisAParty", CardActions.andUCallThisAParty, 5));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "threeFour", CardActions.threeFour, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "threeFour", CardActions.threeFour, 4));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "oneTwo", CardActions.oneTwo, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "oneTwo", CardActions.oneTwo, 5));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "admissionsTime", CardActions.admissionsTime, 8));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "onlyBetweenUs", CardActions.onlyBetweenUs, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "onlyBetweenUs", CardActions.onlyBetweenUs, 7));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "oops", CardActions.oops, 10));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "blindDate", CardActions.blindDate, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "blindDate", CardActions.blindDate, 4));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "letsBeFriends", CardActions.letsBeFriends, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "letsBeFriends", CardActions.letsBeFriends, 7));

        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "oldRopes", CardActions.oldRopes, 9));
        deck.add(new Card(++idInc, CARD_TYPE_PANIC, "oldRopes", CardActions.oldRopes, 6));

        return deck;
    }

    private static Comparator<Card> getComparatorById() {
        Comparator<Card> comparator = new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getId() - o2.getId();
            }
        };

        return comparator;
    }


}

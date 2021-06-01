package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.game.Player;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    private Player player1;
    private Map<Player, String> playersLastChat;
    private Player player2;

    private Chat(Player plr1, Player plr2){
        //non instantiable
    }

    public static void runChat(Player plr1, Player plr2){
        //TODO trouver une moyen de le run en boucle jusqu'a la fin de la game et améliorer la classe
        //TODO faire un proprieté pour lastChat plutot?
        //TODO regler le bug pour l'envoie de message à double

        Player player1 = plr1;
        Player player2 = plr2;
        Map<Player, String> playersLastChat = new HashMap<>();
        playersLastChat.put(player1, "");
        playersLastChat.put(player2, "");
        //trouver la condition d'arrêt
        boolean stopCondition = false;

        while(!stopCondition) {
                if (! player1.lastChat().equals(playersLastChat.get(player1)) ) {
                    playersLastChat.put(player1, player1.lastChat());
                    sendNewChat(player2, playersLastChat.get(player1));
                }
                if (! player2.lastChat().equals(playersLastChat.get(player2)) ) {
                    playersLastChat.put(player2, player2.lastChat());
                    sendNewChat(player1, playersLastChat.get(player2));
                }

        }
    }

    private static void sendNewChat(Player player, String chat){
        player.receiveChat(chat);
    }
}

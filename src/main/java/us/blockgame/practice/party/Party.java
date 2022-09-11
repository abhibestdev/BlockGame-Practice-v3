package us.blockgame.practice.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.match.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Party {

    @Setter @Getter private UUID leader;
    @Getter private List<UUID> members;
    @Setter @Getter private boolean open;
    @Setter @Getter private PartyState partyState;
    @Setter @Getter private Match match;
    @Getter private List<Duel> duels;

    public Party(UUID leader) {
        this.leader = leader;
        //Initialize members to empty list
        this.members = new ArrayList<>();
        //Add leader to list of members
        this.members.add(leader);
        //Set party state to lobby by default
        partyState = PartyState.LOBBY;
        //Initialize duels to empty list by default
        duels = new ArrayList<>();
    }

    public void broadcast(String message) {
        partyAction(player -> player.sendMessage(message));
    }

    public void broadcast(FancyMessage fancyMessage) {
        partyAction(player -> fancyMessage.send(player));
    }

    public void partyAction(Consumer<? super Player> action) {
        members.stream().forEach(u -> {
            Player player = Bukkit.getPlayer(u);

            //Accept action if player is online
            if (player != null) {
                action.accept(player);
            }
        });
    }
}

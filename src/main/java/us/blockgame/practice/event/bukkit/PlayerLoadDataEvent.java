package us.blockgame.practice.event.bukkit;

import org.bukkit.entity.Player;
import us.blockgame.lib.event.PlayerEvent;

public class PlayerLoadDataEvent extends PlayerEvent {

    public PlayerLoadDataEvent(Player player) {
        super(player);
    }
}

package us.blockgame.practice.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.item.Items;

public class SettingsListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        //Check if player is holding settings item
        if (player.getItemInHand().equals(Items.SETTINGS.getItem())) {
            event.setCancelled(true);

            //Make them do /settings
            player.performCommand("settings");
            return;
        }
    }

}

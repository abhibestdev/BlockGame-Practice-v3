package us.blockgame.practice.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Load player data
        playerHandler.loadData(player);

        //Teleport player to spawn and give them the spawn items when they login
        playerHandler.teleportSpawn(player);
        playerHandler.giveItems(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) return;

        //Check if player is clicking snapshot
        if (event.getClickedInventory().getName().startsWith(ChatColor.GRAY + "Inventory of")) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getWhoClicked();

        //Check if player is clicking in their own inventory
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
            MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            //If player is not editing, in match, or builder mode, cancel
            if (!practiceProfile.isEditing() && matchHandler.getMatch(player) == null && !player.hasMetadata("build")) {
                event.setCancelled(true);
                return;
            }
        }
    }
}

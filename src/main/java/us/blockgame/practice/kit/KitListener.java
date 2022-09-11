package us.blockgame.practice.kit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.kit.menu.EditKitMenu;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Arrays;

public class KitListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        //Check if player is holding edit kit item
        if (player.getItemInHand().equals(Items.EDIT_KIT.getItem())) {
            event.setCancelled(true);

            //Open edit kit menu
            EditKitMenu editKitMenu = new EditKitMenu();
            editKitMenu.openMenu(player);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Remove dropped item
        if (practiceProfile.isEditing()) event.getItemDrop().remove();

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        Kit kit = Arrays.stream(Kit.values()).filter(k -> event.getInventory().equals(k.getEditInventory())).findFirst().orElse(null);

        //Ignore if player is not in a kit edit inventory
        if (kit == null) return;

        //Set edit contents
        kit.setEditContents(kit.getEditInventory().getContents());

        //Save edit inventory in config
        PracticePlugin.getInstance().getConfig().set("kit." + kit.getName() + ".editinventory", kit.getEditContents());
        PracticePlugin.getInstance().saveConfig();

        player.sendMessage(ChatColor.YELLOW + "You have updated the edit inventory for the kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + ".");
        return;
    }
}

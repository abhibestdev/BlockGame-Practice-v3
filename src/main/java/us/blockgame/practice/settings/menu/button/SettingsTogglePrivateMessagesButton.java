package us.blockgame.practice.settings.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.profile.GravityProfile;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;

public class SettingsTogglePrivateMessagesButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        us.blockgame.gravity.profile.ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        return new ItemBuilder(Material.SKULL_ITEM)
                .setName(ChatColor.LIGHT_PURPLE + "Toggle Private Messages")
                .setLore(ChatColor.YELLOW + "Private messages: " + (gravityProfile.isPrivateMessaging() ? ChatColor.GREEN + "True" : ChatColor.RED + "False"),
                        "",
                        ChatColor.BLUE + "Click to toggle private messages."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Have player run the toggle private messages command
        player.performCommand("toggleprivatemessages");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}

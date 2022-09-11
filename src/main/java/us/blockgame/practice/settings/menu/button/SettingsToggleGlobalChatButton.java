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

public class SettingsToggleGlobalChatButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        us.blockgame.gravity.profile.ProfileHandler profileHandler = GravityPlugin.getInstance().getProfileHandler();
        GravityProfile gravityProfile = profileHandler.getGravityProfile(player);

        return new ItemBuilder(Material.PAPER)
                .setName(ChatColor.LIGHT_PURPLE + "Toggle Global Chat")
                .setLore(ChatColor.YELLOW + "Global Chat: " + (gravityProfile.isGlobalChat() ? ChatColor.GREEN + "True" : ChatColor.RED + "False"),
                        "",
                        ChatColor.BLUE + "Click to toggle global chat."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Have player run the toggle global chat command
        player.performCommand("toggleglobalchat");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}

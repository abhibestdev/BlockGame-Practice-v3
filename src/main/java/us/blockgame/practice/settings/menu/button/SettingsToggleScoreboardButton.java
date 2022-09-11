package us.blockgame.practice.settings.menu.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class SettingsToggleScoreboardButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        return new ItemBuilder(Material.PAINTING)
                .setName(ChatColor.LIGHT_PURPLE + "Toggle Scoreboard Visibility")
                .setLore(ChatColor.YELLOW + "Visible: " + (practiceProfile.isScoreboard() ? ChatColor.GREEN + "True" : ChatColor.RED + "False"),
                        "",
                        ChatColor.BLUE + "Click to toggle scoreboard visibility."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Have player run the toggle scoreboard command
        player.performCommand("togglescoreboard");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}

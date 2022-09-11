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
import us.blockgame.practice.tab.TabType;

public class SettingsToggleDeathLightningButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        return new ItemBuilder(Material.BONE)
                .setName(ChatColor.LIGHT_PURPLE + "Toggle View Death Lightning")
                .setLore(ChatColor.YELLOW + "View Death Lightning: " + (practiceProfile.isViewDeathLightning() ? ChatColor.GREEN + "True" : ChatColor.RED + "False"),
                        "",
                        ChatColor.BLUE + "Click to toggle death lightning."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Toggle death lightning
        practiceProfile.setViewDeathLightning(!practiceProfile.isViewDeathLightning());

        player.sendMessage(ChatColor.YELLOW + "You are " + (practiceProfile.isViewDeathLightning() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " viewing death lightning.");
        return;
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
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

public class SettingsTabLayoutButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        return new ItemBuilder(Material.BOOK_AND_QUILL)
                .setName(ChatColor.LIGHT_PURPLE + "Switch Tab Layout")
                .setLore(ChatColor.YELLOW + "Layout: " + ChatColor.GREEN + practiceProfile.getTabType().getName(),
                        "",
                        ChatColor.BLUE + "Click to switch tab layout."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Set tab type to the next type
        try {
            practiceProfile.setTabType(TabType.values()[practiceProfile.getTabType().ordinal() + 1]);
        } catch (Exception ex) {
            practiceProfile.setTabType(TabType.PRACTICE);
        }

        player.sendMessage(ChatColor.YELLOW + "You have switched your tab layout to " + ChatColor.AQUA + practiceProfile.getTabType().getName() + ChatColor.YELLOW + ".");
        return;
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}

package us.blockgame.practice.kit.menu.button.editkit;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKitMenu;
import us.blockgame.practice.kit.menu.KitEditorMenu;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

@AllArgsConstructor
public class EditKitKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getDisplay().getType())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to edit kit " + kit.getName() + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Open menu
        CustomKitMenu customKitMenu = new CustomKitMenu(kit);
        customKitMenu.openMenu(player);
        return;
    }
}

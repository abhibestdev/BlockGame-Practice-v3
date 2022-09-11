package us.blockgame.practice.kit.menu.button.kiteditor;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKitMenu;

@AllArgsConstructor
public class KitEditorCancelButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.INK_SACK)
                .setDurability((short) 1)
                .setName(ChatColor.RED + "Cancel")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Open menu
        CustomKitMenu customKitMenu = new CustomKitMenu(kit);
        customKitMenu.openMenu(player);
    }
}

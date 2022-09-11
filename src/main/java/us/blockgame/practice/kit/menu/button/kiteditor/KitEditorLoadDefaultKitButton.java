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

@AllArgsConstructor
public class KitEditorLoadDefaultKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .setName(ChatColor.YELLOW + "Load Default Kit")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Set default kit
        player.getInventory().setContents(kit.getContents().clone());
        player.updateInventory();
    }
}

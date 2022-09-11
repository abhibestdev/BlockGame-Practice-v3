package us.blockgame.practice.kit.menu.button.kiteditor;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;

@AllArgsConstructor
public class KitEditorNoFunctionButton extends Button {

    private ItemStack itemStack;

    @Override
    public ItemStack getButtonItem(Player player) {
        return itemStack;
    }
}

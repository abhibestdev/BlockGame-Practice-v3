package us.blockgame.practice.kit.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class CustomKit {

    private ItemStack[] contents;
    private int number;

    @Setter
    private String name;
}

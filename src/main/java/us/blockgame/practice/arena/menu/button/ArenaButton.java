package us.blockgame.practice.arena.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.menu.ArenaInstancesMenu;

@AllArgsConstructor
public class ArenaButton extends Button {

    private Arena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .setName(ChatColor.AQUA + arena.name())
                .setLore(ChatColor.GOLD + "Click to view all instances of " + arena.name() + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ArenaInstancesMenu arenaInstancesMenu = new ArenaInstancesMenu(arena);

        //Open arena instances menu
        arenaInstancesMenu.openMenu(player);
    }
}

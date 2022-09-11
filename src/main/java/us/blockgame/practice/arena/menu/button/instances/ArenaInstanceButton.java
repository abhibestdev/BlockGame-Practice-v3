package us.blockgame.practice.arena.menu.button.instances;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.arena.Arena;

@AllArgsConstructor
public class ArenaInstanceButton extends Button {

    private Arena arena;
    private Arena.Entry entry;
    private int instance;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(entry.available() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setName((entry.available() ? ChatColor.GREEN : ChatColor.RED) + arena.name() + " instance " + instance)
                .setLore(ChatColor.GOLD + "Click to teleport to " + arena.name() + " " + instance + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.teleport(entry.spawn1());
    }
}

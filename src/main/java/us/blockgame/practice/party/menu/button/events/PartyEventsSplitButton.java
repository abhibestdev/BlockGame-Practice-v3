package us.blockgame.practice.party.menu.button.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.party.menu.PartySplitMenu;

public class PartyEventsSplitButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatColor.AQUA + "Party Split")
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to start a Party Split event."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Open Party split menu
        PartySplitMenu partySplitMenu = new PartySplitMenu();
        partySplitMenu.openMenu(player);
    }
}

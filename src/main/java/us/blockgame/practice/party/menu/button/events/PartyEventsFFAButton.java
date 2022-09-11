package us.blockgame.practice.party.menu.button.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.party.menu.PartyFFAMenu;
import us.blockgame.practice.party.menu.PartySplitMenu;

public class PartyEventsFFAButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.SLIME_BALL).setName(ChatColor.GREEN + "Party FFA")
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to start a Party FFA event."
                )
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Open Party ffa menu
        PartyFFAMenu partyFFAMenu = new PartyFFAMenu();
        partyFFAMenu.openMenu(player);
    }
}

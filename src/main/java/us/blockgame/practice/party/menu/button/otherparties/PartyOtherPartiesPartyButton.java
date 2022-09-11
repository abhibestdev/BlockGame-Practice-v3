package us.blockgame.practice.party.menu.button.otherparties;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.party.Party;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PartyOtherPartiesPartyButton extends Button {

    private Party party;

    @Override
    public ItemStack getButtonItem(Player player) {
        Player leader = Bukkit.getPlayer(party.getLeader());

        List<String> lore = new ArrayList<>();

        //Add all party member's names to the lore
        lore.add(" ");
        party.partyAction(member -> {
            lore.add(ChatColor.GOLD + member.getName());
        });
        lore.add(" ");
        lore.add(ChatColor.GREEN + "Click to duel " + leader.getName() + "'s party.");

        return new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(leader.getName()).setName(ChatColor.GOLD + leader.getName()).setLore(lore).toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Player leader = Bukkit.getPlayer(party.getLeader());

        //Duel party leader
        player.performCommand("duel " + leader.getName());
        return;
    }
}

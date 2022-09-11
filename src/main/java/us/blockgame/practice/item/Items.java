package us.blockgame.practice.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.util.ItemBuilder;

@AllArgsConstructor
public enum Items {

    UNRANKED(new ItemBuilder(Material.IRON_SWORD)
            .setName(ChatColor.GREEN + "Join an Unranked Queue")
            .toItemStack()),
    RANKED(new ItemBuilder(Material.DIAMOND_SWORD)
            .setName(ChatColor.AQUA + "Join a Ranked Queue")
            .toItemStack()),
    FFA(new ItemBuilder(Material.GOLD_AXE)
            .setName(ChatColor.GOLD + "Join FFA")
            .toItemStack()),
    CREATE_PARTY(new ItemBuilder(Material.NETHER_STAR)
            .setName(ChatColor.DARK_GREEN + "Create a Party")
            .toItemStack()),
    SETTINGS(new ItemBuilder(Material.WATCH)
            .setName(ChatColor.RED + "Settings")
            .toItemStack()),
    LEADERBOARDS(new ItemBuilder(Material.PAPER)
            .setName(ChatColor.LIGHT_PURPLE + "View Leaderboards")
            .toItemStack()),
    EDIT_KIT(new ItemBuilder(Material.BOOK)
            .setName(ChatColor.YELLOW + "Edit a Kit")
            .toItemStack()),
    LEAVE_QUEUE(new ItemBuilder(Material.INK_SACK)
            .setDurability((short) 1)
            .setName(ChatColor.RED + "Leave Queue")
            .toItemStack()),
    DUEL_PARTIES(new ItemBuilder(Material.STONE_SWORD)
            .setName(ChatColor.GRAY + "Duel Other Parties")
            .toItemStack()),
    PARTY_EVENTS(new ItemBuilder(Material.GOLD_SWORD)
            .setName(ChatColor.GOLD + "Start a Party Event")
            .toItemStack()),
    PARTY_INFO(new ItemBuilder(Material.PAPER)
            .setName(ChatColor.YELLOW + "View Party Info")
            .toItemStack()),
    DISBAND_PARTY(new ItemBuilder(Material.INK_SACK)
            .setDurability((short) 1)
            .setName(ChatColor.RED + "Disband Party")
            .toItemStack()),
    LEAVE_PARTY(new ItemBuilder(Material.INK_SACK)
            .setDurability((short) 1)
            .setName(ChatColor.RED + "Leave Party")
            .toItemStack()),
    CUSTOM_KIT(new ItemBuilder(Material.ENCHANTED_BOOK)
            .setName("drip touches breonna")
            .toItemStack()),
    DEFAULT_KIT(new ItemBuilder(Material.BOOK)
            .setName(ChatColor.YELLOW + "Default Kit")
            .toItemStack()),
    MATCH_INFO(new ItemBuilder(Material.BOOK)
            .setName(ChatColor.AQUA + "Match Info")
            .toItemStack()),
    STOP_SPECTATING(new ItemBuilder(Material.INK_SACK)
            .setDurability((short) 1)
            .setName(ChatColor.RED + "Stop Spectating")
            .toItemStack());

    @Getter
    private ItemStack item;
}

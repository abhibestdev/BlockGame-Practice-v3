package us.blockgame.practice.snapshot.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.snapshot.InventorySnapshot;

public class InventoryCommand {

    @Command(name = "inventory", aliases = {"inv"}, inGameOnly = true)
    public void inventory(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        //Check if player has inventory
        if (InventorySnapshot.getByPlayer(target) == null) {
            args.getSender().sendMessage(ChatColor.RED + "No inventory found.");
            return;
        }
        //Show inventory for player
        Player player = args.getPlayer();
        player.openInventory(InventorySnapshot.getByPlayer(target).getInventory());
        return;
    }
}

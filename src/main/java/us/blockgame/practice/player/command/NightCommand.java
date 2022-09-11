package us.blockgame.practice.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class NightCommand {

    @Command(name = "night", inGameOnly = true)
    public void night(CommandArgs args) {
        Player player = args.getPlayer();

        //Set player time to night
        player.setPlayerTime(12000L, true);
        args.getSender().sendMessage(ChatColor.GREEN + "The time has been set to night.");
        return;
    }
}

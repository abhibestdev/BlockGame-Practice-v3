package us.blockgame.practice.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class SunsetCommand {

    @Command(name = "sunset", inGameOnly = true)
    public void sunset(CommandArgs args) {
        Player player = args.getPlayer();

        //Set player time to sunset
        player.setPlayerTime(6750L, false);
        args.getSender().sendMessage(ChatColor.GREEN + "The time has been set to sunset.");
        return;
    }
}

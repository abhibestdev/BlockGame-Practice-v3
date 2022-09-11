package us.blockgame.practice.player.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;

public class DayCommand {

    @Command(name = "day", inGameOnly = true)
    public void day(CommandArgs args) {
        Player player = args.getPlayer();

        //Set player time to day
        player.setPlayerTime(0L, false);
        args.getSender().sendMessage(ChatColor.GREEN + "The time has been set to day.");
        return;
    }
}

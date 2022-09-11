package us.blockgame.practice.leaderboards.command;

import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.leaderboards.menu.LeaderboardsMenu;

public class LeaderboardsCommand {

    @Command(name = "leaderboards", aliases = {"leaderboard", "lb", "lbs"}, inGameOnly = true)
    public void leaderboards(CommandArgs args) {
        Player player = args.getPlayer();

        //Open leaderboards menu
        LeaderboardsMenu leaderboardsMenu = new LeaderboardsMenu();
        leaderboardsMenu.openMenu(player);
        return;
    }
}

package us.blockgame.practice.tournament.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.TimeUtil;
import us.blockgame.practice.tournament.TournamentHandler;
import us.blockgame.practice.tournament.menu.TournamentMenu;

import java.util.concurrent.TimeUnit;

public class TournamentCommand {

    @Command(name = "tournament", aliases = {"tourny"}, permission = "practice.command.tournament", inGameOnly = true)
    public void tournament(CommandArgs args) {
        //Send tournament help
        args.getSender().sendMessage(new String[]{
                ChatColor.RED + "Tournament Commands: ",
                ChatColor.RED + " * /tournament start",
                ChatColor.RED + " * /tournament join",
                ChatColor.RED + " * /tournament status",
                ChatColor.RED + " * /tournament info",
        });
        //If sender is admin, send admin help
        if (args.getSender().hasPermission("practice.admin")) {
            args.getSender().sendMessage(new String[]{
                    ChatColor.RED + " * /tournament stop",
            });
        }
    }

    @Command(name = "tournament.start", aliases = {"tourny.start"}, permission = "practice.command.tournament", inGameOnly = true)
    public void tournamentStart(CommandArgs args) {
        TournamentHandler tournamentHandler = new TournamentHandler();

        //Check if there is an ongoing tournament
        if (tournamentHandler.getTournament() != null) {
            args.getSender().sendMessage(ChatColor.RED + "There is already an ongoing tournament.");
            return;
        }

        //Check if tournament is on cooldown
        if (System.currentTimeMillis() - tournamentHandler.getLastTournament() <= TimeUnit.MINUTES.toMillis(45) && !args.getSender().hasPermission("practice.admin")) {
            long difference = (tournamentHandler.getLastTournament() + TimeUnit.MINUTES.toSeconds(45)) - System.currentTimeMillis();

            args.getSender().sendMessage(ChatColor.RED + "You must wait " + TimeUtil.formatTimeMillis(difference, true, true) + " to host another tournament.");
            return;
        }
        Player player = args.getPlayer();

        //Open menu
        TournamentMenu tournamentMenu = new TournamentMenu();
        tournamentMenu.openMenu(player);
        return;
    }
}

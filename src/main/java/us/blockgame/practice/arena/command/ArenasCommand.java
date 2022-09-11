package us.blockgame.practice.arena.command;

import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.arena.menu.ArenaMenu;

public class ArenasCommand {

    @Command(name = "arenas", permission = "op", inGameOnly = true)
    public void arenas(CommandArgs args) {
        Player player = args.getPlayer();

        //Open arenas menu
        ArenaMenu arenaMenu = new ArenaMenu();
        arenaMenu.openMenu(player);
    }
}

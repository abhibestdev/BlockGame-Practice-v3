package us.blockgame.practice.spectate;

import org.bukkit.Bukkit;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.spectate.command.SpectateCommand;

public class SpectateHandler {

    public SpectateHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new SpectateCommand());

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new SpectateListener(), PracticePlugin.getInstance());
    }
}

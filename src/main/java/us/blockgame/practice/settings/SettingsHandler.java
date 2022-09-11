package us.blockgame.practice.settings;

import org.bukkit.Bukkit;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.settings.command.SettingsCommand;

public class SettingsHandler {

    public SettingsHandler() {
        //Register Commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new SettingsCommand());

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new SettingsListener(), PracticePlugin.getInstance());
    }
}

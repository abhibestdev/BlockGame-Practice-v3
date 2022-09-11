package us.blockgame.practice.location;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.LocationUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.location.command.SetSpawnCommand;

public class LocationHandler {

    @Setter @Getter private Location spawn;

    public LocationHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new SetSpawnCommand());

        //Load locations
        loadLocations();
    }

    private void loadLocations() {
        //Check if spawn exists in config
        if (PracticePlugin.getInstance().getConfig().get("spawn") != null) {
            //Load spawn from config
            spawn = LocationUtil.getLocationFromString(PracticePlugin.getInstance().getConfig().getString("spawn"));
        }
    }
}

package us.blockgame.practice.location.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.util.LocationUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.location.LocationHandler;

public class SetSpawnCommand {

    @Command(name = "setspawn", permission = "op", inGameOnly = true)
    public void setSpawn(CommandArgs args) {
        Player player = args.getPlayer();

        LocationHandler locationHandler = PracticePlugin.getInstance().getLocationHandler();

        //Save spawn to location handler
        locationHandler.setSpawn(player.getLocation().clone());
        //Save spawn in config
        PracticePlugin.getInstance().getConfig().set("spawn", LocationUtil.getStringFromLocation(locationHandler.getSpawn()));
        PracticePlugin.getInstance().saveConfig();

        args.getSender().sendMessage(ChatColor.GREEN + "You have set the spawn!");
        return;
    }
}

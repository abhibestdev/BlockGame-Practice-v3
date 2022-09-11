package us.blockgame.practice.settings.command;

import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.settings.menu.SettingsMenu;

public class SettingsCommand {

    @Command(name = "settings", inGameOnly = true)
    public void settings(CommandArgs args) {
        Player player = args.getPlayer();

        //Open settings menu
        SettingsMenu settingsMenu = new SettingsMenu();
        settingsMenu.openMenu(player);
        return;
    }
}

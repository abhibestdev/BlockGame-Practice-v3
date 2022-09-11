package us.blockgame.practice.settings.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.settings.menu.button.*;

import java.util.Map;

public class SettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Load settings buttons
        buttonMap.put(0, new SettingsToggleScoreboardButton());
        buttonMap.put(1, new SettingsTabLayoutButton());
        buttonMap.put(2, new SettingsToggleGlobalChatButton());
        buttonMap.put(3, new SettingsToggleSoundsButton());
        buttonMap.put(4, new SettingsTogglePrivateMessagesButton());
        buttonMap.put(5, new SettingsToggleDeathLightningButton());

        return buttonMap;
    }
}

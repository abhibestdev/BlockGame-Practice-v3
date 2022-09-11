package us.blockgame.practice.party.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.party.menu.button.events.PartyEventsFFAButton;
import us.blockgame.practice.party.menu.button.events.PartyEventsSplitButton;

import java.util.Map;

public class PartyEventsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Start a party event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        //Set party event items
        buttonMap.put(3, new PartyEventsSplitButton());
        buttonMap.put(5, new PartyEventsFFAButton());

        return buttonMap;
    }
}

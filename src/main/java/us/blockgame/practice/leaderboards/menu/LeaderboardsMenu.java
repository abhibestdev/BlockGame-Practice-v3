package us.blockgame.practice.leaderboards.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.leaderboards.menu.button.LeaderboardsKitButton;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderboardsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Leaderboards";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Add queue buttons to menu
        Arrays.stream(Kit.values()).forEach(k -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new LeaderboardsKitButton(k));
        });

        return buttonMap;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}

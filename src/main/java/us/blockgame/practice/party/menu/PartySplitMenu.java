package us.blockgame.practice.party.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.party.menu.button.split.PartySplitKitButton;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PartySplitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BOLD.toString() + ChatColor.BOLD + "Select a kit for split event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Add split buttons to menu
        Arrays.stream(Kit.values()).forEach(k -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new PartySplitKitButton(k));
        });

        return buttonMap;
    }
}

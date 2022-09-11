package us.blockgame.practice.queue.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.queue.menu.button.unranked.QueueUnrankedKitButton;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueUnrankedMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Join an Unranked Queue";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Add queue buttons to menu
        Arrays.stream(Kit.values()).forEach(k -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new QueueUnrankedKitButton(k));
        });

        return buttonMap;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}

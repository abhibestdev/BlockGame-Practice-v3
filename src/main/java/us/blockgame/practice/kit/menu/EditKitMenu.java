package us.blockgame.practice.kit.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.button.editkit.EditKitKitButton;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EditKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a kit to edit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Add split buttons to menu
        Arrays.stream(Kit.values()).filter(k -> k.isEditable()).forEach(k -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new EditKitKitButton(k));
        });

        return buttonMap;
    }
}

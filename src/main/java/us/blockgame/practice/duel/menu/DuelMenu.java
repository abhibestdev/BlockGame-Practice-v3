package us.blockgame.practice.duel.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.duel.menu.button.DuelKitButton;
import us.blockgame.practice.kit.Kit;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class DuelMenu extends Menu {

    private Player target;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + "Select a kit to duel";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        AtomicInteger atomicSlot = new AtomicInteger();

        //Add kit buttons to menu
        Arrays.stream(Kit.values()).forEach(k -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new DuelKitButton(target, k));
        });

        return buttonMap;
    }
}

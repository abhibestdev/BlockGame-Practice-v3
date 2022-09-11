package us.blockgame.practice.kit.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.button.customkit.CustomKitCustomKitButton;

import java.util.Map;

@AllArgsConstructor
public class CustomKitMenu extends Menu {

    private Kit kit;

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a " + kit.getName() + " kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        for (int i = 1; i <= 5; i++) {
            buttonMap.put(i * 2 - 2, new CustomKitCustomKitButton(kit, i));
        }
        return buttonMap;
    }
}

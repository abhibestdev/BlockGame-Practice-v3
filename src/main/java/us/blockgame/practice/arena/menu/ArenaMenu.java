package us.blockgame.practice.arena.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.pagination.PaginatedMenu;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.arena.ArenaHandler;
import us.blockgame.practice.arena.menu.button.ArenaButton;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ArenaMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Arenas " + ChatColor.GRAY + "[" + getPage() + "/" + getPages(player) + "]";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();
        AtomicInteger atomicSlot = new AtomicInteger();

        ArenaHandler arenaHandler = PracticePlugin.getInstance().getArenaHandler();

        //Add all arena buttons
        arenaHandler.getArenas().forEach(a -> {
            buttonMap.put(atomicSlot.getAndAdd(1), new ArenaButton(a));
        });

        return buttonMap;
    }
}

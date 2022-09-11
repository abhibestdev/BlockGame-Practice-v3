package us.blockgame.practice.arena.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.pagination.PaginatedMenu;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.menu.button.instances.ArenaInstanceButton;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class ArenaInstancesMenu extends PaginatedMenu {

    private Arena arena;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + arena.name() + " Instances " + ChatColor.GRAY + "[" + getPage() + "/" + getPages(player) + "]";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();
        AtomicInteger atomicSlot = new AtomicInteger();

        //Add all arena instances buttons
        arena.entries().forEach(e -> buttonMap.put(atomicSlot.getAndAdd(1), new ArenaInstanceButton(arena, e, atomicSlot.get())));

        return buttonMap;
    }
}

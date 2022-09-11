package us.blockgame.practice.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Arena {

    String hash();

    ItemStack icon();

    void setIcon(ItemStack icon);

    String name();

    List<Entry> entries();

    String[] lines = new String[4];

    interface Entry {

        CuboidClipboard cuboidClipboard();

        Vector threshHoldVector();

        EditSession editSession();

        String hash();

        boolean available();

        void setAvailable(boolean available);

        Location spawn1();

        Location spawn2();
    }

}
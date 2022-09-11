package us.blockgame.practice.arena.impl;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import us.blockgame.practice.arena.Arena;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public final class NormalArena implements Arena {

    private final List<Arena.Entry> entries = new LinkedList<>();

    private final String name, hash;

    @Setter
    private ItemStack icon;

    @Override
    public String hash() {
        return hash;
    }

    @Override
    public ItemStack icon() {
        return icon;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Entry> entries() {
        return entries;
    }

    @Data
    public static class NormalEntry implements Entry {

        private final CuboidClipboard cuboidClipboard;

        private final Vector threshHoldVector;

        private final EditSession editSession;

        private final String hash;

        private final Location spawn1, spawn2;

        private boolean available = true;


        @Override
        public CuboidClipboard cuboidClipboard() {
            return cuboidClipboard;
        }

        @Override
        public Vector threshHoldVector() {
            return threshHoldVector;
        }

        @Override
        public EditSession editSession() {
            return editSession;
        }

        @Override
        public String hash() {
            return hash;
        }

        @Override
        public boolean available() {
            return available;
        }

        @Override
        public void setAvailable(boolean available) {
            this.available = available;
        }

        @Override
        public Location spawn1() {
            return spawn1;
        }

        @Override
        public Location spawn2() {
            return spawn2;
        }
    }
}
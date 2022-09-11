package us.blockgame.practice.kit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.util.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public enum Kit {

    NODEBUFF("NoDebuff", new ItemBuilder(Material.POTION).setDurability((short) 16421).toItemStack(), true),
    DEBUFF("Debuff", new ItemBuilder(Material.POTION).setDurability((short) 16388).toItemStack(), true),
    SOUP("Soup", new ItemBuilder(Material.MUSHROOM_SOUP).toItemStack(), true),
    SUMO("Sumo", new ItemBuilder(Material.LEASH).toItemStack(), false),
    BUILDUHC("BuildUHC", new ItemBuilder(Material.LAVA_BUCKET).toItemStack(), true),
    GAPPLE("Gapple", new ItemBuilder(Material.GOLDEN_APPLE).setDurability((short) 1).toItemStack(), true),
    COMBO("Combo", new ItemBuilder(Material.RAW_FISH).setDurability((short) 3).toItemStack(), true);

    @Getter private final String name;
    @Getter private final ItemStack display;
    @Getter private final boolean editable;

    //Kit itself
    @Setter @Getter private ItemStack[] contents;
    @Setter @Getter private ItemStack[] armor;
    @Setter @Getter private ItemStack[] editContents;
    @Setter @Getter private Inventory editInventory;

    //Lists to keep track of things
    @Getter private List<UUID> unrankedQueue = new ArrayList<>();
    @Getter private List<UUID> rankedQueue = new ArrayList<>();
    @Getter private List<UUID> unrankedMatch = new ArrayList<>();
    @Getter private List<UUID> rankedMatch = new ArrayList<>();
    @Getter private List<String> arenas = new ArrayList<>();

    public static Kit getKit(String name) {
        return Arrays.stream(Kit.values()).filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

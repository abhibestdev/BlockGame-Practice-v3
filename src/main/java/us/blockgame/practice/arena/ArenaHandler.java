package us.blockgame.practice.arena;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.Logger;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.arena.command.ArenasCommand;
import us.blockgame.practice.arena.impl.NormalArena;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.util.CollectionsUtil;
import us.blockgame.practice.util.WorldEditUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaHandler {

    private World arenaWorld;
    private File worldInfoFile;

    private Map<Locations, Location> locationsMap = Maps.newEnumMap(Locations.class);
    @Getter private List<Arena> arenas = new ArrayList<>();

    private static final int THRESH_HOLD_X = 1300, THRESH_HOLD_Z = -1300;

    public ArenaHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new ArenasCommand());

        loadWorld();
        loadArenas();
    }

    private void loadWorld() {
        WorldCreator worldCreator = new WorldCreator("arenas");
        worldCreator.generator(new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }
        });
        this.arenaWorld = worldCreator.createWorld();
        arenaWorld.setGameRuleValue("doMobSpawning", "false");
        arenaWorld.setGameRuleValue("doDaylightCycle", "false");
        arenaWorld.setGameRuleValue("naturalRegeneration", "false");
        arenaWorld.setGameRuleValue("doFireTick", "false");
        arenaWorld.setAutoSave(false);

    }

    @SneakyThrows
    private void loadArenas() {
        worldInfoFile = new File(arenaWorld.getWorldFolder() + File.separator + "worldinfo.json");

        Path schemDir = Files.createDirectories(Paths.get(PracticePlugin.getInstance().getDataFolder() + "/schematics"));
        List<File> schematics = Files.walk(schemDir).filter(Files::isRegularFile).map(Path::toFile).filter(file -> file.getName().endsWith("schematic")).collect(Collectors.toList());

        WorldInfo worldInfo = getWorldInfo();
        int freeX = worldInfo.freeArea[0];
        int freeZ = worldInfo.freeArea[1];
        locationsMap.putAll(worldInfo.getLocationMap());

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        Field idField = Material.class.getDeclaredField("byId");
        idField.setAccessible(true);
        Material[] idMaterial = (Material[]) idField.get(null);

        for (File schematicFile : schematics) {
            byte[] bytes = messageDigest.digest(Files.readAllBytes(schematicFile.toPath()));
            String hash = bytesToHex(bytes);
            if (arenaExists(hash)) continue;
            CuboidClipboard cuboidClipboard = SchematicFormat.MCEDIT.load(schematicFile);
            ArrayListMultimap<String, ArenaMeta> arrayListMultimap = ArrayListMultimap.create();
            for (int x = 0; x < cuboidClipboard.getWidth(); x++) {
                for (int y = 0; y < cuboidClipboard.getHeight(); y++) {
                    for (int z = 0; z < cuboidClipboard.getLength(); z++) {
                        Vector blockVector = new Vector(x, y, z);
                        BaseBlock block = cuboidClipboard.getBlock(new Vector(x, y, z));

                        if (arenaWorld.getBiome(x, z) != Biome.PLAINS) {
                            arenaWorld.setBiome(x, z, Biome.PLAINS);
                        }

                        int blockId = block.getId();

                        boolean remove = false;

                        if (blockId == 68 || blockId == 63) {
                            remove = true;

                            boolean spawn = false;
                            CompoundTag tag = block.getNbtData();
                            String[] lines = WorldEditUtil.linesFromCompound(tag);
                            if (lines == null) {
                                continue;
                            }
                            for (String line : lines) {
                                if (line.contains("spawn")) {
                                    spawn = true;
                                }
                            }
                            ArenaMeta meta = new ArenaMeta();
                            meta.setLines(lines);
                            meta.setDirection(WorldEditUtil.getRotation(block));
                            meta.setVector(blockVector.add(cuboidClipboard.getOffset()));

                            if (spawn) {
                                meta.setMetaType("spawn");
                                arrayListMultimap.put("spawn", meta);
                            }
                        }

                        remove = remove || blockId == 19;

                        if (remove) {
                            cuboidClipboard.setBlock(blockVector, new BaseBlock(0));
                        }
                    }
                }
            }
            String name = "";
            ItemStack icon = new ItemStack(Material.BEDROCK);
            {
                ArenaMeta meta = CollectionsUtil.findOneInMultimap("name", arrayListMultimap);
                if (meta != null) {
                    name = meta.getValue();
                }
                ArenaMeta iconMeta = CollectionsUtil.findOneInMultimap("icon", arrayListMultimap);
                if (iconMeta != null) {
                    String iconValue = iconMeta.getValue();
                    int id;
                    int itemMeta = 0;
                    if (iconValue.indexOf(':') != -1) {
                        String[] data = iconValue.split(":", 2);
                        id = Integer.parseInt(data[0]);
                        itemMeta = Integer.parseInt(data[1]);
                    } else {
                        id = Integer.parseInt(iconValue);
                    }
                    Material material = idMaterial.length > id && id >= 0 ? idMaterial[id] : null;

                    if (material != null) {
                        icon = new ItemStack(material, 1, (short) itemMeta);
                    }
                }
            }

            String fileName = schematicFile.getName().replaceFirst("[.][^.]+$", "");

            NormalArena arena = new NormalArena(name.isEmpty() ? fileName : name, hash);
            arena.setIcon(icon);
            EditSession session = new EditSession(new BukkitWorld(arenaWorld), -1);

            Vector previous = null;
            int instances = 1;
            for (int i = 0; i < instances; i++) {
                Vector threshHoldVector = new Vector(freeX += THRESH_HOLD_X, 100, freeZ += THRESH_HOLD_Z);
                final Vector current = previous;
                arrayListMultimap.values().forEach(arenaMeta -> {
                    Vector vector = arenaMeta.getVector();
                    if (current != null) {
                        vector = vector.subtract(current);
                    }
                    arenaMeta.setVector(vector.add(threshHoldVector));
                });
                cuboidClipboard.paste(session, threshHoldVector, true);
                Location spawn1 = null, spawn2 = null;
                for (ArenaMeta meta : arrayListMultimap.get("spawn")) {
                    if (meta.getLines()[0].contains("1")) {
                        spawn1 = meta.asSpawn();
                        spawn1.getBlock().setType(Material.AIR);
                    }
                    if (meta.getLines()[0].contains("2")) {
                        spawn2 = meta.asSpawn();
                        spawn2.getBlock().setType(Material.AIR);
                    }
                }
                if (spawn1 != null && spawn2 != null) {
                    NormalArena.NormalEntry entry = new NormalArena.NormalEntry(cuboidClipboard, threshHoldVector, session, hash, spawn1, spawn2);
                    arena.entries().add(entry);

                    Logger.success("Loaded arena " + arena.name() + " #" + (i + 1) + ".");
                } else {
                    System.out.println("Entry didn't pass");
                    Logger.error("Could not find spawns for schematic " + arena.name() + ".");
                }
                previous = threshHoldVector;

                arrayListMultimap.values().forEach(meta -> {
                    meta.setAir();
                    arenaWorld.getChunkAt(meta.asBlock()).load();
                });
            }
            arenas.add(arena);
        }
        worldInfo.freeArea[0] = freeX;
        worldInfo.freeArea[1] = freeZ;
        saveWorldInfo(worldInfo);
    }

    public enum Locations {
        KIT_EDITOR,
        LADDER_CREATOR
    }

    public class WorldInfo {
        private final int[] freeArea = {7500, 7500};

        @Getter
        private final Map<Locations, Location> locationMap = new EnumMap<>(Locations.class);

        boolean isSpawnDefault(Locations location) {
            return locationMap.containsKey(location);
        }
    }

    private boolean isSpawnWorld(World world) {
        return arenaWorld.getName().equals(world.getName());
    }

    @SneakyThrows
    public WorldInfo getWorldInfo() {
        return worldInfoFile.exists() ? PracticePlugin.getGson().fromJson(new String(Files.readAllBytes(worldInfoFile.toPath())), WorldInfo.class) : new WorldInfo();
    }

    @SneakyThrows
    public void saveWorldInfo(WorldInfo worldInfo) {
        Files.write(worldInfoFile.toPath(), PracticePlugin.getGson().toJson(worldInfo).getBytes(Charset.defaultCharset()));
    }

    public void setLocation(Locations locations, Location location) {
        locationsMap.put(locations, location);
        WorldInfo worldInfo = getWorldInfo();
        worldInfo.getLocationMap().putAll(locationsMap);
        saveWorldInfo(worldInfo);
    }

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private String bytesToHex(byte[] bytes) {

        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[b >> 4 & 15]).append(HEX_DIGITS[b & 15]);
        }

        return sb.toString();
    }

    @Data
    private class ArenaMeta {
        private String metaType;
        private Vector vector;
        private String[] lines = new String[4];
        private String value;
        private float direction;

        private void setAir() {

        }

        private Block asBlock() {
            return arenaWorld.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
        }

        private Location asSpawn() {
            return new Location(arenaWorld, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
        }
    }

    public boolean arenaExists(String hash) {
        for (Arena arena : arenas) {
            for (Arena.Entry entry : arena.entries()) {
                if (entry.hash() == hash) {
                    return true;
                }
            }
        }
        return false;
    }

    public void unload() {
        // arenas.forEach(a -> a.entries().forEach(e -> e.editSession().undo(e.editSession())));
    }

    public Arena.Entry randomArena(Kit kit) {
        List<Arena.Entry> applicable = new ArrayList<>();

        if (kit != null) {
            arenas.forEach(a -> {
                a.entries().stream().filter(e -> e.available() && kit.getArenas().contains(a.name())).forEach(n -> applicable.add(n));
            });
        }
        return applicable.get(LibPlugin.getRandom().nextInt(applicable.size()));
    }

    public Arena getArena(String name) {
        return arenas.stream().filter(a -> a.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

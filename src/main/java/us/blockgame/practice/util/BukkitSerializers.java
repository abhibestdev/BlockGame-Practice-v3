package us.blockgame.practice.util;

import com.google.gson.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;


@UtilityClass
public final class BukkitSerializers {

    public final LocationSerializer LOCATION_TYPE_ADAPTER = new LocationSerializer();

    public byte[] encodeItemStack(ItemStack item) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeObject(item);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String encodeItemStackToString(ItemStack item) {
        return Base64.getEncoder().encodeToString(encodeItemStack(item));
    }

    public ItemStack decodeItemStack(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return (ItemStack) dataInput.readObject();
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack decodeItemStack(String data) {
        return decodeItemStack(Base64.getDecoder().decode(data));
    }

    public byte[] encodeItemStacks(ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(items.length);
                for (ItemStack item : items) {
                    dataOutput.writeObject(item);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String encodeItemStacksToString(ItemStack[] items) {
        return Base64.getEncoder().encodeToString(encodeItemStacks(items));
    }

    public ItemStack[] decodeItemStacks(byte[] buf) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                ItemStack[] items = new ItemStack[dataInput.readInt()];
                for (int i = 0; i < items.length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }
                return items;
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack[] decodeItemStacks(String data) {
        return decodeItemStacks(Base64.getDecoder().decode(data));
    }

    public byte[] encodeInventory(Inventory inventory) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeInt(inventory.getSize());
                for (int i = 0; i < inventory.getSize(); i++) {
                    dataOutput.writeObject(inventory.getItem(i));
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String encodeInventoryToString(Inventory inventory) {
        return Base64.getEncoder().encodeToString(encodeInventory(inventory));
    }

    public Inventory decodeInventory(byte[] buf, String title) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buf)) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), title);
                for (int i = 0; i < inventory.getSize(); i++) {
                    inventory.setItem(i, (ItemStack) dataInput.readObject());
                }
                return inventory;
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Inventory decodeInventory(String data, String title) {
        return decodeInventory(Base64.getDecoder().decode(data), title);
    }

    public JsonObject locationToJson(Location location) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", location.getWorld().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());
        return obj;
    }

    public Location locationFromJson(String json) {
        return locationFromJson(new JsonParser().parse(json));
    }

    public Location locationFromJson(JsonElement element) {

        JsonObject obj = (JsonObject) element;
        JsonElement world = obj.get("world");
        JsonElement x = obj.get("x");
        JsonElement y = obj.get("y");
        JsonElement z = obj.get("z");
        JsonElement yaw = obj.get("yaw");
        JsonElement pitch = obj.get("pitch");
        return new Location(
                Bukkit.getWorld(world.getAsString()),
                x.getAsDouble(),
                y.getAsDouble(),
                z.getAsDouble(),
                yaw.getAsFloat(),
                pitch.getAsFloat()
        );
    }

    private class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

        @Override
        public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return locationFromJson(json);
        }

        @Override
        public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
            return locationToJson(src);
        }
    }

}
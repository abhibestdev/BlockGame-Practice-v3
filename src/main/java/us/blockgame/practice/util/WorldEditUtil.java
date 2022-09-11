package us.blockgame.practice.util;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.internal.gson.JsonArray;
import com.sk89q.worldedit.internal.gson.JsonElement;
import com.sk89q.worldedit.internal.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public final class WorldEditUtil {

    public String[] linesFromCompound(CompoundTag tag) {
        if (tag == null) {
            return null;
        }

        Map<String, Tag> values = tag.getValue();
        Tag currentTag = values.get("id");

        if (!(currentTag instanceof StringTag)) {
            return null;
        }

        String[] lines = new String[]{"", "", "", ""};

        for (int i = 0; i < 4; i++) {
            currentTag = values.get("Text" + (i + 1));
            if (currentTag instanceof StringTag) {
                String value = ((StringTag) currentTag).getValue();
                if (!value.contains("{\"")) {
                    lines[i] = value;
                    continue;
                }
                JsonElement parsed;
                try {
                    parsed = new JsonParser().parse(value);
                } catch (Exception e) {
                    System.out.println("Error parsing sign: " + value);
                    continue;
                }
                if (!parsed.isJsonObject()) {
                    System.out.println("Not a json object, couldn't parse the sign.");
                    continue;
                }
                JsonArray array = parsed.getAsJsonObject().getAsJsonArray("extra");
                if (array != null) {
                    lines[i] = array.get(0).toString();
                }
            }
        }

        return lines;
    }

    public float getRotation(BaseBlock block) {
        if (block.getId() == 63) {
            return block.getData() * 22.5f;
        } else if (block.getId() == 68) {
            switch (block.getData()) {
                case 3:
                    return 0;
                case 4:
                    return 90;
                case 5:
                    return -90;
                default:
                    return 180;
            }
        }
        return 0;
    }

}
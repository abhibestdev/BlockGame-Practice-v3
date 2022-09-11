package us.blockgame.practice.kit.menu.button.kiteditor;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.InventoryUtil;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.ThreadUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.kit.menu.CustomKitMenu;
import us.blockgame.practice.mongo.MongoHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Map;

@AllArgsConstructor
public class KitEditorSaveButton extends Button {

    private Kit kit;
    private int kitNumber;
    private String customName;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 5)
                .setName(ChatColor.GREEN + "Save Kit")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Save custom kit
        if (practiceProfile.getCustomKit(kit, kitNumber) != null) {
            practiceProfile.removeCustomKit(kit, kitNumber);
        }
        practiceProfile.setCustomKit(kit, new CustomKit(player.getInventory().getContents().clone(), kitNumber, customName));

        CustomKit customKit = practiceProfile.getCustomKit(kit, kitNumber);

        //Save Custom kit to mongo
        ThreadUtil.runAsync(() -> {

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();

            MongoCollection mongoCollection = mongoHandler.getCollection("players");
            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

            //If player data exists, delete it
            if (document != null) {
                mongoCollection.deleteOne(document);
            }

            Map<String, Object> documentMap = MapUtil.cloneDocument(document);
            Map<String, Object> kitMap = Maps.newHashMap();

            //Save custom kit to map
            kitMap.put("contents", InventoryUtil.itemStackArrayToBase64(customKit.getContents()));
            kitMap.put("name", customKit.getName());

            documentMap.put("kit-" + kit.getName() + "-" + customKit.getNumber(), kitMap);

            //Insert document into mongo
            mongoCollection.insertOne(new Document(documentMap));

        });

        //Open menu
        CustomKitMenu customKitMenu = new CustomKitMenu(kit);
        customKitMenu.openMenu(player);
    }
}

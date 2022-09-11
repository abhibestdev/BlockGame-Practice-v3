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
import us.blockgame.practice.kit.menu.CustomKitMenu;
import us.blockgame.practice.mongo.MongoHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Map;

@AllArgsConstructor
public class KitEditorDeleteButton extends Button {

    private Kit kit;
    private int kitNumber;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.WOOL)
                .setDurability((short) 14)
                .setName(ChatColor.RED + "Delete Kit")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Delete custom kit
        practiceProfile.removeCustomKit(kit, kitNumber);

        //Delete custom kit from mongo
        ThreadUtil.runAsync(() -> {

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();

            MongoCollection mongoCollection = mongoHandler.getCollection("players");
            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

            //If player data exists, delete it
            if (document != null) {
                mongoCollection.deleteOne(document);
            }

            Map<String, Object> documentMap = MapUtil.cloneDocument(document);

            documentMap.remove("kit-" + kit.getName() + "-" + kitNumber);

            //Insert document into mongo
            mongoCollection.insertOne(new Document(documentMap));

        });

        //Open menu
        CustomKitMenu customKitMenu = new CustomKitMenu(kit);
        customKitMenu.openMenu(player);
    }
}

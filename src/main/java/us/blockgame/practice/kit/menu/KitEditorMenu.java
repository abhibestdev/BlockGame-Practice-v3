package us.blockgame.practice.kit.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.button.kiteditor.*;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Map;

public class KitEditorMenu extends Menu {

    private Kit kit;
    private int kitNumber;
    private String customName;

    public KitEditorMenu(Kit kit, int kitNumber, String customName) {

        this.kit = kit;
        this.kitNumber = kitNumber;
        this.customName = customName;

        this.setPlaceholder(true);
        this.setAutoUpdate(true);

    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Edit " + kit.getName() + " #" + kitNumber;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        buttonMap.put(0, new KitEditorRenameButton(kit, kitNumber, customName));

        //Fill in armor slots
        for (int i = 0; i < 4; i++) {
            if (kit.getArmor().length > i && kit.getArmor()[i] != null) {
                buttonMap.put((i + 1) * 9 + 9, new KitEditorNoFunctionButton(
                        new ItemBuilder(kit.getArmor()[i * -1 + 3])
                                .setLore(
                                        " ",
                                        ChatColor.YELLOW + "Armor will automatically be equipped."
                                )
                                .toItemStack()));
            }
        }

        //Fill in placeholder slots
        for (int i = 0; i < 6; i++) {
            buttonMap.put(i * 9 + 1, new KitEditorNoFunctionButton(new ItemBuilder(Material.OBSIDIAN)
                    .setName(" ")
                    .toItemStack()));
        }
        for (int i = 2; i < 9; i++) {
            buttonMap.put(i, new KitEditorNoFunctionButton(new ItemBuilder(Material.OBSIDIAN)
                    .setName(" ")
                    .toItemStack()));
        }
        for (int i = 9; i < 18; i++) {
            buttonMap.put(i, new KitEditorNoFunctionButton(new ItemBuilder(Material.OBSIDIAN)
                    .setName(" ")
                    .toItemStack()));
        }
        for (int i = 0; i < 18; i++) {
            if (kit.getEditInventory().getContents().length > i
                    && kit.getEditInventory().getContents()[i] != null
                    && kit.getEditInventory().getContents()[i].getType() != Material.AIR) {
                buttonMap.put((i + 18), new KitEditorDuplicateButton(kit.getEditInventory().getContents()[i]));
            }
        }
        buttonMap.put(2, new KitEditorLoadDefaultKitButton(kit));
        buttonMap.put(4, new KitEditorSaveButton(kit, kitNumber, customName));
        buttonMap.put(6, new KitEditorDeleteButton(kit, kitNumber));
        buttonMap.put(8, new KitEditorCancelButton(kit));
        return buttonMap;
    }

    @Override
    public void onOpen(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Set editing
        practiceProfile.setEditing(true);
    }

    @Override
    public void onClose(Player player) {
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Give items back
        playerHandler.giveItems(player);

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Set not editing
        practiceProfile.setEditing(false);
    }
}

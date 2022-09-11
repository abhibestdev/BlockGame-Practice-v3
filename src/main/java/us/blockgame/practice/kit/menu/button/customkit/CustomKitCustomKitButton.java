package us.blockgame.practice.kit.menu.button.customkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.kit.menu.KitEditorMenu;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

@RequiredArgsConstructor
public class CustomKitCustomKitButton extends Button {

    private final Kit kit;
    private final int kitNumber;

    private CustomKit customKit;

    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        customKit = practiceProfile.getCustomKit(kit, kitNumber);

        return new ItemBuilder(Material.INK_SACK)
                .setDurability((short) (customKit == null ? 8 : 10))
                .setName(customKit == null ? ChatColor.GRAY + "Custom Kit #" + kitNumber : ChatColor.RESET + customKit.getName())
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Open kit editor menu
        KitEditorMenu kitEditorMenu = new KitEditorMenu(kit, kitNumber, (customKit != null ? customKit.getName() : ChatColor.AQUA + "Custom Kit #" + kitNumber));
        kitEditorMenu.openMenu(player);

        //Reset inventory
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        playerHandler.resetPlayer(player);

        //Give player kit
        if (customKit != null) {
            player.getInventory().setContents(customKit.getContents());
        } else {
            player.getInventory().setContents(kit.getContents());
        }
        player.updateInventory();
    }
}

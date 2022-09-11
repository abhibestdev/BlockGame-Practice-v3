package us.blockgame.practice.kit.menu.button.kiteditor;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.CC;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.KitEditorMenu;

@AllArgsConstructor
public class KitEditorRenameButton extends Button {

    private Kit kit;
    private int kitNumber;
    private String customName;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .setName(ChatColor.GOLD + "Rename Kit")
                .setLore(ChatColor.YELLOW + "Current Name: " + ChatColor.RESET + customName)
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        //Save inventory contents
        ItemStack[] contents = player.getInventory().getContents().clone();

        //Close menu
        player.closeInventory();

        //Start conversation
        player.beginConversation(new ConversationFactory(GravityPlugin.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {

                return ChatColor.YELLOW + "Enter the new name for this custom kit (color codes are applicable)...";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String name) {
                //Open menu
                KitEditorMenu kitEditorMenu = new KitEditorMenu(kit, kitNumber, CC.translate(name));
                kitEditorMenu.openMenu(player);

                //Restore inventory
                player.getInventory().setContents(contents);
                player.updateInventory();
                return null;
            }

        }).withLocalEcho(false).buildConversation(player));
    }
}

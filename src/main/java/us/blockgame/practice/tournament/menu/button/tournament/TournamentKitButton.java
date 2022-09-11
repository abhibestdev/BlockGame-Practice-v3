package us.blockgame.practice.tournament.menu.button.tournament;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.tournament.menu.TournamentConfirmationMenu;

@AllArgsConstructor
public class TournamentKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getDisplay().getType())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to start a " + kit.getName() + " tournament.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        //Open confirmation menu
        TournamentConfirmationMenu tournamentConfirmationMenu = new TournamentConfirmationMenu(kit);
        tournamentConfirmationMenu.openMenu(player);
    }
}

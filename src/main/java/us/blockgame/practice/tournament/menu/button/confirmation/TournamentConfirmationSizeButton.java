package us.blockgame.practice.tournament.menu.button.confirmation;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.tournament.Tournament;

@AllArgsConstructor
public class TournamentConfirmationSizeButton extends Button {

    private Tournament tournament;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.DIAMOND_SWORD)
                .setName(ChatColor.AQUA + "Change Tournament Size")
                .setLore(
                        ChatColor.YELLOW + "Size: " + ChatColor.RESET + tournament.getSize() + "v" + tournament.getSize(),
                        " ",
                        ChatColor.GOLD + "Click to change tournament size.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        //Increment tournament sizes
        tournament.setSize(tournament.getSize() + 1);
        if (tournament.getSize() > 5) {
            tournament.setSize(1);
        }
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}

package us.blockgame.practice.leaderboards.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.leaderboards.LeaderboardPlayer;
import us.blockgame.practice.leaderboards.LeaderboardsHandler;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LeaderboardsKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        LeaderboardsHandler leaderboardsHandler = PracticePlugin.getInstance().getLeaderboardsHandler();
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        List<String> lore = new ArrayList<>();

        List<LeaderboardPlayer> leaderboardPlayers = leaderboardsHandler.getLeaderboard(kit);
        for (int i = 0; i < leaderboardPlayers.size(); i++) {
            LeaderboardPlayer leaderboardPlayer = leaderboardPlayers.get(i);

            lore.add(ChatColor.YELLOW + "#" + (i + 1) + ". " + ChatColor.DARK_GREEN + cacheHandler.getUsername(leaderboardPlayer.getUuid()) + ChatColor.GRAY + " (" + leaderboardPlayer.getElo() + ")");
        }

        return new ItemBuilder(kit.getDisplay().getType())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName() + ChatColor.GRAY + " | " + ChatColor.WHITE + "Top 10")
                .setLore(lore)
                .toItemStack();
    }
}
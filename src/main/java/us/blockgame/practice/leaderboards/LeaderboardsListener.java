package us.blockgame.practice.leaderboards;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.event.bukkit.PlayerLoadDataEvent;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Arrays;

public class LeaderboardsListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        //Check if player is holding leaderboards item
        if (player.getItemInHand().equals(Items.LEADERBOARDS.getItem())) {
            event.setCancelled(true);

            //Force player to run /leaderboards command
            player.chat("/leaderboards");
            return;
        }
    }

    @EventHandler
    public void onLoadData(PlayerLoadDataEvent event) {
        Player player = event.getPlayer();

        LeaderboardsHandler leaderboardsHandler = PracticePlugin.getInstance().getLeaderboardsHandler();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Update leaderboard for each kit
        Arrays.stream(Kit.values()).forEach(kit -> {
            leaderboardsHandler.updateLeaderboard(kit, player.getUniqueId(), practiceProfile.getElo(kit));

            LeaderboardPlayer leaderboardPlayer = (leaderboardsHandler.getLeaderboardPlayer(kit, player.getUniqueId()));

            //Check if player is on the leaderboard
            if (leaderboardPlayer != null) {
                leaderboardsHandler.saveLeaderboard(kit);
            }
        });

    }
}

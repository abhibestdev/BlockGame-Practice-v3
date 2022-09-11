package us.blockgame.practice.match.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.concurrent.TimeUnit;

public class EnderpearlTask extends BukkitRunnable {

    @Override
    public void run() {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Bukkit.getOnlinePlayers().stream().filter(p -> profileHandler.hasProfile(p) && p.isOnline()).forEach(p -> {
            PracticeProfile practiceProfile = profileHandler.getProfile(p);

            if (practiceProfile.getLastEnderpearl() != 0 && matchHandler.getMatch(p) != null) {

                int difference = 16 - (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - practiceProfile.getLastEnderpearl());
                p.setLevel(difference);

                p.setExp(p.getExp() - 0.003125f);

                if (System.currentTimeMillis() >= practiceProfile.getLastEnderpearl() + 16000L) {
                    practiceProfile.setLastEnderpearl(0);

                    p.setLevel(0);
                    p.setExp(0f);
                    p.sendMessage(ChatColor.GREEN + "You may enderpearl again.");
                }
            }
        });
    }
}

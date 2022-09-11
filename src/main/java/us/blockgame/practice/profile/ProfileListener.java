package us.blockgame.practice.profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.blockgame.practice.PracticePlugin;

public class ProfileListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Create a profile if the player doesn't have one
        if (!profileHandler.hasProfile(player)) {
            profileHandler.addPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Remove the player's profile
        if (profileHandler.hasProfile(player)) {
            profileHandler.removePlayer(player);
        }
    }
}

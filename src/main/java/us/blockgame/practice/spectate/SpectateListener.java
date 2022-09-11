package us.blockgame.practice.spectate;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class SpectateListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is holding stop spectating item
        if (player.getItemInHand().equals(Items.STOP_SPECTATING.getItem())) {

            //Remove player as a spectator
            practiceProfile.setPlayerState(PlayerState.LOBBY);

            Match match = practiceProfile.getMatch();
            match.getSpectators().remove(player.getUniqueId());

            //If player isn't in silent, send stopped spectating message
            if (!practiceProfile.isSilent()) {
                match.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " is no longer spectating.");
            }

            practiceProfile.setMatch(null);

            //Send player back to spawn
            playerHandler.giveItems(player);
            playerHandler.teleportSpawn(player);
            return;
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Check if damager is a player
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            //If player is a spectator, cancel their damage
            if (practiceProfile.getPlayerState() == PlayerState.SPECTATING) {
                event.setCancelled(true);
            }
            return;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Check if entity is a player
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            //If player is a spectator, cancel their damage
            if (practiceProfile.getPlayerState() == PlayerState.SPECTATING) {
                event.setCancelled(true);
            }
            return;
        }
    }
}

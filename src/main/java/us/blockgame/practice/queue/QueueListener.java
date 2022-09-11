package us.blockgame.practice.queue;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.queue.menu.QueueUnrankedMenu;
import us.blockgame.practice.queue.menu.QueueRankedMenu;

public class QueueListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is holding unranked queue item
        if (player.getItemInHand().equals(Items.UNRANKED.getItem())) {
            event.setCancelled(true);

            //Open unranked queue menu
            QueueUnrankedMenu queueUnrankedMenu = new QueueUnrankedMenu();
            queueUnrankedMenu.openMenu(player);
            return;
        }

        //Check if player is holding ranked queue item
        if (player.getItemInHand().equals(Items.RANKED.getItem())) {
            event.setCancelled(true);

            //Open ranked queue menu
            QueueRankedMenu queueRankedMenu = new QueueRankedMenu();
            queueRankedMenu.openMenu(player);
            return;
        }

        //Check if player right clicked leave queue item
        if (player.getItemInHand().equals(Items.LEAVE_QUEUE.getItem())) {
            event.setCancelled(true);

            Queue queue = practiceProfile.getQueue();

            //Check if the player is queued for ranked
            if (queue.isRanked()) {
                //Remove player from ranked queue
                queue.getKit().getRankedQueue().remove(player.getUniqueId());
            } else {
                //Remove player from unranked queue
                queue.getKit().getUnrankedQueue().remove(player.getUniqueId());
            }

            //Set player state to lobby
            practiceProfile.setPlayerState(PlayerState.LOBBY);
            //Remove queue object from their profile
            practiceProfile.setQueue(null);

            //Give player lobby items
            playerHandler.giveItems(player);

            player.sendMessage(ChatColor.RED + "You have left the queue for " + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKit().getName() + ".");
            return;
        }
    }
}

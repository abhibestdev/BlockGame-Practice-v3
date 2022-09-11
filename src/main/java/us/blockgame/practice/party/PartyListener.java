package us.blockgame.practice.party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.party.menu.PartyEventsMenu;
import us.blockgame.practice.party.menu.PartyOtherPartiesMenu;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class PartyListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is holding create party item
        if (player.getItemInHand().equals(Items.CREATE_PARTY.getItem())) {
            event.setCancelled(true);

            player.performCommand("party create");
            return;
        }
        //Check if player is holding leave party item
        if (player.getItemInHand().equals(Items.LEAVE_PARTY.getItem())) {
            event.setCancelled(true);

            player.performCommand("party leave");
            return;
        }
        //Check if player is holding party info item
        if (player.getItemInHand().equals(Items.PARTY_INFO.getItem())) {
            event.setCancelled(true);

            player.performCommand("party info");
            return;
        }
        //Check if player is holding disband party item
        if (player.getItemInHand().equals(Items.DISBAND_PARTY.getItem())) {
            event.setCancelled(true);

            player.performCommand("party disband");
            return;
        }
        //Check if player is holding duel other parties item
        if (player.getItemInHand().equals(Items.DUEL_PARTIES.getItem())) {
            event.setCancelled(true);

            //Open menu
            PartyOtherPartiesMenu partyOtherPartiesMenu = new PartyOtherPartiesMenu();
            partyOtherPartiesMenu.openMenu(player);
            return;
        }
        //Check if player is holding party events item
        if (player.getItemInHand().equals(Items.PARTY_EVENTS.getItem())) {
            event.setCancelled(true);

            Party party = practiceProfile.getParty();

            //Check if party has 2 members
            if (party.getMembers().size() < 2) {
                player.sendMessage(ChatColor.RED + "You must have at least 2 party members to do this.");
                return;
            }

            //Open menu
            PartyEventsMenu partyEventsMenu = new PartyEventsMenu();
            partyEventsMenu.openMenu(player);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is using party chat
        if (event.getMessage().startsWith("@") && practiceProfile.getPlayerState() == PlayerState.PARTY) {
            event.setCancelled(true);

            //Make sure player provided message
            if (event.getMessage().equalsIgnoreCase("@")) {
                player.sendMessage(ChatColor.RED + "Please provide a message.");
                return;
            }

            //Set new message without @ and remove extra spaces
            event.setMessage(event.getMessage().substring(1).trim());

            Party party = practiceProfile.getParty();

            //Broadcast party chat message
            party.broadcast(ChatColor.AQUA.toString() + ChatColor.BOLD + "[" + ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "P" + ChatColor.AQUA.toString() + ChatColor.BOLD + "] " + player.getName() + ": " + ChatColor.LIGHT_PURPLE + event.getMessage());
            return;
        }
    }
}

package us.blockgame.practice.duel.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.duel.menu.DuelMenu;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class DuelCommand {

    @Command(name = "duel", aliases = {"1v1"}, inGameOnly = true)
    public void duel(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player isn't online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        Player player = args.getPlayer();

        //Check if player is dueling themself
        if (player == target) {
            args.getSender().sendMessage(ChatColor.RED + "You cannot duel yourself.");
            return;
        }

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        PracticeProfile targetProfile = profileHandler.getProfile(target);

        //Check if player is in lobby
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY && practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }
        if (practiceProfile.getPlayerState() == PlayerState.PARTY) {
            Party party = practiceProfile.getParty();

            //Check if party isn't in lobby
            if (party.getPartyState() != PartyState.LOBBY) {
                args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
                return;
            }
            //Check if player isn't party leader
            if (!party.getLeader().equals(player.getUniqueId())) {
                args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
                return;
            }
            //Make sure player is in party
            if (targetProfile.getPlayerState() != PlayerState.PARTY) {
                args.getSender().sendMessage(ChatColor.RED + "That player is not in a party.");
                return;
            }
            Party targetParty = targetProfile.getParty();

            //Check if target is in lobby
            if (targetParty.getPartyState() != PartyState.LOBBY) {
                args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
                return;
            }
            //Search for existing duel
            Duel duel = targetParty.getDuels().stream().filter(d -> party.getMembers().contains(d.getRequester()) && System.currentTimeMillis() - d.getTimestamp() < 30000L).findFirst().orElse(null);

            //Check if duel already exists
            if (duel != null) {
                args.getSender().sendMessage(ChatColor.RED + "Your party already has an existing duel request out to that party. You must wait for that one to expire before requesting again.");
                return;
            }

            //Open menu
            DuelMenu duelMenu = new DuelMenu(target);
            duelMenu.openMenu(player);
            return;
        }
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }
        //Search for existing duel
        Duel duel = targetProfile.getDuels().stream().filter(d -> d.getRequester().equals(player.getUniqueId()) && System.currentTimeMillis() - d.getTimestamp() < 30000L).findFirst().orElse(null);

        //Check if duel already exists
        if (duel != null) {
            args.getSender().sendMessage(ChatColor.RED + "You already have an existing duel request out to that player. You must wait for that one to expire before requesting again.");
            return;
        }
        //Open menu
        DuelMenu duelMenu = new DuelMenu(target);
        duelMenu.openMenu(player);
        return;
    }
}

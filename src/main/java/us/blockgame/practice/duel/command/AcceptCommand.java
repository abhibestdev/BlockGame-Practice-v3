package us.blockgame.practice.duel.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.duel.menu.DuelMenu;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AcceptCommand {

    @Command(name = "accept", inGameOnly = true)
    public void accept(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if target is not online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        Player player = args.getPlayer();

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
            Duel duel = party.getDuels().stream().filter(d -> targetParty.getMembers().contains(d.getRequester()) && System.currentTimeMillis() - d.getTimestamp() < 30000L).findFirst().orElse(null);

            //Check if duel doesn't exist
            if (duel == null) {
                args.getSender().sendMessage(ChatColor.RED + "That duel request was not found.");
                return;
            }

            //Clear duel requests
            party.getDuels().clear();

            List<UUID> teamOne = new ArrayList<>(party.getMembers());
            List<UUID> teamTwo = new ArrayList<>(targetParty.getMembers());

            //Start match
            Match match = new Match(duel.getKit(), teamOne, teamTwo, false, false);
            match.start();
            return;
        }
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }
        //Search for existing duel
        Duel duel = practiceProfile.getDuels().stream().filter(d -> d.getRequester().equals(target.getUniqueId()) && System.currentTimeMillis() - d.getTimestamp() < 30000L).findFirst().orElse(null);

        //Check if duel doesn't exist
        if (duel == null) {
            args.getSender().sendMessage(ChatColor.RED + "That duel request was not found.");
            return;
        }

        //Clear duel requests
        practiceProfile.getDuels().clear();

        //Start match
        Match match = new Match(duel.getKit(), ImmutableList.of(player.getUniqueId()), ImmutableList.of(target.getUniqueId()), false, false);
        match.start();
        return;
    }
}

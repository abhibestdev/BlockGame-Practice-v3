package us.blockgame.practice.nametag;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.rank.Rank;
import us.blockgame.gravity.rank.RankHandler;
import us.blockgame.lib.LibPlugin;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.nametag.task.NametagUpdateTask;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class NametagHandler {

    public NametagHandler() {
        //Register task
        new NametagUpdateTask().runTaskTimerAsynchronously(PracticePlugin.getInstance(), 1L, 1L);
    }

    public void updateNametag(Player updateFor, Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        RankHandler rankHandler = GravityPlugin.getInstance().getRankHandler();
        us.blockgame.lib.nametag.NametagHandler nametagHandler = LibPlugin.getInstance().getNametagHandler();

        //If player doesn't have a profile, ignore
        if (!profileHandler.hasProfile(player)) return;

        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        Match match = matchHandler.getMatch(player);

        if (match == null) {

            if (practiceProfile.getPlayerState() == PlayerState.PARTY) {
                Party party = practiceProfile.getParty();

                //Check if player to update is a member of their party
                if (party.getMembers().contains(updateFor.getUniqueId())) {
                    nametagHandler.setPrefix(updateFor, player, (party.getLeader().equals(player.getUniqueId()) ? "*" : "") + ChatColor.BLUE.toString());
                    return;
                }
                return;
            }
            Rank rank = rankHandler.getOfflineVisibleRank(player.getUniqueId());
            //If player is in lobby, set their tag to their lite prefix
            nametagHandler.setPrefix(updateFor, player, rank.getLitePrefix());
            return;
        }

        //Player is not a participant of the match
        if (match.getTeam(player) == 0) {
            return;
        }

        //Participant if player is in the match, not exempt, not dead, and not a spectator
        boolean isPlayer = match.getAllPlayers().contains(player.getUniqueId()) && !match.getExempt().contains(player.getUniqueId()) && !match.getDead().contains(player.getUniqueId()) && !match.getSpectators().contains(player.getUniqueId());


        //Check if the user we are updating is a spectator
        if (match.getSpectators().contains(updateFor.getUniqueId())) {
            nametagHandler.setPrefix(updateFor, player, (match.getTeam(player) == 1 ? ChatColor.AQUA : ChatColor.LIGHT_PURPLE).toString());
            return;
        }

        //Whether the players are on the same team or not
        boolean sameTeam = updateFor == player || (isPlayer && match.getTeam(player) == match.getTeam(updateFor) && !match.isFfa());

        //Set nametag to red or green depending on the player's team
        nametagHandler.setPrefix(updateFor, player, (sameTeam ? ChatColor.GREEN : ChatColor.RED).toString());
        return;
    }
}

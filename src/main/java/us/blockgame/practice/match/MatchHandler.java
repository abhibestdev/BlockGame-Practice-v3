package us.blockgame.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.task.EnderpearlTask;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchHandler {

    private List<Match> matchList;

    public MatchHandler() {
        //Set match list to empty list
        matchList = new ArrayList<>();

        //Register listener
        Bukkit.getPluginManager().registerEvents(new MatchListener(), PracticePlugin.getInstance());

        //Register tasks
        new EnderpearlTask().runTaskTimerAsynchronously(PracticePlugin.getInstance(), 0L, 0L);
    }

    public void addMatch(Match match) {
        //Add match to match list
        matchList.add(match);
    }

    public void removeMatch(Match match) {
        //Remove match from match list
        matchList.remove(match);
    }

    public Match getMatch(Player player) {

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //If player is in party, return party match
        if (practiceProfile.getPlayerState() == PlayerState.PARTY) {
            Party party = practiceProfile.getParty();

            return party.getMatch();
        }
        //Return personal match
        return practiceProfile.getMatch();
    }

    public Match getMatch(Location location) {
        return matchList.stream().filter(m -> m.getEntry().spawn1().distance(location) < 500).findFirst().orElse(null);
    }

    public String getOpponent(Player player, Match match) {
        if (match.getTeamOne().contains(player.getUniqueId())) {
            return Bukkit.getPlayer(match.getTeamTwo().get(0)).getName() + (!match.is1v1() ? "'s Team" : "");
        } else {
            return Bukkit.getPlayer(match.getTeamOne().get(0)).getName() + (!match.is1v1() ? "'s Team" : "");
        }
    }

    public String getTeammate(Player player, Match match) {
        List<UUID> team = (match.getTeam(player) == 1 ? match.getTeamOne() : match.getTeamTwo());
        for (int i = 0; i <= 1; i++) {
            if (!team.get(i).equals(player.getUniqueId())) {
                return Bukkit.getPlayer(team.get(i)).getName();
            }
        }
        return null;
    }

    public int getInMatch() {
        AtomicInteger atomicFighting = new AtomicInteger();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Get all players with active profiles and matches
        Bukkit.getOnlinePlayers().stream().filter(p -> profileHandler.hasProfile(p) && matchHandler.getMatch(p) != null).forEach(p -> {

            PracticeProfile practiceProfile = profileHandler.getProfile(p);

            //If one to fighting if the player has a match, but not spectating it
            if (practiceProfile.getPlayerState() != PlayerState.SPECTATING) {
                atomicFighting.addAndGet(1);
            }
        });
        return atomicFighting.get();
    }

}

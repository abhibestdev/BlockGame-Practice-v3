package us.blockgame.practice.tournament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.timer.Timer;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.party.Party;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Tournament {

    @Getter private Kit kit;
    @Setter @Getter private int size;
    @Setter @Getter private int round;
    @Getter private List<Party> partyList;
    @Getter private List<Match> matchList;
    @Setter @Getter private TournamentState tournamentState;
    @Setter @Getter private Timer timer;

    public Tournament(Kit kit) {
        this.kit = kit;

        //Set tournament size to 1v1 by default
        size = 1;
        //Set tournament round to 1 by default
        round = 1;
        //Initialize party list to empty list by default
        partyList = new ArrayList<>();
        //Initialize match list to empty list by default
        matchList = new ArrayList<>();
        //Set tournament state to starting by default
        tournamentState = TournamentState.ROUND_STARTING;
    }

    public void startRound() {
        List<Party> l1 = new ArrayList<>(partyList);

        //Check for an odd number of parties
        if (partyList.size() % 2 != 0) {
            //Chose random party to sit out
            Party sitOut = partyList.get(LibPlugin.getRandom().nextInt(partyList.size()));

            //Remove them from list of participants
            l1.remove(sitOut);

            sitOut.broadcast(ChatColor.RED + "There was an odd number of teams in this round. Your team will automatically advance to the next round.");
        }
        //Shuffle parties
        Collections.shuffle(l1);

        List<Party> l2 = new ArrayList<>();
        for (int i = 0; i < l1.size() / 2; i++) {
            Party party = l1.get(i);

            //Remove from first list, add to second
            l1.remove(party);
            l2.add(party);
        }

        for (int i = 0; i < l1.size(); i++) {
            Party partyOne = l1.get(i);
            Party partyTwo = l2.get(i);

            List<UUID> teamOne = new ArrayList<>(partyOne.getMembers());
            List<UUID> teamTwo = new ArrayList<>(partyTwo.getMembers());

            //Start match
            Match match = new Match(kit, teamOne, teamTwo, false, true);
            match.start();

            //Save match to list
            addMatch(match);
        }
        tournamentState = TournamentState.STARTED;
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Round " + ChatColor.DARK_GREEN + round + ChatColor.YELLOW + " of the " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + " has started!");
    }

    public void addDeath(Party party) {
        Player leader = Bukkit.getPlayer(party.getLeader());

        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + leader.getName() + ChatColor.YELLOW + "'s party has been eliminated from the tournament");

        //Remove party from tournament
        removeParty(party);

        //Check for a winner
        if (checkForWinner()) return;

        if (matchList.size() == 0) {
            //Increment round
            round += 1;

            Bukkit.broadcastMessage(ChatColor.YELLOW + "Round " + ChatColor.DARK_GREEN + round + ChatColor.YELLOW + " will start in " + ChatColor.GREEN + "30" + ChatColor.YELLOW + " seconds!");
        }
    }

    public boolean checkForWinner() {
        if (partyList.size() == 1) {
            Party winner = partyList.get(0);

        }
        return false;
    }

    public void addParty(Party party) {
        partyList.add(party);
    }

    public void removeParty(Party party) {
        partyList.remove(party);
    }

    public void addMatch(Match match) {
        matchList.add(match);
    }

    public void removeMatch(Match match) {
        matchList.remove(match);
    }
}

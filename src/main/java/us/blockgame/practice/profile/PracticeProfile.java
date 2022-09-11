package us.blockgame.practice.profile;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyInvite;
import us.blockgame.practice.queue.Queue;
import us.blockgame.practice.tab.TabType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PracticeProfile {

    @Setter @Getter private boolean scoreboard;
    @Setter @Getter private TabType tabType;
    @Setter @Getter private PlayerState playerState;
    @Setter @Getter private Match match;
    @Getter private Map<Kit, Integer> eloMap;
    @Setter @Getter private long lastEnderpearl;
    @Setter @Getter private Queue queue;
    @Setter @Getter private boolean viewDeathLightning;
    @Setter @Getter private Party party;
    @Setter @Getter private List<PartyInvite> partyInviteList;
    @Setter @Getter private boolean editing;
    @Getter @Setter private int hits;
    @Getter @Setter private int combo;
    @Getter @Setter private int longestCombo;
    @Getter @Setter private int thrownPots;
    @Getter @Setter private int fullyLandedPots;
    @Getter @Setter private boolean givenKit;
    @Getter @Setter private boolean silent;
    @Getter @Setter private boolean dataLoaded;
    @Getter private List<Duel> duels;
    private Map<Kit, ArrayList<CustomKit>> customKitMap;

    public PracticeProfile() {
        //Set their scoreboard enabled by default
        scoreboard = true;
        //Set tab type to practice by default
        tabType = TabType.PRACTICE;
        //Set player state to lobby by default
        playerState = PlayerState.LOBBY;
        //Initialize elo map to empty set
        eloMap = Maps.newHashMap();
        //Set view death lightning to true by default
        viewDeathLightning = true;
        //Set party invite list to an empty list by default
        partyInviteList = new ArrayList<>();
        //Initialize duels to empty list by default
        duels = new ArrayList<>();
        //Set custom kit map to empty map by default
        customKitMap = Maps.newHashMap();
    }

    public int getElo(Kit kit) {
        return eloMap.getOrDefault(kit, 1000);
    }

    public void setElo(Kit kit, int elo) {
        eloMap.put(kit, elo);
    }

    public CustomKit getCustomKit(Kit kit, int number) {
        CustomKit customKit = null;
        if (customKitMap.containsKey(kit)) {
            for (CustomKit customKits : customKitMap.get(kit)) {
                if (customKits.getNumber() == number) customKit = customKits;
            }
        }
        return customKit;
    }

    public void setCustomKit(Kit kit, CustomKit customKit) {
        ArrayList<CustomKit> customKits = customKitMap.getOrDefault(kit, new ArrayList<>());
        customKits.add(customKit);

        customKitMap.put(kit, customKits);
    }

    public void removeCustomKit(Kit kit, int number) {
        CustomKit customKit = getCustomKit(kit, number);

        ArrayList<CustomKit> customKits = customKitMap.getOrDefault(kit, new ArrayList<>());
        customKits.remove(customKit);

        customKitMap.put(kit, customKits);
    }
}

package us.blockgame.practice.party.menu;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.pagination.PaginatedMenu;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyHandler;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.party.menu.button.otherparties.PartyOtherPartiesPartyButton;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PartyOtherPartiesMenu extends PaginatedMenu {

    public PartyOtherPartiesMenu() {
        this.setPlaceholder(true);
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Other Parties " + ChatColor.GRAY + "[" + getPage() + "/" + getPages(player) + "]";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        Party party = practiceProfile.getParty();

        PartyHandler partyHandler = PracticePlugin.getInstance().getPartyHandler();

        AtomicInteger atomicSlot = new AtomicInteger();
        partyHandler.getPartyList().forEach(p -> {

            //Get all available parties to duel
            if (!p.equals(party) && party.getPartyState() == PartyState.LOBBY) {

                //Add button
                buttonMap.put(atomicSlot.getAndAdd(1), new PartyOtherPartiesPartyButton(p));
            }
        });

        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 45;
    }
}

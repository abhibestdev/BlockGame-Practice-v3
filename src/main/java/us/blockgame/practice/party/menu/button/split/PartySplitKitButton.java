package us.blockgame.practice.party.menu.button.split;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PartySplitKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getDisplay().getType())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to start a " + kit.getName() + " split event.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler  = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Close menu
        player.closeInventory();

        //Make sure player is in a party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            return;
        }
        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            return;
        }

        //Make sure party is in lobby
        if (party.getPartyState() != PartyState.LOBBY) {
            return;
        }

        List<UUID> teamOne = new ArrayList<>(party.getMembers());
        List<UUID> teamTwo = new ArrayList<>();

        //Shuffle party members
        Collections.shuffle(teamOne);

        //Split teams
        for (int i = 0; i< (int) Math.round(teamOne.size() / 2); i++) {
            UUID uuid = teamOne.get(i);

            //Add player to team two and remove them from team one
            teamTwo.add(uuid);
            teamOne.remove(uuid);
        }

        //Start match
        Match match = new Match(kit, teamOne, teamTwo, false, false);
        match.start();
        return;
    }
}

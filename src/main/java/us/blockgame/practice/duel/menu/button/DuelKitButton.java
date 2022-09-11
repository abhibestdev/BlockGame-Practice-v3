package us.blockgame.practice.duel.menu.button;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.duel.Duel;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

@AllArgsConstructor
public class DuelKitButton extends Button {

    private Player target;
    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getDisplay().getType())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        " ",
                        ChatColor.GOLD + "Click to request a " + kit.getName() + " duel.")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Close menu
        player.closeInventory();

        //Check if target logged off
        if (!target.isOnline()) {
            player.sendMessage(ChatColor.RED + "That player is no longer online.");
            return;
        }

        PracticeProfile targetProfile = profileHandler.getProfile(target);

        //Check if player is in party
        if (practiceProfile.getPlayerState() == PlayerState.PARTY) {
            Party party = practiceProfile.getParty();

            //Check if target is not in a party
            if (targetProfile.getPlayerState() != PlayerState.PARTY) {
                player.sendMessage(ChatColor.RED + "That player is not in a party.");
                return;
            }
            Party targetParty = targetProfile.getParty();

            //Check if target is not in lobby
            if (targetParty.getPartyState() != PartyState.LOBBY) {
                player.sendMessage(ChatColor.RED + "That player is not in the lobby.");
                return;
            }

            //Add duel request
            targetParty.getDuels().add(new Duel(player.getUniqueId(), kit));

            FancyMessage fancyDuel = new FancyMessage(player.getName()).color(ChatColor.DARK_GREEN).then("'s party (").color(ChatColor.YELLOW).then(String.valueOf(party.getMembers().size())).color(ChatColor.DARK_GREEN).then(") has requested to duel you with the kit ").color(ChatColor.YELLOW).then(kit.getName()).color(ChatColor.DARK_GREEN).then(" ").then("[Accept]").color(ChatColor.GREEN).tooltip("Click to accept " + player.getName() + "'s party's duel request.").command("/accept " + player.getName());
            targetParty.broadcast(fancyDuel);

            Player targetLeader = Bukkit.getPlayer(targetParty.getLeader());

            party.broadcast(ChatColor.YELLOW + "Your party has sent a duel request to " + ChatColor.DARK_GREEN + targetLeader.getName() + ChatColor.YELLOW + "'s party (" + ChatColor.DARK_GREEN + targetParty.getMembers().size() + ChatColor.YELLOW + ") with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".");
            return;
        }
        //Check if target is not in lobby
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }
        targetProfile.getDuels().add(new Duel(player.getUniqueId(), kit));

        FancyMessage fancyDuel = new FancyMessage(player.getName()).color(ChatColor.DARK_GREEN).then(" has requested to duel you with the kit ").color(ChatColor.YELLOW).then(kit.getName()).color(ChatColor.DARK_GREEN).then(" ").then("[Accept]").color(ChatColor.GREEN).tooltip("Click to accept " + player.getName() + "'s duel request.").command("/accept " + player.getName());
        fancyDuel.send(target);

        player.sendMessage(ChatColor.YELLOW + "You have sent a duel request to " + ChatColor.DARK_GREEN + target.getName() + ChatColor.YELLOW + " with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".");
        return;
    }
}

package us.blockgame.practice.queue.menu.button.unranked;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.queue.Queue;

import java.util.UUID;

@AllArgsConstructor
public class QueueUnrankedKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getDisplay().getType(), kit.getUnrankedMatch().size())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        ChatColor.YELLOW + "In queue: " + ChatColor.DARK_GREEN + kit.getUnrankedQueue().size(),
                        ChatColor.YELLOW + "In fights: " + ChatColor.DARK_GREEN + kit.getUnrankedMatch().size(),
                        " ",
                        ChatColor.GOLD + "Click to join the queue for Unranked " + kit.getName() + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        //If player is already in queue, ignore
        if (kit.getUnrankedQueue().contains(player.getUniqueId())) return;

        player.sendMessage(ChatColor.YELLOW + "You have been added to the queue for the kit unranked " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".");

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        if (kit.getUnrankedQueue().size() > 0) {
            UUID foundUUID = kit.getUnrankedQueue().get(0);

            //If the person already in queue is the same player, ignore
            if (foundUUID.equals(player.getUniqueId())) return;

            Player foundPlayer = Bukkit.getPlayer(foundUUID);
            PracticeProfile foundProfile = profileHandler.getProfile(foundPlayer);

            //Remove found player from the queue
            kit.getUnrankedQueue().remove(foundUUID);

            //Add players to match
            kit.getUnrankedMatch().add(foundUUID);
            kit.getUnrankedMatch().add(player.getUniqueId());

            //Start match
            Match match = new Match(kit, ImmutableList.of(player.getUniqueId()), ImmutableList.of(foundUUID), false, false);
            match.start();
            return;
        }
        //Add player to queue
        kit.getUnrankedQueue().add(player.getUniqueId());

        //Save queue object to player
        practiceProfile.setQueue(new Queue(kit, false));
        //Set player state to queue
        practiceProfile.setPlayerState(PlayerState.QUEUE);

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Give player queue items
        playerHandler.giveItems(player);
        return;
    }
}

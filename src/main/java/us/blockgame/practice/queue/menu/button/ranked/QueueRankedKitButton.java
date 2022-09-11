package us.blockgame.practice.queue.menu.button.ranked;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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
public class QueueRankedKitButton extends Button {

    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        return new ItemBuilder(kit.getDisplay().getType(), kit.getRankedMatch().size())
                .setDurability(kit.getDisplay().getDurability())
                .setName(ChatColor.AQUA + kit.getName())
                .setLore(
                        ChatColor.YELLOW + "In queue: " + ChatColor.DARK_GREEN + kit.getRankedQueue().size(),
                        ChatColor.YELLOW + "In fights: " + ChatColor.DARK_GREEN + kit.getRankedMatch().size(),
                        " ",
                        ChatColor.YELLOW + "Your Elo: " + ChatColor.BLUE + practiceProfile.getElo(kit),
                        " ",
                        ChatColor.GOLD + "Click to join the queue for Ranked " + kit.getName() + ".")
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();

        //If player is already in queue, ignore
        if (kit.getRankedQueue().contains(player.getUniqueId())) return;

        //Add player to queue
        kit.getRankedQueue().add(player.getUniqueId());

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Save queue object to player
        Queue queue = new Queue(kit, true);
        queue.setMinElo(practiceProfile.getElo(kit));
        queue.setMaxElo(practiceProfile.getElo(kit));
        practiceProfile.setQueue(queue);

        //Set player state to queue
        practiceProfile.setPlayerState(PlayerState.QUEUE);

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Give player queue items
        playerHandler.giveItems(player);

        player.sendMessage(ChatColor.YELLOW + "You have been added to the queue for the kit ranked " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + " with " + ChatColor.DARK_GREEN + practiceProfile.getElo(kit) + ChatColor.YELLOW + " elo.");

        new BukkitRunnable() {
            public void run() {
                //Ignore if player is not in queue anymore
                if (practiceProfile.getPlayerState() != PlayerState.QUEUE || !kit.getRankedQueue().contains(player.getUniqueId())) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < kit.getRankedQueue().size(); i++) {

                    UUID foundUUID = kit.getRankedQueue().get(i);

                    //If the person already in queue is the same player, ignore
                    if (foundUUID.equals(player.getUniqueId())) continue;

                    Player foundPlayer = Bukkit.getPlayer(foundUUID);
                    PracticeProfile foundProfile = profileHandler.getProfile(foundPlayer);
                    Queue foundQueue = foundProfile.getQueue();

                    //Check if each player fits the other's queue requirements
                    if (queue.getMinElo() <= foundProfile.getElo(kit) && queue.getMaxElo() >= foundProfile.getElo(kit) && foundQueue.getMinElo() <= practiceProfile.getElo(kit) && foundQueue.getMaxElo() >= practiceProfile.getElo(kit)) {
                        this.cancel();

                        //Remove players from the queue
                        kit.getRankedQueue().remove(foundUUID);
                        kit.getRankedQueue().remove(player.getUniqueId());

                        //Add players to match
                        kit.getRankedMatch().add(foundUUID);
                        kit.getRankedMatch().add(player.getUniqueId());

                        //Start match
                        Match match = new Match(kit, ImmutableList.of(player.getUniqueId()), ImmutableList.of(foundUUID), true, false);
                        match.start();
                        return;
                    }
                }
                queue.setMinElo(queue.getMinElo() - 20 > 0 ? queue.getMinElo() - 20 : 0);
                queue.setMaxElo(queue.getMaxElo() + 20 > 2500 ? 2500 : queue.getMaxElo() + 20);
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);
    }
}

package us.blockgame.practice.tournament.menu.button.confirmation;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.timer.Timer;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.tournament.Tournament;
import us.blockgame.practice.tournament.TournamentHandler;
import us.blockgame.practice.tournament.TournamentState;

@AllArgsConstructor
public class TournamentConfirmationStartButton extends Button {

    private Tournament tournament;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                .setName(ChatColor.GREEN + "Start Tournament")
                .setLore(
                        ChatColor.YELLOW + "Kit: " + ChatColor.RESET + tournament.getKit().getName(),
                        ChatColor.YELLOW + "Size: " + ChatColor.RESET + tournament.getSize() + "v" + tournament.getSize(),
                        " ",
                        ChatColor.GOLD + "Click to start tournament.")
                .setDurability((short) 5)
                .toItemStack();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Timer timer = new Timer("tournament", 120, true, true);

        TournamentHandler tournamentHandler = PracticePlugin.getInstance().getTournamentHandler();
        tournamentHandler.setTournament(tournament);

        //Start timer
        tournament.setTimer(timer);
        timer.start();

        new BukkitRunnable() {
            public void run() {

                //If timer is no longer needed, cancel task
                if (tournamentHandler.getTournament() == null || tournamentHandler.getTournament().getTournamentState() != TournamentState.ROUND_STARTING) {
                    this.cancel();
                    return;
                }

                if (timer.getRawTime() % 30 == 0 && timer.getRawTime() > 0) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "There is a " + ChatColor.DARK_GREEN + tournament.getSize() + "v" + tournament.getSize() + " " + tournament.getKit().getName() + ChatColor.YELLOW + " tournament starting in " + ChatColor.DARK_GREEN + timer.getTime() + ChatColor.YELLOW + ". Do " + ChatColor.DARK_GREEN + "/tournament join" + ChatColor.YELLOW + " to join!");
                }
                //If timer has ended, start round
                if (timer.getRawTime() == 0) {
                    tournament.startRound();
                    return;
                }
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);

    }
}

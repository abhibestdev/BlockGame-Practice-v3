package us.blockgame.practice.nametag.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.nametag.NametagHandler;

public class NametagUpdateTask extends BukkitRunnable {

    public void run() {
        NametagHandler nametagHandler = PracticePlugin.getInstance().getNametagHandler();

        Bukkit.getOnlinePlayers().forEach(updateFor -> {
            Bukkit.getOnlinePlayers().forEach(toUpdate -> {
                //Update each player's nametag
                nametagHandler.updateNametag(updateFor, toUpdate);
            });
        });
    }
}

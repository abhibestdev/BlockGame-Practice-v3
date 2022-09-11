package us.blockgame.practice.tab;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import us.blockgame.lib.tab.BGTab;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.queue.QueueHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PracticeTab implements BGTab {

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public Map<Integer, String> getSlots(Player player) {
        Map<Integer, String> slots = Maps.newHashMap();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Ignore if player doesn't have a profile
        if (!profileHandler.hasProfile(player)) return slots;

        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        TabHandler tabHandler = PracticePlugin.getInstance().getTabHandler();

        //If player is using vanilla tab, use the gravity tab
        if (practiceProfile.getTabType() == TabType.VANILLA) {
            return tabHandler.getGravityTab().getSlots(player);
        }

        int ping = ((CraftPlayer) player).getHandle().ping;

        QueueHandler queueHandler = PracticePlugin.getInstance().getQueueHandler();
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        slots.put(1, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------");
        slots.put(21, ChatColor.AQUA.toString() + ChatColor.BOLD + "BlockGame");
        slots.put(22, ChatColor.GRAY + "Your Ping: " + ping + " ms");

        slots.put(2, ChatColor.GRAY + "Queueing: " + queueHandler.getInQueue());
        slots.put(42, ChatColor.GRAY + "Fighting: " + matchHandler.getInMatch());
        slots.put(24, ChatColor.DARK_AQUA.toString() + "Your Stats:");

        AtomicInteger atomicStatSlot = new AtomicInteger(5);

        AtomicInteger atomicStatColumn = new AtomicInteger(0);

        Arrays.stream(Kit.values()).forEach(k -> {
            atomicStatColumn.addAndGet(1);
           if (atomicStatColumn.get() >= 4) {
               atomicStatColumn.set(0);

               atomicStatSlot.addAndGet(1);
           }

           slots.put(atomicStatSlot.get() + (atomicStatColumn.get() == 1 ? 0 : atomicStatColumn.get() == 2 ? 20 : 40), ChatColor.AQUA + k.getName() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + practiceProfile.getElo(k));
        });

        slots.put(41, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------");
        slots.put(20, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------");
        slots.put(40, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------");
        slots.put(60, ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------");

        return slots;
    }
}

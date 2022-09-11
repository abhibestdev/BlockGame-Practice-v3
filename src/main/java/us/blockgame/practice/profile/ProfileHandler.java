package us.blockgame.practice.profile;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.blockgame.practice.PracticePlugin;

import java.util.Map;
import java.util.UUID;

public class ProfileHandler {

    private Map<UUID, PracticeProfile> practiceProfileMap;

    public ProfileHandler() {
        //Initialize map
        practiceProfileMap = Maps.newHashMap();

        //Register Listener
        Bukkit.getPluginManager().registerEvents(new ProfileListener(), PracticePlugin.getInstance());
    }

    public void addPlayer(Player player) {
        practiceProfileMap.put(player.getUniqueId(), new PracticeProfile());
    }

    public PracticeProfile getProfile(Player player) {
        return practiceProfileMap.get(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        practiceProfileMap.remove(player.getUniqueId());
    }

    public boolean hasProfile(Player player) {
        return practiceProfileMap.containsKey(player.getUniqueId());
    }
}

package us.blockgame.practice.queue;

import org.bukkit.Bukkit;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class QueueHandler {

    public QueueHandler() {
        //Register listeners
        Bukkit.getPluginManager().registerEvents(new QueueListener(), PracticePlugin.getInstance());
    }

    public int getInQueue() {
        AtomicInteger atomicQueueing = new AtomicInteger();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Get all players with active profiles
        Bukkit.getOnlinePlayers().stream().filter(profileHandler::hasProfile).forEach(p -> {

            PracticeProfile practiceProfile = profileHandler.getProfile(p);

            //Add 1 to queueing if player state is queue
            if (practiceProfile.getPlayerState() == PlayerState.QUEUE) {
                atomicQueueing.addAndGet(1);
            }
        });
        return atomicQueueing.get();
    }
}

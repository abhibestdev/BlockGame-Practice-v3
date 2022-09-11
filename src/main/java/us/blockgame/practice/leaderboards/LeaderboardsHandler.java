package us.blockgame.practice.leaderboards;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.ThreadUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.leaderboards.command.LeaderboardsCommand;
import us.blockgame.practice.mongo.MongoHandler;

import java.util.*;

public class LeaderboardsHandler {

    private HashMap<Kit, List<LeaderboardPlayer>> leaderboardPlayerMap = Maps.newHashMap();

    public LeaderboardsHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new LeaderboardsCommand());

        //Load leaderboards
        Arrays.stream(Kit.values()).forEach(this::loadLeaderboard);

        //Register listener
        Bukkit.getPluginManager().registerEvents(new LeaderboardsListener(), PracticePlugin.getInstance());
    }

    public LeaderboardPlayer getLeaderboardPlayer(Kit kit, UUID uuid) {
        List<LeaderboardPlayer> leaderboardPlayers = getLeaderboard(kit);
        return leaderboardPlayers.stream().filter(l -> l.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public void updateLeaderboard(Kit kit, UUID uuid, int elo) {
        List<LeaderboardPlayer> leaderboardPlayers = getLeaderboard(kit);

        LeaderboardPlayer existingLeaderboardPlayer = leaderboardPlayers.stream().filter(l -> l.getUuid().equals(uuid)).findFirst().orElse(null);

        //Check if player is already on the leaderboard and then update their elo
        if (existingLeaderboardPlayer != null) {
            existingLeaderboardPlayer.setElo(elo);
        } else {
            //Add new leaderboard player
            leaderboardPlayers.add(new LeaderboardPlayer(uuid, elo));
        }

        //Sort leaderboard least to greatest, then reverse
        leaderboardPlayers.sort(Comparator.comparing(LeaderboardPlayer::getElo));
        Collections.reverse(leaderboardPlayers);

        //Check if leaderboard size is greater than 10
        if (leaderboardPlayers.size() > 10) {

            //Delete extra leaderboard players
            for (int i = 10; i < leaderboardPlayers.size(); i++) {
                LeaderboardPlayer excess = leaderboardPlayers.get(i);
                leaderboardPlayers.remove(excess);
            }
        }
        leaderboardPlayerMap.put(kit, leaderboardPlayers);
    }

    public void loadLeaderboard(Kit kit) {

        ThreadUtil.runAsync(() -> {

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("leaderboards");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", kit.getName())).first();

            //Check if document does not exist
            if (document == null) {
                return;
            }

            //Get string list of leaderboard entries
            List<String> leaderboard = (List<String>) document.get("leaderboard");

            //Convert string entries back into leaderboard player entries
            leaderboard.forEach(l -> {
                String[] data = l.split(":");
                UUID uuid = UUID.fromString(data[0]);
                int elo = Integer.parseInt(data[1]);

                updateLeaderboard(kit, uuid, elo);
            });
        });
    }

    public void saveLeaderboard(Kit kit) {

        ThreadUtil.runAsync(() -> {

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("leaderboards");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", kit.getName())).first();

            //Check if document exists then delete it
            if (document != null) {
                mongoCollection.deleteOne(document);
            }
            List<LeaderboardPlayer> leaderboardPlayers = getLeaderboard(kit);
            List<String> leaderboard = new ArrayList<>();

            //Save leaderboard to a string list
            leaderboardPlayers.forEach(l -> leaderboard.add(l.getUuid().toString() + ":" + l.getElo()));

            Map<String, Object> documentMap = MapUtil.createMap(
                    "_id", kit.getName(),
                    "leaderboard", leaderboard
            );

            //Insert leaderboard into mongo
            mongoCollection.insertOne(new Document(documentMap));
        });
    }

    public List<LeaderboardPlayer> getLeaderboard(Kit kit) {
        return leaderboardPlayerMap.getOrDefault(kit, new ArrayList<>());
    }
}

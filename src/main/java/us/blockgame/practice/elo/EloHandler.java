package us.blockgame.practice.elo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.MapUtil;
import us.blockgame.lib.util.ThreadUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.elo.command.SetEloCommand;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.leaderboards.LeaderboardsHandler;
import us.blockgame.practice.mongo.MongoHandler;

import java.util.Map;
import java.util.UUID;

public class EloHandler {

    public EloHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new SetEloCommand());
    }

    public void saveElo(UUID uuid, Kit kit, int elo) {
        ThreadUtil.runAsync(() -> {

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", uuid)).first();

            //Update elo in mongo
            if (document != null) {
                mongoCollection.deleteOne(document);
            }

            Map<String, Object> documentMap = MapUtil.cloneDocument(document);
            documentMap.put(kit.getName() + "-elo", elo);

            //Insert document back into mongo
            mongoCollection.insertOne(new Document(documentMap));
        });

        //Update leaderboards
        LeaderboardsHandler leaderboardsHandler = PracticePlugin.getInstance().getLeaderboardsHandler();
        leaderboardsHandler.updateLeaderboard(kit, uuid, elo);
    }
}

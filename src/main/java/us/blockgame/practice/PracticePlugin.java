package us.blockgame.practice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockgame.practice.arena.ArenaHandler;
import us.blockgame.practice.board.BoardHandler;
import us.blockgame.practice.duel.DuelHandler;
import us.blockgame.practice.elo.EloHandler;
import us.blockgame.practice.kit.KitHandler;
import us.blockgame.practice.leaderboards.LeaderboardsHandler;
import us.blockgame.practice.location.LocationHandler;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.mongo.MongoHandler;
import us.blockgame.practice.nametag.NametagHandler;
import us.blockgame.practice.party.PartyHandler;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.queue.QueueHandler;
import us.blockgame.practice.settings.SettingsHandler;
import us.blockgame.practice.snapshot.SnapshotHandler;
import us.blockgame.practice.spectate.SpectateHandler;
import us.blockgame.practice.tab.TabHandler;
import us.blockgame.practice.tournament.TournamentHandler;
import us.blockgame.practice.util.BukkitSerializers;

public class PracticePlugin extends JavaPlugin {

    @Getter private static PracticePlugin instance;
    @Getter private static Gson gson;

    @Getter private MongoHandler mongoHandler;
    @Getter private ProfileHandler profileHandler;
    @Getter private LocationHandler locationHandler;
    @Getter private PlayerHandler playerHandler;
    @Getter private SettingsHandler settingsHandler;
    @Getter private QueueHandler queueHandler;
    @Getter private ArenaHandler arenaHandler;
    @Getter private KitHandler kitHandler;
    @Getter private SnapshotHandler snapshotHandler;
    @Getter private LeaderboardsHandler leaderboardsHandler;
    @Getter private EloHandler eloHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private DuelHandler duelHandler;
    @Getter private SpectateHandler spectateHandler;
    @Getter private TournamentHandler tournamentHandler;
    @Getter private BoardHandler boardHandler;
    @Getter private TabHandler tabHandler;
    @Getter private NametagHandler nametagHandler;
    @Getter private PartyHandler partyHandler;

    @Override
    public void onEnable() {
        instance = this;
        gson = new GsonBuilder().registerTypeAdapter(Location.class, BukkitSerializers.LOCATION_TYPE_ADAPTER).setPrettyPrinting().create();

        //Create config
        getConfig().options().copyDefaults(true);
        saveConfig();

        //Register Handlers
        registerHandlers();
    }

    private void registerHandlers() {
        mongoHandler = new MongoHandler();
        profileHandler = new ProfileHandler();
        locationHandler = new LocationHandler();
        playerHandler = new PlayerHandler();
        settingsHandler = new SettingsHandler();
        queueHandler = new QueueHandler();
        arenaHandler = new ArenaHandler();
        kitHandler = new KitHandler();
        snapshotHandler = new SnapshotHandler();
        leaderboardsHandler = new LeaderboardsHandler();
        eloHandler = new EloHandler();
        matchHandler = new MatchHandler();
        duelHandler = new DuelHandler();
        spectateHandler = new SpectateHandler();
        tournamentHandler = new TournamentHandler();
        boardHandler = new BoardHandler();
        tabHandler = new TabHandler();
        nametagHandler = new NametagHandler();
        partyHandler = new PartyHandler();
    }

}

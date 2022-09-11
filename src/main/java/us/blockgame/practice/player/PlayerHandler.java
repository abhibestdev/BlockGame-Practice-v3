package us.blockgame.practice.player;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.util.InventoryUtil;
import us.blockgame.lib.util.ThreadUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.event.bukkit.PlayerLoadDataEvent;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.location.LocationHandler;
import us.blockgame.practice.mongo.MongoHandler;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.player.command.DayCommand;
import us.blockgame.practice.player.command.NightCommand;
import us.blockgame.practice.player.command.SunsetCommand;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Arrays;
import java.util.Map;

public class PlayerHandler {

    public PlayerHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new DayCommand());
        commandHandler.registerCommand(new NightCommand());
        commandHandler.registerCommand(new SunsetCommand());

        //Register listener
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), PracticePlugin.getInstance());
    }

    public void teleportSpawn(Player player) {
        LocationHandler locationHandler = PracticePlugin.getInstance().getLocationHandler();

        //Teleport player to spawn if one is set
        if (locationHandler.getSpawn() != null) player.teleport(locationHandler.getSpawn());

        //TODO: Update player visibility
    }

    public void resetPlayer(Player player) {
        if (!player.isOnline()) return;

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Clear player inventory
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        //Reset health and exp
        player.setHealth(20.0f);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setSaturation(5);
        player.setFireTicks(0);
        player.setMaximumNoDamageTicks(19);
        player.setExp(0);

        //Remove enderpearl cooldown
        practiceProfile.setLastEnderpearl(0);

        //Clear potion effects
        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));

        //If player isn't in creative, don't allow them to fly
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    public void giveItems(Player player) {
        resetPlayer(player);

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        switch (practiceProfile.getPlayerState()) {
            case LOBBY: {
                //Give player lobby items
                player.getInventory().setItem(0, Items.UNRANKED.getItem());
                player.getInventory().setItem(1, Items.RANKED.getItem());
                player.getInventory().setItem(2, Items.FFA.getItem());
                player.getInventory().setItem(4, Items.CREATE_PARTY.getItem());
                player.getInventory().setItem(6, Items.LEADERBOARDS.getItem());
                player.getInventory().setItem(7, Items.EDIT_KIT.getItem());
                player.getInventory().setItem(8, Items.SETTINGS.getItem());
                break;
            }
            case QUEUE: {
                //Give player leave queue items
                player.getInventory().setItem(8, Items.LEAVE_QUEUE.getItem());
                break;
            }
            case PARTY: {
                Party party = practiceProfile.getParty();

                player.getInventory().setItem(6, Items.PARTY_INFO.getItem());
                player.getInventory().setItem(7, Items.EDIT_KIT.getItem());

                //Check if player is leader
                if (party.getLeader().equals(player.getUniqueId())) {

                    //Give party leader items
                    player.getInventory().setItem(0, Items.DUEL_PARTIES.getItem());
                    player.getInventory().setItem(1, Items.PARTY_EVENTS.getItem());
                    player.getInventory().setItem(8, Items.DISBAND_PARTY.getItem());
                } else {
                    //Give party member items
                    player.getInventory().setItem(8, Items.LEAVE_PARTY.getItem());
                }
                break;
            }
            case SPECTATING: {
                //Give playuer spectating items
                player.getInventory().setItem(0, Items.MATCH_INFO.getItem());
                player.getInventory().setItem(8, Items.STOP_SPECTATING.getItem());
                break;
            }
        }
        player.updateInventory();
    }

    public void loadData(Player player) {

        ThreadUtil.runAsync(() -> {

            ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            MongoHandler mongoHandler = PracticePlugin.getInstance().getMongoHandler();
            MongoCollection mongoCollection = mongoHandler.getCollection("players");

            Document document = (Document) mongoCollection.find(Filters.eq("_id", player.getUniqueId())).first();

            //If document doesn't exist, create empty data for the player
            if (document == null) {
                Map<String, Object> documentMap = Maps.newHashMap();
                documentMap.put("_id", player.getUniqueId());

                mongoCollection.insertOne(new Document(documentMap));
            } else {
                Arrays.stream(Kit.values()).forEach(kit -> {

                    //Load elo
                    if (document.containsKey(kit.getName() + "-elo")) {
                        practiceProfile.setElo(kit, (int) document.get(kit.getName() + "-elo"));
                    }

                    //Load custom kits
                    for (int i = 1; i <= 5; i++) {

                        //Check if custom kit exists
                        if (document.containsKey("kit-" + kit.getName() + "-" + i)) {
                            Map<String, Object> customKitMap = (Map<String, Object>) document.get("kit-" + kit.getName() + "-" + i);

                            String name = (String) customKitMap.get("name");
                            ItemStack[] contents = InventoryUtil.itemStackArrayFromBase64((String) customKitMap.get("contents"));

                            //Apply custom kit
                            practiceProfile.setCustomKit(kit, new CustomKit(contents, i, name));
                        }
                    }
                });
            }

            //Mark data as loaded
            practiceProfile.setDataLoaded(true);

            //Call player load data event
            PlayerLoadDataEvent playerLoadDataEvent = new PlayerLoadDataEvent(player);
            playerLoadDataEvent.call();
        });
    }
}

package us.blockgame.practice.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.nametag.NametagHandler;
import us.blockgame.lib.timer.Timer;
import us.blockgame.lib.util.ItemBuilder;
import us.blockgame.lib.util.StringUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.ArenaHandler;
import us.blockgame.practice.elo.EloHandler;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.leaderboards.LeaderboardsHandler;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.snapshot.InventorySnapshot;
import us.blockgame.practice.tournament.Tournament;
import us.blockgame.practice.tournament.TournamentHandler;
import us.blockgame.practice.util.BukkitReflection;
import us.blockgame.practice.util.EloUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Match {

    @Getter
    private final Kit kit;
    @Getter
    private final List<UUID> teamOne;
    @Getter
    private final List<UUID> teamTwo;
    @Getter
    private final boolean ranked;
    @Getter
    private final boolean tournament;
    @Getter
    private boolean is1v1;
    @Getter
    private boolean is2v2;

    @Getter
    private Player t1l;
    @Getter
    private Player t2l;

    @Getter
    private PracticeProfile t1lp;
    @Getter
    private PracticeProfile t2lp;

    @Getter
    private List<UUID> spectators = new ArrayList<>();
    @Getter
    private List<UUID> dead = new ArrayList<>();
    @Getter
    private List<Item> items = new ArrayList<>();
    @Getter
    private List<UUID> exempt = new ArrayList<>();
    @Getter
    private List<Arrow> arrows = new ArrayList<>();
    @Getter
    private List<Block> placedBlocks = new ArrayList<>();

    //Set match state to starting by default
    @Getter
    private MatchState matchState = MatchState.STARTING;

    @Getter
    private boolean party;
    @Getter
    private boolean ffa;

    @Getter
    private Timer timer;

    @Getter
    private Player winner;

    @Getter
    private Arena.Entry entry;

    public void start() {
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        ArenaHandler arenaHandler = PracticePlugin.getInstance().getArenaHandler();

        //Add match to list of ongoing matches
        matchHandler.addMatch(this);

        //If ffa, teamTwo will be null
        ffa = teamTwo == null;
        timer = new Timer("match", 6, true);

        //Get team one leader variables
        t1l = Bukkit.getPlayer(teamOne.get(0));
        t1lp = profileHandler.getProfile(t1l);

        //If not ffa, get team two leader variables
        if (!ffa) {
            t2l = Bukkit.getPlayer(teamTwo.get(0));
            t2lp = profileHandler.getProfile(t2l);
        }

        //Party is if either of the two teams are in a party
        party = t1lp.getPlayerState() == PlayerState.PARTY || (!ffa && t2lp.getPlayerState() == PlayerState.PARTY);

        entry = arenaHandler.randomArena(kit);
        entry.setAvailable(false);

        matchAction(player -> {
            //Make player invisible to everyone else on the server
            hideFromAllPlayers(player);

            //Ignore spectators and those who are exempt
            if (spectators.contains(player.getUniqueId()) || exempt.contains(player.getUniqueId())) return;

            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            //If not a party match, set player state to match
            if (!party) {
                practiceProfile.setMatch(this);
                practiceProfile.setPlayerState(PlayerState.MATCH);
                practiceProfile.setQueue(null);
            } else {
                Party party = practiceProfile.getParty();
                party.setMatch(this);
                party.setPartyState(PartyState.MATCH);
            }

            is1v1 = teamOne.size() == 1 && teamTwo.size() == 1;
            is2v2 = teamOne.size() == 2 && teamTwo.size() == 2;

            //Whether the player is on team one or not
            boolean t1 = teamOne.contains(player.getUniqueId());

            //Get the opposite team's leader
            Player opponent = (t1 ? !ffa ? t2l : t1l : t1l);
            //Get opponents profile
            PracticeProfile opponentProfile = profileHandler.getProfile(opponent);

            String message = "";
            if (ffa) {
                message = ChatColor.YELLOW + "Party FFA starting with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".";
            } else if (party && !is1v1) {
                message = ChatColor.YELLOW + "Match starting against " + ChatColor.DARK_GREEN + opponent.getName() + "'s Team" + ChatColor.YELLOW + " with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".";
            } else if (is1v1) {
                message = ChatColor.YELLOW + "Match starting against " + ChatColor.DARK_GREEN + opponent.getName() + (ranked ? " (" + opponentProfile.getElo(kit) + " Elo)" : "") + ChatColor.YELLOW + " with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".";
            }
            //Send player match starting message
            player.sendMessage(message);

            //Calculate spawn point
            Vector direction = (teamOne.contains(player.getUniqueId()) ? entry.spawn1() : entry.spawn2()).clone().toVector().subtract(teamOne.contains(player.getUniqueId()) ? entry.spawn2().toVector() : entry.spawn1().toVector());
            Location fakeLocation = teamOne.contains(player.getUniqueId()) ? entry.spawn1() : entry.spawn2();
            fakeLocation.setDirection(direction.multiply(-1));
            fakeLocation.setPitch(0);

            //Teleport player to spawn
            player.teleport(fakeLocation);

            playerHandler.resetPlayer(player);

            boolean hasKits = false;
            for (int i = 1; i <= 5; i++) {
                if (practiceProfile.getCustomKit(kit, i) != null) {
                    hasKits = true;

                    CustomKit customKit = practiceProfile.getCustomKit(kit, i);
                    //Add custom kit book to inventory
                    player.getInventory().setItem(i - 1, new ItemBuilder(Items.CUSTOM_KIT.getItem().clone()).setName(customKit.getName()).toItemStack());
                }
            }

            //Check if player has kits
            if (!hasKits) {
                player.getInventory().setArmorContents(kit.getArmor().clone());
                player.getInventory().setContents(kit.getContents().clone());

                //Set given kit
                practiceProfile.setGivenKit(true);
            } else {
                //Give player default kit book
                player.getInventory().setItem(8, Items.DEFAULT_KIT.getItem());
            }
            //Set players item slot to 0
            player.getInventory().setHeldItemSlot(0);

            player.updateInventory();

            matchAction(otherPlayers -> {

                //Ignore spectators and those who are exempt
                if (spectators.contains(player.getUniqueId()) || exempt.contains(player.getUniqueId())) return;

                //Show other player to player
                player.showPlayer(otherPlayers);
            });
        });

        timer.start();
        new BukkitRunnable() {
            int i = 5;

            public void run() {

                //Cancel if the match isn't in the starting state anymore
                if (matchState != MatchState.STARTING) {
                    this.cancel();
                    return;
                }
                if (i > 0) {
                    //Send starting countdown with tick noises
                    broadcast(ChatColor.GREEN + "Starting in " + ChatColor.YELLOW + i + ChatColor.GREEN + " second" + (i == 1 ? "" : "s") + "...");
                    playSound(Sound.NOTE_STICKS);
                } else {
                    //Send match started message with ping sound
                    broadcast(ChatColor.GREEN + "Match started, good luck!");
                    playSound(Sound.NOTE_PLING);

                    //Declare this match started
                    matchState = MatchState.STARTED;
                    //Create new timer
                    timer = new Timer("match-end", -1, false);
                    timer.start();
                    //Cancel task
                    this.cancel();
                    return;
                }
                i -= 1;
            }
        }.runTaskTimerAsynchronously(PracticePlugin.getInstance(), 0L, 20L);
    }

    public void addDeath(Player player, MatchDeathReason matchDeathReason, Player killer) {
        //Ignore if the player is already dead, the match is ending, or the player is spectating
        if (dead.contains(player.getUniqueId()) || matchState == MatchState.ENDING || spectators.contains(player.getUniqueId()))
            return;

        //Mark the player as dead
        dead.add(player.getUniqueId());

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Hide player from everyone in the match
        matchAction(players -> players.hidePlayer(player));
        new InventorySnapshot(player);

        //Reset player
        playerHandler.resetPlayer(player);

        //Allow player to fly
        player.setAllowFlight(true);
        player.setFlying(true);

        boolean t1 = teamOne.contains(player.getUniqueId());

        NametagHandler nametagHandler = LibPlugin.getInstance().getNametagHandler();

        matchAction(players -> {
            String message = "";
            switch (matchDeathReason) {
                case DIED: {
                    message = nametagHandler.getPrefix(players, player) + player.getName() + ChatColor.GRAY + " has died.";
                    break;
                }
                case QUIT: {
                    message = nametagHandler.getPrefix(players, player) + player.getName() + ChatColor.GRAY + " has disconnected.";
                    break;
                }
                case KILLED: {
                    message = nametagHandler.getPrefix(players, player) + player.getName() + ChatColor.GRAY + " was slain by " + nametagHandler.getPrefix(players, killer) + killer.getName() + ChatColor.GRAY + ".";
                    break;
                }
                case LEFT: {
                    message = nametagHandler.getPrefix(players, player) + player.getName() + ChatColor.GRAY + " has left.";
                }
            }
            players.sendMessage(message);

            PracticeProfile playersProfile = profileHandler.getProfile(players);

            //If player has death lightning visible, spawn lightning at the spot of the death
            if (playersProfile.isViewDeathLightning()) {
                BukkitReflection.sendLightning(players, player.getLocation());
            }
        });

        //If there is 0 players alive on team one, declare team two the winner
        if (getAlive(true) == (ffa ? 1 : 0)) end(ffa);
            //If there is 0 alive players on team two, declare team one the winner
        else if (!ffa && getAlive(false) == 0) end(true);
    }

    public void end(boolean t1) {

        //Ignore if the match is already ending
        if (matchState == MatchState.ENDING) return;

        //Set match state to ending
        matchState = MatchState.ENDING;

        //Stop the timer
        timer.stop();

        List<String> winners = new ArrayList<>();
        List<String> losers = new ArrayList<>();

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        matchAction(player -> {
            //Add everyone from the winning team to the list of winners
            if ((t1 ? teamOne : teamTwo).contains(player.getUniqueId()) && (ffa && !dead.contains(player.getUniqueId()) || !ffa)) {
                winners.add(player.getName());
            }
            //Add everyone from the losing team to the list of losers
            if ((!ffa && (t1 ? teamTwo : teamOne).contains(player.getUniqueId())) || (ffa && dead.contains(player.getUniqueId()))) {
                losers.add(player.getName());
            }

            if (!dead.contains(player.getUniqueId()) && !spectators.contains(player.getUniqueId())) {
                new InventorySnapshot(player);

                //Reset player's inventory
                playerHandler.resetPlayer(player);
            }
        });

        broadcast(ChatColor.GREEN + "The match has ended!");

        FancyMessage fancyInventories = new FancyMessage("Inventories: ").color(ChatColor.AQUA);
        //Add all winners names to inventory message
        winners.forEach(n -> fancyInventories.then(n).color(ChatColor.GREEN).command("/inv " + n).tooltip("Click to view " + n + "'s inventory.").then(", ").color(ChatColor.GRAY));

        Iterator<String> losersIterator = losers.iterator();
        while (losersIterator.hasNext()) {

            String loserName = losersIterator.next();

            //Add losers name to inventory message
            fancyInventories.then(loserName).color(ChatColor.RED).command("/inv " + loserName).tooltip("Click to view " + loserName + "'s inventory.");

            //If another loser name, add a comma
            if (losersIterator.hasNext()) {
                fancyInventories.then(", ").color(ChatColor.GRAY);
            }
        }

        winner = (t1 ? t1l : t2l);
        Player loser = (t1 ? t2l : t1l);

        Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.getInstance(), () -> {

            //Send winner and inventory messages
            broadcast(" ");
            broadcast(ChatColor.YELLOW + "Winner" + (winners.size() == 1 ? "" : "s") + ": " + StringUtil.join(winners, ", "));
            broadcast(fancyInventories);
            if (spectators.size() > 0) {
                List<String> spectatorNames = new ArrayList<>();

                ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
                spectators.forEach(u -> {
                    Player spectator = Bukkit.getPlayer(u);
                    PracticeProfile practiceProfile = profileHandler.getProfile(spectator);

                    //Remove their name if they are in silent mode
                    if (!practiceProfile.isSilent() || !spectator.hasPermission("practice.staff")) {
                        spectatorNames.add(spectator.getName());
                    }
                });
                if (spectatorNames.size() > 0) {
                    broadcast(ChatColor.AQUA + "Spectators (" + spectators.size() + "): " + ChatColor.GRAY + StringUtil.join(spectatorNames, ", "));
                }
            }

            ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

            PracticeProfile winnerProfile = profileHandler.getProfile(winner);
            PracticeProfile loserProfile = profileHandler.getProfile(loser);

            if (ranked) {
                //Calculate new rankings
                int[] newElo = EloUtil.getNewRankings(t1lp.getElo(kit), t2lp.getElo(kit), t1);
                int eloChange = Math.abs(t1lp.getElo(kit) - newElo[0]);

                EloHandler eloHandler = PracticePlugin.getInstance().getEloHandler();

                //Set new elo
                t1lp.setElo(kit, newElo[0]);
                eloHandler.saveElo(t1l.getUniqueId(), kit, newElo[0]);

                t2lp.setElo(kit, newElo[1]);
                eloHandler.saveElo(t2l.getUniqueId(), kit, newElo[1]);

                LeaderboardsHandler leaderboardsHandler = PracticePlugin.getInstance().getLeaderboardsHandler();

                //Check if player is on the leaderboard then save the leaderboard
                if (leaderboardsHandler.getLeaderboardPlayer(kit, t1l.getUniqueId()) != null || leaderboardsHandler.getLeaderboardPlayer(kit, t2l.getUniqueId()) != null) {
                    leaderboardsHandler.saveLeaderboard(kit);
                }

                //Broadcast elo changes
                broadcast(" ");
                broadcast(ChatColor.YELLOW + "Elo Changes: " + ChatColor.GREEN + winner.getName() + " +" + (eloChange) + " (" + winnerProfile.getElo(kit) + ")" + ChatColor.GRAY + ", " + ChatColor.RED + loser.getName() + " -" + (eloChange) + " (" + loserProfile.getElo(kit) + ")");
            }
            broadcast(" ");

            //Clear items from the ground
            items.forEach(Item::remove);
            //Remove arrows from the ground
            arrows.forEach(Arrow::remove);
            placedBlocks.forEach(b -> {
                b.setType(Material.AIR);
                b.getState().update(true, true);
            });

            matchAction(player -> {

                //Remove player from match count
                kit.getRankedMatch().remove(player.getUniqueId());
                kit.getUnrankedMatch().remove(player.getUniqueId());

                PracticeProfile practiceProfile = profileHandler.getProfile(player);

                practiceProfile.setHits(0);
                practiceProfile.setCombo(0);
                practiceProfile.setLongestCombo(0);
                practiceProfile.setThrownPots(0);
                practiceProfile.setFullyLandedPots(0);
                practiceProfile.setGivenKit(false);

                //If player is not in a party, set their player state to lobby and remove match object from their profile
                if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
                    practiceProfile.setPlayerState(PlayerState.LOBBY);
                    practiceProfile.setMatch(null);
                }

                //Give player spawn items and teleport them to spawn
                playerHandler.giveItems(player);
                playerHandler.teleportSpawn(player);

            });
            if (party && !tournament) {
                Party p1 = t1lp.getParty();

                p1.setPartyState(PartyState.LOBBY);
                p1.setMatch(null);

                if (teamTwo != null) {
                    Party p2 = t2lp.getParty();

                    p2.setPartyState(PartyState.LOBBY);
                    p2.setMatch(null);
                }
            } else if (tournament) {
                Party winnerParty = winnerProfile.getParty();
                Party loserParty = loserProfile.getParty();

                //Set party state to tournament
                winnerParty.setPartyState(PartyState.TOURNAMENT);
                winnerParty.setMatch(null);

                //Set party state to lobby
                loserParty.setPartyState(PartyState.LOBBY);
                loserParty.setMatch(null);

                TournamentHandler tournamentHandler = PracticePlugin.getInstance().getTournamentHandler();
                Tournament tournament = tournamentHandler.getTournament();

                //Eliminate losers
                tournament.addDeath(loserParty);

                //Remove match from tournament
                tournament.removeMatch(this);
            }
            MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
            //Remove match from ongoing matches
            matchHandler.removeMatch(this);
            //Set arena available
            entry.setAvailable(true);
        }, 60L);
    }

    public List<UUID> getAllPlayers() {
        List<UUID> allPlayers = new ArrayList<>();

        //Add all players to list
        teamOne.stream().filter(u -> !allPlayers.contains(u)).forEach(allPlayers::add);
        teamTwo.stream().filter(u -> !allPlayers.contains(u)).forEach(allPlayers::add);
        spectators.stream().filter(u -> !allPlayers.contains(u)).forEach(allPlayers::add);
        dead.stream().filter(u -> !allPlayers.contains(u)).forEach(allPlayers::add);
        exempt.stream().filter(u -> !allPlayers.contains(u)).forEach(allPlayers::add);

        return allPlayers;
    }

    public void matchAction(Consumer<? super Player> action) {
        //Filter out exempt players
        getAllPlayers().stream().filter(u -> !exempt.contains(u)).forEach(u -> {
            Player player = Bukkit.getPlayer(u);

            //Accept action if player is online
            if (player != null) {
                action.accept(player);
            }
        });
    }

    public int getAlive(boolean t1) {
        return (t1 ? teamOne : !ffa ? teamTwo : teamOne).stream().filter(u -> !dead.contains(u)).collect(Collectors.toList()).size();
    }

    public void broadcast(String message) {
        matchAction(p -> p.sendMessage(message));
    }

    public void broadcast(FancyMessage fancyMessage) {
        matchAction(p -> fancyMessage.sendXD(p));
    }

    public void playSound(Sound sound) {
        matchAction(p -> p.playSound(p.getLocation(), sound, 20f, 20f));
    }

    public void hideFromAllPlayers(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(p -> !getAllPlayers().contains(p.getUniqueId())).forEach(p -> p.hidePlayer(player));
    }

    public int getTeam(Player player) {
        if (teamOne.contains(player.getUniqueId())) return 1;
        if (!ffa && teamTwo.contains(player.getUniqueId())) return 2;
        return 0;
    }
}

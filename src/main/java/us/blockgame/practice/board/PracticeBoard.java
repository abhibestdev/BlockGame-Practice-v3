package us.blockgame.practice.board;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.scoreboard.BGBoard;
import us.blockgame.lib.util.DurationUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.match.MatchState;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;
import us.blockgame.practice.queue.Queue;
import us.blockgame.practice.queue.QueueHandler;
import us.blockgame.practice.tournament.Tournament;
import us.blockgame.practice.tournament.TournamentHandler;
import us.blockgame.practice.tournament.TournamentState;

import java.util.*;

public class PracticeBoard implements BGBoard {

    @Override
    public String getTitle(Player player) {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "BlockGame";
    }

    @Override
    public List<String> getSlots(Player player) {
        List<String> slots = new ArrayList<>();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        //Ignore if player doesn't have a profile
        if (!profileHandler.hasProfile(player)) return slots;

        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        QueueHandler queueHandler = PracticePlugin.getInstance().getQueueHandler();
        TournamentHandler tournamentHandler = PracticePlugin.getInstance().getTournamentHandler();
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();

        slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------");
        switch (practiceProfile.getPlayerState()) {
            case LOBBY: {
                slots.add(ChatColor.WHITE + "Online: " + ChatColor.GOLD + Bukkit.getOnlinePlayers().size());
                slots.add(ChatColor.WHITE + "In Fights: " + ChatColor.GOLD + matchHandler.getInMatch());

                if (tournamentHandler.getTournament() != null) {
                    Tournament tournament = tournamentHandler.getTournament();

                    slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------");
                    slots.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Tournament:");
                    slots.add(ChatColor.GRAY + "» " + ChatColor.DARK_AQUA + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + tournament.getKit().getName() + " " + tournament.getSize() + "v" + tournament.getSize());
                    slots.add(ChatColor.GRAY + "» " + ChatColor.DARK_AQUA + "Round" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + tournament.getRound());
                    slots.add(ChatColor.GRAY + "» " + ChatColor.DARK_AQUA + "Teams" + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + tournament.getPartyList().size());
                    slots.add(ChatColor.GRAY + "» " + ChatColor.DARK_AQUA + (tournament.getTournamentState() == TournamentState.ROUND_STARTING ? "Starting" : "Time") + ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + tournament.getTimer().getTime());
                }
                break;
            }
            case QUEUE: {
                Queue queue = practiceProfile.getQueue();

                slots.add(ChatColor.WHITE + "Online: " + ChatColor.GOLD + Bukkit.getOnlinePlayers().size());
                slots.add(ChatColor.WHITE + "In Fights: " + ChatColor.GOLD + matchHandler.getInMatch());
                slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------");
                slots.add(ChatColor.GREEN + (queue.isRanked() ? "Ranked" : "Unranked") + " " + queue.getKit().getName());
                if (queue.isRanked()) {
                    slots.add(ChatColor.WHITE + "Range: " + ChatColor.GOLD + "[" + queue.getMinElo() + " -> " + queue.getMaxElo() + "]");
                }
                slots.add(ChatColor.WHITE + "Time: " + ChatColor.GOLD + DurationUtil.getDuration(queue.getStartTime()));
                break;
            }
            case MATCH: {
                Match match = matchHandler.getMatch(player);
                slots.add(ChatColor.WHITE + "Opponent: " + ChatColor.GOLD + matchHandler.getOpponent(player, match));
                slots.add(ChatColor.WHITE + (match.getMatchState() == MatchState.STARTING ? "Starting" : "Time") + ": " + ChatColor.GOLD + match.getTimer().getTime());
                break;
            }
            case PARTY: {
                Party party = practiceProfile.getParty();

                switch (party.getPartyState()) {
                    case LOBBY: {
                        slots.add(ChatColor.GREEN + "Your Party: " + ChatColor.WHITE + party.getMembers().size());
                        slots.add(" ");
                        slots.add(ChatColor.WHITE + "Online: " + ChatColor.GOLD + Bukkit.getOnlinePlayers().size());
                        slots.add(ChatColor.WHITE + "In Fights: " + ChatColor.GOLD + matchHandler.getInMatch());
                        break;
                    }
                    case MATCH: {
                        Match match = matchHandler.getMatch(player);

                        List<UUID> team = match.getTeam(player) == 1 ? match.getTeamOne() : match.getTeamTwo();
                        List<UUID> opponents = match.getTeam(player) == 1 ? match.getTeamTwo() : match.getTeamOne();

                        if (match.is1v1()) {
                            slots.add(ChatColor.WHITE + "Opponent: " + ChatColor.GOLD + matchHandler.getOpponent(player, match));
                        } else if (team.size() == 2) {
                            Player teammate = LibPlugin.getPlayer(matchHandler.getTeammate(player, match));

                            if (match.getDead().contains(teammate.getUniqueId())) {
                                slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + teammate.getName());
                                slots.add(ChatColor.DARK_RED + "RIP");
                            } else {

                                double health = Math.round(teammate.getHealth()) / 2.0;

                                slots.add(ChatColor.GREEN + teammate.getName());

                                ChatColor healthColor;

                                if (health > 8.0) {
                                    healthColor = ChatColor.GREEN;
                                } else if (health > 4.0) {
                                    healthColor = ChatColor.YELLOW;
                                } else if (health > 1.0) {
                                    healthColor = ChatColor.RED;
                                } else {
                                    healthColor = ChatColor.DARK_RED;
                                }

                                ChatColor healsColor;
                                int heals = getHeals(teammate, match.getKit());

                                if (heals > 20) {
                                    healsColor = ChatColor.GREEN;
                                } else if (heals > 12) {
                                    healsColor = ChatColor.YELLOW;
                                } else if (heals > 8) {
                                    healsColor = ChatColor.GOLD;
                                } else if (heals > 3) {
                                    healsColor = ChatColor.RED;
                                } else {
                                    healsColor = ChatColor.DARK_RED;
                                }
                                String healthString = healthColor.toString() + health + " \u2764";
                                if (heals > -1) {
                                    healthString += ChatColor.GRAY + " %splitter% " + healsColor.toString() + heals + " " + (getHealingName(match.getKit()) + (heals == 1 ? "" : "s"));
                                }
                                slots.add(healthString);
                            }
                            slots.add(" ");
                        }
                        if (team.size() > 2 && team.size() <= 5) {
                            slots.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Team");

                            team.stream().filter(u -> !match.getDead().contains(u) && !u.equals(player.getUniqueId())).forEach(u -> {
                                slots.add(ChatColor.RESET.toString() + cacheHandler.getUsername(u));
                            });
                            team.stream().filter(u -> match.getDead().contains(u) && !u.equals(player.getUniqueId())).forEach(u -> {
                                slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + cacheHandler.getUsername(u));
                            });
                        } else if (team.size() > 5) {
                            slots.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Team: " + ChatColor.RESET + match.getAlive(match.getTeam(player) == 1));
                        }
                        if (!match.is1v1()) {

                            if (opponents.size() <= 4) {
                                slots.add(ChatColor.RED.toString() + ChatColor.BOLD + "Opponents");

                                opponents.stream().filter(u -> !match.getDead().contains(u)).forEach(u -> {
                                    slots.add(ChatColor.RESET.toString() + cacheHandler.getUsername(u));
                                });
                                opponents.stream().filter(u -> match.getDead().contains(u)).forEach(u -> {
                                    slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + cacheHandler.getUsername(u));
                                });
                            } else {
                                slots.add(ChatColor.RED.toString() + ChatColor.BOLD + "Opponents: " + ChatColor.RESET + match.getAlive(match.getTeam(player) != 2));
                            }
                            slots.add(" ");
                        }
                        slots.add(ChatColor.WHITE.toString() + (match.getMatchState() == MatchState.STARTING ? "Starting" : "Time") + ": " + ChatColor.GOLD + match.getTimer().getTime());
                        break;
                    }
                }
            }
        }
        slots.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------------");

        //Clear scoreboard if player doesn't have their scoreboard enabled
        if (!practiceProfile.isScoreboard()) {
            slots.clear();
        }
        return slots;
    }

    private int getHeals(Player player, Kit kit) {

        if (kit == Kit.NODEBUFF || kit == Kit.DEBUFF) {
            return (int) Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();
        }
        if (kit == Kit.SOUP) {
            return (int) Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).map(ItemStack::getType).filter(i -> i == Material.MUSHROOM_SOUP).count();
        }
        return -1;
    }

    private String getHealingName(Kit kit) {
        switch (kit) {
            case SOUP: {
                return "soup";
            }
            case NODEBUFF: {
                return "pot";
            }
        }
        return "";
    }

    @Override
    public long getUpdateInterval() {
        return 1L;
    }

}

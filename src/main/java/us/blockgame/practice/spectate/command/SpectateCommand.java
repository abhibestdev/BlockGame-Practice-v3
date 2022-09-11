package us.blockgame.practice.spectate.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.match.MatchHandler;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class SpectateCommand {

    @Command(name = "spectate", aliases = {"spec", "sp"}, inGameOnly = true)
    public void spectate(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        Match match = matchHandler.getMatch(target);

        //Check if match is null
        if (match == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in a match.");
            return;
        }

        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is not in the lobby
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }

        //Register the player as a spectator
        match.getSpectators().add(player.getUniqueId());
        practiceProfile.setMatch(match);
        practiceProfile.setPlayerState(PlayerState.SPECTATING);

        //If player is not in silent mode, send spectating message
        if (!practiceProfile.isSilent()) {
            match.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " is now spectating.");
        }

        //Hide player from everyone in match, teleport them to the target
        match.hideFromAllPlayers(player);

        match.matchAction(players -> {
            //Hide the spectator to everyone
            if (player != players) {
                players.hidePlayer(player);
            }

            //If player is a participant of the match and alive, show them to the spectator
            if (match.getTeam(players) > 0 && !match.getDead().contains(players.getUniqueId()) && !match.getExempt().contains(players.getUniqueId())) {
                player.showPlayer(players);
            }
        });

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        playerHandler.giveItems(player);

        //Allow player to fly
        player.setAllowFlight(true);
        player.setFlying(true);

        //Teleport player to target
        player.teleport(target);
        return;
    }
}

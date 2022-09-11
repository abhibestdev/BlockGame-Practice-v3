package us.blockgame.practice.elo.command;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.cache.CacheHandler;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.elo.EloHandler;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.UUID;

public class SetEloCommand {

    @Command(name = "setelo", permission = "practice.command.setelo")
    public void setElo(CommandArgs args) {
        if (args.length() < 3) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <kit> <elo>");
            return;
        }
        CacheHandler cacheHandler = LibPlugin.getInstance().getCacheHandler();
        UUID uuid = cacheHandler.getOnlineOfflineUUID(args.getArgs(0));

        //Make sure player has logged in before
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "That player has never logged in.");
            return;
        }

        String name = args.getArgs(1);
        Kit kit = Kit.getKit(name);

        //Check if kit is null
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }

        //Check if elo is not a number
        if (!NumberUtils.isDigits(args.getArgs(2))) {
            args.getSender().sendMessage(ChatColor.RED + "Please enter a valid amount.");
            return;
        }

        int elo = Integer.parseInt(args.getArgs(2));

        EloHandler eloHandler = PracticePlugin.getInstance().getEloHandler();

        //Update Elo
        eloHandler.saveElo(uuid, kit, elo);

        Player target = Bukkit.getPlayer(uuid);

        //Check if target is online
        if (target != null) {
            ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
            PracticeProfile practiceProfile = profileHandler.getProfile(target);

            //Set profile elo
            practiceProfile.setElo(kit, elo);
        }
        args.getSender().sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.DARK_GREEN + cacheHandler.getUsername(uuid) + "'s " + ChatColor.YELLOW + "elo for the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + " to " + ChatColor.DARK_GREEN + elo + ChatColor.YELLOW + ".");
        return;
    }
}

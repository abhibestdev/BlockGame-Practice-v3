package us.blockgame.practice.board.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

public class ToggleScoreboardCommand {

    @Command(name = "togglescoreboard", aliases = {"togglesidebar", "tsb", "togglesb"}, inGameOnly = true)
    public void toggleScoreboard(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Toggle scoreboard
        practiceProfile.setScoreboard(!practiceProfile.isScoreboard());

        args.getSender().sendMessage(ChatColor.YELLOW + "You are " + (practiceProfile.isScoreboard() ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " viewing the scoreboard.");
        return;
    }
}

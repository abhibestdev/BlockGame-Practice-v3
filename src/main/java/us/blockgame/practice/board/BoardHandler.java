package us.blockgame.practice.board;

import us.blockgame.gravity.GravityPlugin;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.lib.scoreboard.ScoreboardHandler;
import us.blockgame.practice.board.command.ToggleScoreboardCommand;

public class BoardHandler {

    public BoardHandler() {
        //Set scoreboard override so Gravity board doesn't initialize
        GravityPlugin.setScoreboardOverride(true);

        ScoreboardHandler scoreboardHandler = LibPlugin.getInstance().getScoreboardHandler();

        //Set practice scoreboard
        scoreboardHandler.setScoreboard(new PracticeBoard());

        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new ToggleScoreboardCommand());
    }
}

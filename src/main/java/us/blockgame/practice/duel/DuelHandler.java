package us.blockgame.practice.duel;

import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.duel.command.AcceptCommand;
import us.blockgame.practice.duel.command.DuelCommand;

public class DuelHandler {

    public DuelHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new DuelCommand());
        commandHandler.registerCommand(new AcceptCommand());
    }
}

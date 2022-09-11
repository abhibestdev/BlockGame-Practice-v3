package us.blockgame.practice.snapshot;

import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.snapshot.command.InventoryCommand;

public class SnapshotHandler {

    public SnapshotHandler() {
        //Register Handlers
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new InventoryCommand());
    }
}

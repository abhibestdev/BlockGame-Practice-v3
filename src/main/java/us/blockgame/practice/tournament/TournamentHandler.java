package us.blockgame.practice.tournament;

import lombok.Getter;
import lombok.Setter;

public class TournamentHandler {

    @Setter @Getter private long lastTournament;
    @Setter @Getter private Tournament tournament;

    public TournamentHandler() {
        //Register commands
    /*    CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new TournamentCommand()); */

        //TODO: Finish Tournaments
    }
}

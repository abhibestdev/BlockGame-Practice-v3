package us.blockgame.practice.party;

import lombok.Getter;
import org.bukkit.Bukkit;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.party.command.PartyCommand;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {

    @Getter private List<Party> partyList;

    public PartyHandler() {
        //Initialize party list to empty list by default
        partyList = new ArrayList<>();

        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new PartyCommand());

        //Register listener
        Bukkit.getPluginManager().registerEvents(new PartyListener(), PracticePlugin.getInstance());
    }

    public void addParty(Party party) {
        //Add party to list of parties
        partyList.add(party);
    }

    public void removeParty(Party party) {
        //Remove parties from list of parties
        partyList.remove(party);
    }
}

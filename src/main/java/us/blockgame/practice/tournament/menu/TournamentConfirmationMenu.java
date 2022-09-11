package us.blockgame.practice.tournament.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.menu.Button;
import us.blockgame.lib.menu.Menu;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.tournament.Tournament;
import us.blockgame.practice.tournament.menu.button.confirmation.TournamentConfirmationSizeButton;
import us.blockgame.practice.tournament.menu.button.confirmation.TournamentConfirmationStartButton;

import java.util.Map;

public class TournamentConfirmationMenu extends Menu {

    private Kit kit;
    private Tournament tournament;

    public TournamentConfirmationMenu(Kit kit) {
        this.kit = kit;
        this.tournament = new Tournament(kit);
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Confirm Tournament";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        buttonMap.put(3, new TournamentConfirmationStartButton(tournament));
        buttonMap.put(5, new TournamentConfirmationSizeButton(tournament));

        return buttonMap;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}

package us.blockgame.practice.leaderboards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
public class LeaderboardPlayer {

    @Getter private UUID uuid;
    @Setter @Getter private int elo;

}

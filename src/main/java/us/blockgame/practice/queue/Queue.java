package us.blockgame.practice.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import us.blockgame.practice.kit.Kit;

@RequiredArgsConstructor
public class Queue {

    @Getter private final Kit kit;
    @Getter private final boolean ranked;
    @Getter private final long startTime = System.currentTimeMillis();

    @Setter @Getter private int minElo;
    @Setter @Getter private int maxElo;
}

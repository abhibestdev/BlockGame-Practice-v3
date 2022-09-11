package us.blockgame.practice.duel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import us.blockgame.practice.kit.Kit;

import java.util.UUID;

@RequiredArgsConstructor
public class Duel {

    @Getter private final UUID requester;
    @Getter private final Kit kit;
    @Getter private long timestamp = System.currentTimeMillis();
}

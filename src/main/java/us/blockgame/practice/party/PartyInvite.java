package us.blockgame.practice.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PartyInvite {

    @Getter private Party party;
    @Getter private final long timestamp = System.currentTimeMillis();
}

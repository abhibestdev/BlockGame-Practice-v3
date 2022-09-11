package us.blockgame.practice.tab;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TabType {

    PRACTICE("Practice"),
    VANILLA("Vanilla");

    @Getter private String name;
}

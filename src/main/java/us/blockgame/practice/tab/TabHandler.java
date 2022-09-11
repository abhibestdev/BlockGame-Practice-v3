package us.blockgame.practice.tab;

import lombok.Getter;
import us.blockgame.gravity.GravityPlugin;
import us.blockgame.gravity.tab.GravityTab;
import us.blockgame.lib.LibPlugin;

public class TabHandler {

    @Getter private GravityTab gravityTab;

    public TabHandler() {
        //Set tab override so Gravity tab doesn't initialize
        GravityPlugin.setTabOverride(true);

        us.blockgame.lib.tab.TabHandler tabHandler = LibPlugin.getInstance().getTabHandler();

        //Set and initialize Practice tab
        tabHandler.setTab(new PracticeTab());
        tabHandler.initialize();

        //Initialize gravity tab so we can use this as the "vanilla" tab
        gravityTab = new GravityTab();
    }
}

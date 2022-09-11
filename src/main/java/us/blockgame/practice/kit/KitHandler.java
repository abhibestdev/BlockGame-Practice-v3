package us.blockgame.practice.kit;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.CommandHandler;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.kit.command.KitCommand;

import java.util.Arrays;
import java.util.List;

public class KitHandler {

    public KitHandler() {
        //Register commands
        CommandHandler commandHandler = LibPlugin.getInstance().getCommandHandler();
        commandHandler.registerCommand(new KitCommand());

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new KitListener(), PracticePlugin.getInstance());

        //Load kits
        loadKits();
    }

    private void loadKits() {
        Arrays.stream(Kit.values()).forEach(kit -> {
            List<String> arenas = PracticePlugin.getInstance().getConfig().getStringList("kit." + kit.getName() + ".arenas");

            //Add arenas
            kit.getArenas().addAll(arenas);

            //Check if kit is set
            if (PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".inventory.contents") != null && PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".inventory.armor") != null) {
                List<ItemStack> contents = (List<ItemStack>) PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".inventory.contents");
                List<ItemStack> armor = (List<ItemStack>) PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".inventory.armor");

                //Load kit
                kit.setContents(contents.toArray(new ItemStack[0]));
                kit.setArmor(armor.toArray(new ItemStack[0]));

                //Check if edit inventory is set
                if (PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".editinventory") != null) {
                    List<ItemStack> editContents = (List<ItemStack>) PracticePlugin.getInstance().getConfig().get("kit." + kit.getName() + ".editinventory");

                    //Set edit inventory
                    kit.setEditContents(editContents.toArray(new ItemStack[0]));
                }
                //Create edit inventory
                if (kit.getEditInventory() == null) {
                    kit.setEditInventory(Bukkit.createInventory(null, 18, kit.getName()));
                }
                //Fill inventory
                if (kit.getEditContents() != null) {
                    kit.getEditInventory().setContents(kit.getEditContents());
                }
            }
        });
    }
}

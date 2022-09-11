package us.blockgame.practice.kit.command;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.arena.Arena;
import us.blockgame.practice.arena.ArenaHandler;
import us.blockgame.practice.kit.Kit;

public class KitCommand {

    @Command(name = "kit.addarena", permission = "op")
    public void kitAddArena(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <kit> <arena>");
            return;
        }
        Kit kit = Kit.getKit(args.getArgs(0));

        //Check if kit exists
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }
        ArenaHandler arenaHandler = PracticePlugin.getInstance().getArenaHandler();
        Arena arena = arenaHandler.getArena(args.getArgs(1));

        //Check if arena exists
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exist.");
            return;
        }

        //Check if kit already has arena
        if (kit.getArenas().contains(arena.name())) {
            args.getSender().sendMessage(ChatColor.RED + "That kit already has this arena.");
            return;
        }

        //Add arena
        kit.getArenas().add(arena.name());
        //Save arenas to config
        PracticePlugin.getInstance().getConfig().set("kit." + kit.getName() + ".arenas", kit.getArenas());
        PracticePlugin.getInstance().saveConfig();

        args.getSender().sendMessage(ChatColor.YELLOW + "You have added the arena " + ChatColor.GOLD + arena.name() + ChatColor.YELLOW + " to the kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "kit.delarena", permission = "op")
    public void kitDelArena(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <kit> <arena>");
            return;
        }
        Kit kit = Kit.getKit(args.getArgs(0));

        //Check if kit exists
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }
        ArenaHandler arenaHandler = PracticePlugin.getInstance().getArenaHandler();
        Arena arena = arenaHandler.getArena(args.getArgs(1));

        //Check if arena exists
        if (arena == null) {
            args.getSender().sendMessage(ChatColor.RED + "That arena doesn't exist.");
            return;
        }

        //Check if kit doesn't have arena
        if (!kit.getArenas().contains(arena.name())) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't have this arena.");
            return;
        }

        //Remove arena
        kit.getArenas().remove(arena.name());
        //Save arenas to config
        PracticePlugin.getInstance().getConfig().set("kit." + kit.getName() + ".arenas", kit.getArenas());
        PracticePlugin.getInstance().saveConfig();

        args.getSender().sendMessage(ChatColor.YELLOW + "You have removed the arena " + ChatColor.GOLD + arena.name() + ChatColor.YELLOW + " from the kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "kit.setinventory", permission = "op", inGameOnly = true)
    public void kitSetInventory(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <kit>");
            return;
        }
        Kit kit = Kit.getKit(args.getArgs(0));

        //Check if kit exists
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }
        Player player = args.getPlayer();

        //Check if player is in creative
        if (player.getGameMode() == GameMode.CREATIVE) {
            args.getSender().sendMessage(ChatColor.RED + "You cannot do this in creative.");
            return;
        }
        //Set inventory
        kit.setArmor(player.getInventory().getArmorContents().clone());
        kit.setContents(player.getInventory().getContents().clone());

        //Save inventory to config
        PracticePlugin.getInstance().getConfig().set("kit." + kit.getName() + ".inventory.contents", kit.getContents());
        PracticePlugin.getInstance().getConfig().set("kit." + kit.getName() + ".inventory.armor", kit.getArmor());
        PracticePlugin.getInstance().saveConfig();

        args.getSender().sendMessage(ChatColor.YELLOW + "You have set the inventory for the kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "kit.seteditinventory", permission = "op", inGameOnly = true)
    public void kitSetEditInventory(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <kit>");
            return;
        }
        Kit kit = Kit.getKit(args.getArgs(0));

        //Check if kit exists
        if (kit == null) {
            args.getSender().sendMessage(ChatColor.RED + "That kit doesn't exist.");
            return;
        }
        Player player = args.getPlayer();

        //Open edit inventory
        player.openInventory(kit.getEditInventory());
        return;
    }
}

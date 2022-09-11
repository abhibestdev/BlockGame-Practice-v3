package us.blockgame.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.blockgame.lib.LibPlugin;
import us.blockgame.lib.command.framework.Command;
import us.blockgame.lib.command.framework.CommandArgs;
import us.blockgame.lib.fanciful.FancyMessage;
import us.blockgame.lib.util.StringUtil;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.match.Match;
import us.blockgame.practice.match.MatchDeathReason;
import us.blockgame.practice.party.Party;
import us.blockgame.practice.party.PartyHandler;
import us.blockgame.practice.party.PartyInvite;
import us.blockgame.practice.party.PartyState;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PlayerState;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.ArrayList;
import java.util.List;

public class PartyCommand {

    @Command(name = "party", aliases = {"p"}, inGameOnly = true)
    public void party(CommandArgs args) {
        args.getSender().sendMessage(new String[]{
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------",
                ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Party Help",
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " create " + ChatColor.GRAY + "- Create a party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " invite <player> " + ChatColor.GRAY + "- Invite a player to your party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " accept <player> " + ChatColor.GRAY + "- Accept an invite to a party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " promote <player> " + ChatColor.GRAY + "- Promote a player in your party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " kick <player> " + ChatColor.GRAY + "- Kick a player from your party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " leave " + ChatColor.GRAY + "- Leave your party",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " open " + ChatColor.GRAY + "- Change party join status",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " info " + ChatColor.GRAY + "- View party info",
                ChatColor.DARK_GREEN + "/" + args.getLabel() + " disband " + ChatColor.GRAY + "- Disband your party",
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------"
        });
    }

    @Command(name = "party.create", aliases = {"p.create"}, inGameOnly = true)
    public void partyCreate(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is in the lobby
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }

        PartyHandler partyHandler = PracticePlugin.getInstance().getPartyHandler();

        Party party = new Party(player.getUniqueId());
        partyHandler.addParty(party);

        //Save party object to player's profile and set their player state to party
        practiceProfile.setParty(party);
        practiceProfile.setPlayerState(PlayerState.PARTY);

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        //Give player party items
        playerHandler.giveItems(player);

        args.getSender().sendMessage(ChatColor.YELLOW + "You have created a party!");
        return;
    }

    @Command(name = "party.invite", aliases = {"p.invite"}, inGameOnly = true)
    public void partyInvite(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }

        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
            return;
        }

        if (party.getPartyState() != PartyState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }

        //Check if party is full
        if (party.getMembers().size() >= 50) {
            args.getSender().sendMessage(ChatColor.RED + "Your party is full!");
            return;
        }

        PracticeProfile targetProfile = profileHandler.getProfile(target);

        //Make sure player is in the lobby
        if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the lobby.");
            return;
        }

        //Look for party invite that already exists
        PartyInvite existingInvite = targetProfile.getPartyInviteList().stream().filter(p -> p.getParty().equals(party) && System.currentTimeMillis() - p.getTimestamp() <= 30_000).findFirst().orElse(null);

        if (existingInvite != null) {
            args.getSender().sendMessage(ChatColor.RED + "Your party already has an existing invite out to that player. Please wait for that to expire before sending another one.");
            return;
        }

        //Add party invite
        targetProfile.getPartyInviteList().add(new PartyInvite(party));

        FancyMessage fancyInvite = new FancyMessage(player.getName()).color(ChatColor.DARK_GREEN).then(" has invited you to join their party (").color(ChatColor.YELLOW).then(String.valueOf(party.getMembers().size())).color(ChatColor.DARK_GREEN).then(") ").color(ChatColor.YELLOW).then("[Accept]").color(ChatColor.GREEN).tooltip("Click to accept " + player.getName() + "'s party invite.").command("/party accept " + player.getName());
        fancyInvite.sendXD(target);

        party.broadcast(ChatColor.DARK_GREEN + target.getName() + ChatColor.YELLOW + " has been invited to join the party.");
        return;
    }

    @Command(name = "party.accept", aliases = {"p.accept", "party.join", "p.join"}, inGameOnly = true)
    public void partyAccept(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in lobby
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in the lobby to do this.");
            return;
        }
        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        PracticeProfile targetProfile = profileHandler.getProfile(target);

        //Check if target is in party
        if (targetProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in a party.");
            return;
        }

        Party party = targetProfile.getParty();
        PartyInvite existingInvite = practiceProfile.getPartyInviteList().stream().filter(p -> p.getParty().equals(party) && System.currentTimeMillis() - p.getTimestamp() <= 30_000).findFirst().orElse(null);

        //Check if the invite exists or if the party is open
        if (existingInvite == null && !party.isOpen()) {
            args.getSender().sendMessage(ChatColor.RED + "You do not have an invite from this party.");
            return;
        }

        //Check if the party is in the lobby
        if (party.getPartyState() != PartyState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "That party is not in the lobby.");
            return;
        }

        //Check if party is full
        if (party.getMembers().size() >= 50) {
            args.getSender().sendMessage(ChatColor.RED + "That party is full!");
            return;
        }

        //Save party object to player and set player state to party
        practiceProfile.setParty(party);
        practiceProfile.setPlayerState(PlayerState.PARTY);

        //Add player to members
        party.getMembers().add(player.getUniqueId());
        party.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " has joined the party.");

        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

        //Give player party items
        playerHandler.giveItems(player);

        //Remove party invite from their list of invites
        practiceProfile.getPartyInviteList().remove(existingInvite);
        return;
    }

    @Command(name = "party.promote", aliases = {"p.promote"}, inGameOnly = true)
    public void partyPromote(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }

        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
            return;
        }

        //Check if target isn't in party
        if (!party.getMembers().contains(target.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the party.");
            return;
        }

        //Check if player is trying to promote themselves
        if (party.getLeader().equals(target.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You are already the party leader.");
            return;
        }

        //Set new leader
        party.setLeader(target.getUniqueId());
        party.broadcast(ChatColor.DARK_GREEN + target.getName() + ChatColor.YELLOW + " has been promoted to party leader.");

        //If party is in lobby, give player and new leader approproate party items
        if (party.getPartyState() == PartyState.LOBBY) {
            PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();

            playerHandler.giveItems(player);
            playerHandler.giveItems(target);
        }
        return;
    }

    @Command(name = "party.kick", aliases = {"p.kick"}, inGameOnly = true)
    public void partyKick(CommandArgs args) {
        if (args.length() < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }

        Player target = LibPlugin.getPlayer(args.getArgs(0));

        //Check if player is online
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }

        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
            return;
        }

        //Check if target isn't in party
        if (!party.getMembers().contains(target.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "That player is not in the party.");
            return;
        }

        //Force player to leave party
        target.performCommand("party leave");
        return;
    }

    @Command(name = "party.leave", aliases = {"p.leave"}, inGameOnly = true)
    public void partyLeave(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }
        Party party = practiceProfile.getParty();

        //Check if player is the party leader
        if (party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You are the party leader. You must promote someone else or disband the party.");
            return;
        }
        //Remove player from list of members
        party.getMembers().remove(player.getUniqueId());
        party.broadcast(ChatColor.DARK_GREEN + player.getName() + ChatColor.YELLOW + " has left the party.");

        //Set player state to lobby and remove party from their profile
        practiceProfile.setPlayerState(PlayerState.LOBBY);
        practiceProfile.setParty(null);

        //Check if party is in match
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        if (party.getPartyState() == PartyState.MATCH) {
            Match match = party.getMatch();

            //Add death to match
            match.addDeath(player, MatchDeathReason.LEFT, null);

            //Teleport player to spawn
            playerHandler.teleportSpawn(player);
        }
        //Give player lobby items
        playerHandler.giveItems(player);
        args.getSender().sendMessage(ChatColor.RED + "You have left the party.");
        return;
    }

    @Command(name = "party.open", aliases = {"p.open", "party.close", "p.close"}, inGameOnly = true)
    public void partyOpen(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }

        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
            return;
        }

        //Toggle party open
        party.setOpen(!party.isOpen());

        party.broadcast(ChatColor.YELLOW + "Your party is now " + (party.isOpen() ? ChatColor.GREEN + "Open" : ChatColor.RED + "Invite-Only") + ChatColor.YELLOW + ".");
        return;
    }

    @Command(name = "party.info", aliases = {"p.info"}, inGameOnly = true)
    public void partyInfo(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }
        Party party = practiceProfile.getParty();

        List<String> members = new ArrayList<>();
        Player leader = Bukkit.getPlayer(party.getLeader());
        party.partyAction(p -> {
            //Add all players besides the leader to list of members
            if (!p.getUniqueId().equals(leader.getUniqueId())) {
                members.add(p.getName());
            }
        });

        //Display party info
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
        player.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.WHITE + leader.getName());
        player.sendMessage(ChatColor.GOLD + "Members: " + ChatColor.WHITE + (members.size() == 0 ? "None" : StringUtil.join(members, ", ")));
        player.sendMessage(ChatColor.GOLD + "Status: " + ChatColor.WHITE + (party.isOpen() ? "Open" : "Invite-Only"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
        return;
    }

    @Command(name = "party.disband", aliases = {"p.disband"}, inGameOnly = true)
    public void partyDisband(CommandArgs args) {
        Player player = args.getPlayer();

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Make sure player is in party
        if (practiceProfile.getPlayerState() != PlayerState.PARTY) {
            args.getSender().sendMessage(ChatColor.RED + "You must be in a party to do this.");
            return;
        }

        Party party = practiceProfile.getParty();

        //Make sure player is party leader
        if (!party.getLeader().equals(player.getUniqueId())) {
            args.getSender().sendMessage(ChatColor.RED + "You must be party leader to to this.");
            return;
        }
        party.partyAction(member -> {
            PracticeProfile memberProfile = profileHandler.getProfile(member);

            //Set player state to lobby and remove party from their profile
            memberProfile.setPlayerState(PlayerState.LOBBY);
            memberProfile.setParty(null);

            //Check if party is in match
            PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
            if (party.getPartyState() == PartyState.MATCH) {
                Match match = party.getMatch();

                //Add death to match
                match.addDeath(member, MatchDeathReason.LEFT, null);

                //Teleport player to spawn
                playerHandler.teleportSpawn(member);
            }
            //Give player lobby items
            playerHandler.giveItems(member);
        });
        party.broadcast(ChatColor.RED + "Your party has been disbanded.");

        //Clear list of members
        party.getMembers().clear();

        PartyHandler partyHandler = PracticePlugin.getInstance().getPartyHandler();

        //Remove party from list of parties
        partyHandler.removeParty(party);
        return;
    }
}

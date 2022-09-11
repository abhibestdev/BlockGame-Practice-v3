package us.blockgame.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.blockgame.lib.event.impl.PlayerRightClickEvent;
import us.blockgame.practice.PracticePlugin;
import us.blockgame.practice.item.Items;
import us.blockgame.practice.kit.Kit;
import us.blockgame.practice.kit.menu.CustomKit;
import us.blockgame.practice.player.PlayerHandler;
import us.blockgame.practice.profile.PracticeProfile;
import us.blockgame.practice.profile.ProfileHandler;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MatchListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        //Ignore if entity isn't a player
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Match match = matchHandler.getMatch(player);

        //Ignore if player isn't in match
        if (match == null) {
            event.setCancelled(true);
            return;
        }

        //If the player is dead, cancel damage
        if (match.getDead().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        //Cancel damage if match isn't started
        if (match.getMatchState() != MatchState.STARTED) {
            event.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        //Ignore if entity isn't a player
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Match match = matchHandler.getMatch(player);

        //Ignore if player isn't in match
        if (match == null) {
            event.setFoodLevel(20);
            return;
        }

        //If the player is dead or a spectator, cancel damage
        if (match.getDead().contains(player.getUniqueId()) || match.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        //Cancel damage if match isn't started
        if (match.getMatchState() != MatchState.STARTED) {
            event.setFoodLevel(20);
            return;
        }

        if (!event.isCancelled()) {
            if (event.getFoodLevel() < player.getFoodLevel()) {
                event.setCancelled(new Random().nextInt(100) < 66);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //Check if entity who took damage is a player
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            //Check if player is in match
            if (matchHandler.getMatch(player) != null) {
                Match match = matchHandler.getMatch(player);

                //Check if damager is a player
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();

                    //If both players are on the same team, the damager is a spectator, or dead, cancel attack
                    if ((match.getTeam(player) == match.getTeam(damager) && !match.isFfa()) || match.getTeam(damager) == 0 || match.getDead().contains(damager.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }

                    PracticeProfile damagerProfile = profileHandler.getProfile(damager);

                    //Increment hits
                    damagerProfile.setHits(damagerProfile.getHits() + 1);
                    //Increment combo
                    damagerProfile.setCombo(damagerProfile.getCombo() + 1);

                    //Set longest combo
                    if (damagerProfile.getLongestCombo() < damagerProfile.getCombo()) {
                        damagerProfile.setLongestCombo(damagerProfile.getCombo());
                    }

                    PracticeProfile practiceProfile = profileHandler.getProfile(player);

                    //Reset combo
                    practiceProfile.setCombo(0);
                    return;
                }
                //Check if damager is a projectile
                if (event.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) event.getDamager();

                    //Check if shooter is a player
                    if (projectile.getShooter() instanceof Player) {
                        Player damager = (Player) projectile.getShooter();

                        //If both players are on the same team, the damager is a spectator, or dead, cancel attack
                        if ((match.getTeam(player) == match.getTeam(damager) && !match.isFfa()) || match.getTeam(damager) == 0 || match.getDead().contains(damager.getUniqueId())) {
                            event.setCancelled(true);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        //No death message
        event.setDeathMessage(null);

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //If player isn't in a match, ignore
        if (matchHandler.getMatch(player) == null) return;

        Match match = matchHandler.getMatch(player);

        //Check if player isn't the last one alive
        if (match.getAlive(match.getTeam(player) == 1) > 1) {

            //Drop items
            event.getDrops().stream().filter(i -> !i.equals(Items.DEFAULT_KIT.getItem()) && i.getType() != Items.CUSTOM_KIT.getItem().getType()).forEach(i -> {
                //Create a new item for each item drop
                Item item = player.getWorld().dropItemNaturally(player.getLocation(), i);

                //Add item to list of dropped items
                match.getItems().add(item);
            });
        }

        //Clear drops
        event.getDrops().clear();

        //Add death to the match
        match.addDeath(player, (player.getKiller() != null ? MatchDeathReason.KILLED : MatchDeathReason.DIED), player.getKiller());
        return;
    }

    @EventHandler
    public void onRightClick(PlayerRightClickEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        PlayerHandler playerHandler = PracticePlugin.getInstance().getPlayerHandler();
        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();

        Match match = matchHandler.getMatch(player);

        //Ignore if player is not in match
        if (match == null) return;

        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        Kit kit = match.getKit();

        //Check if player is holding default kit item
        if (player.getItemInHand().equals(Items.DEFAULT_KIT.getItem())) {
            event.setCancelled(true);

            playerHandler.resetPlayer(player);

            //Apply default kit
            player.getInventory().setArmorContents(kit.getArmor().clone());
            player.getInventory().setContents(kit.getContents().clone());
            player.updateInventory();

            player.sendMessage(ChatColor.AQUA + "You have been given the default " + kit.getName() + " kit!");

            //Set given kit
            practiceProfile.setGivenKit(true);
            return;
        }

        //Check if player is using custom kit item
        if (player.getItemInHand().getType() == Items.CUSTOM_KIT.getItem().getType()) {
            CustomKit customKit = practiceProfile.getCustomKit(kit, player.getInventory().getHeldItemSlot() + 1);

            //Check if custom kit exists
            if (customKit != null) {
                event.setCancelled(true);

                playerHandler.resetPlayer(player);

                //Apply default armor
                player.getInventory().setArmorContents(kit.getArmor().clone());
                //Give custom kit
                player.getInventory().setContents(customKit.getContents().clone());
                player.updateInventory();

                player.sendMessage(ChatColor.AQUA + "You have been given " + ChatColor.RESET + customKit.getName() + ChatColor.AQUA + "!");

                //Set given kit
                practiceProfile.setGivenKit(true);
                return;
            }
        }
        //Check if player is holding potion
        if (player.getItemInHand().getType() == Material.POTION) {
            Potion potion = Potion.fromItemStack(player.getItemInHand());

            //Cancel splash potions if the match hasn't started yet
            if (potion.isSplash() && match.getMatchState() != MatchState.STARTED) {
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        //If shooter isn't a player, ignore
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Match match = matchHandler.getMatch(player);

        //Ignore if player is not in a match
        if (match == null) return;

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Ignore if player match hasn't started
        if (match.getMatchState() != MatchState.STARTED) {
            event.setCancelled(true);

            //Return projectile
            returnProjectile(player, event.getEntity());
            return;
        }
        if (event.getEntity() instanceof EnderPearl) {
            if (System.currentTimeMillis() - practiceProfile.getLastEnderpearl() <= 16000L) {
                event.setCancelled(true);

                //Return projectile
                returnProjectile(player, event.getEntity());

                int difference = 16 - (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - practiceProfile.getLastEnderpearl());
                player.sendMessage(ChatColor.RED + "You must wait " + difference + "s to do this again.");
                return;
            }
            //Set last enderpearl to now
            practiceProfile.setLastEnderpearl(System.currentTimeMillis());
            return;
        }
    }

    private void returnProjectile(Player player, Projectile projectile) {
        //Check if projectile is an arrow
        if (projectile instanceof Arrow) {
            //Add arrow to inventory
            player.getInventory().addItem(new ItemStack(Material.ARROW));

            //Check if projectile is an enderpearl
        } else if (projectile instanceof EnderPearl) {
            //Add enderpearl to inventory
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
        player.updateInventory();
        return;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //Check if player is in match
        if (matchHandler.getMatch(player) != null) {
            Match match = matchHandler.getMatch(player);

            //Add placed block to match
            match.getPlacedBlocks().add(event.getBlockPlaced());
        }
    }

    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //Check if player is in match
        if (matchHandler.getMatch(player) != null) {
            Match match = matchHandler.getMatch(player);

            //Add placed block to match
            match.getPlacedBlocks().add(event.getBlockClicked().getRelative(event.getBlockFace()));
        }
    }

    @EventHandler
    public void onForm(BlockFormEvent event) {
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //Check if player is in match
        if (matchHandler.getMatch(event.getBlock().getLocation()) != null) {
            Match match = matchHandler.getMatch(event.getBlock().getLocation());

            //Add placed block to match
            match.getPlacedBlocks().add(event.getBlock());
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        //Stop grass from turning into dirt
        if (event.getBlock().getType() == Material.GRASS && event.getNewState().getType() == Material.DIRT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTo(BlockFromToEvent event) {
        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();

        //Get match from block in arena
        if (matchHandler.getMatch(event.getBlock().getLocation()) != null) {
            Match match = matchHandler.getMatch(event.getBlock().getLocation());

            //Add placed blocks
            match.getPlacedBlocks().add(event.getToBlock());
            match.getPlacedBlocks().add(event.getBlock());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        if (matchHandler.getMatch(player) != null) {
            Match match = matchHandler.getMatch(player);

            if (!match.getPlacedBlocks().contains(event.getBlock())) {
                event.setCancelled(true);
            }
            return;
        }
        if (!player.hasMetadata("build")) event.setCancelled(true);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
            PracticeProfile practiceProfile = profileHandler.getProfile(player);

            for (PotionEffect effect : event.getEntity().getEffects()) {
                if (effect.getType().equals(PotionEffectType.HEAL)) {
                    practiceProfile.setThrownPots(practiceProfile.getThrownPots() + 1);
                    if (event.getIntensity(player) >= 0.87 && event.getIntensity(player) <= 1) {
                        practiceProfile.setFullyLandedPots(practiceProfile.getFullyLandedPots() + 1);
                    }
                }
            }
            event.getAffectedEntities().forEach(e -> {
                if (e instanceof Player) {
                    Player affected = (Player) e;
                    if (!player.canSee(affected)) event.setIntensity(affected, 0);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Match match = matchHandler.getMatch(player);

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is in match
        if (match != null) {

            //Can't drop kit books
            if (!practiceProfile.isGivenKit()) {
                event.setCancelled(true);
                return;
            }

            if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.getInstance(), () -> event.getItemDrop().remove(), 5L);
                return;
            }

            match.getItems().add(event.getItemDrop());

            //Remove item
            Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.getInstance(), () -> event.getItemDrop().remove(), 100L);
        } else if (!practiceProfile.isEditing() && !player.hasMetadata("build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        MatchHandler matchHandler = PracticePlugin.getInstance().getMatchHandler();
        Match match = matchHandler.getMatch(player);

        ProfileHandler profileHandler = PracticePlugin.getInstance().getProfileHandler();
        PracticeProfile practiceProfile = profileHandler.getProfile(player);

        //Check if player is in match
        if (match == null) {
            return;
        }
        //If the player is dead or a spectator, cancel
        if (match.getDead().contains(player.getUniqueId()) || match.getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }
}

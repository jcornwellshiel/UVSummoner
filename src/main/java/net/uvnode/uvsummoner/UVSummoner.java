package net.uvnode.uvsummoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public final class UVSummoner extends JavaPlugin implements Listener {
    List<Wave> _waves;
    Random _randomizer;
    Map<String, Integer> _playerKills;
    long _lastKill;
    int _totalKills;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(this, this);
       
        _randomizer = new Random();
        _waves = new ArrayList<>();
        _playerKills = new HashMap<>();
        _lastKill = 0;
        _totalKills = 0;
        
        Map<String, Object> waveConfigList = getConfig().getConfigurationSection("waves").getValues(false);
        for (Map.Entry<String, Object> waveConfig : waveConfigList.entrySet()) {
            ConfigurationSection waveConfigSection = (ConfigurationSection) waveConfig.getValue();
            _waves.add(new Wave(
                waveConfigSection.getString("announcement"),
                waveConfigSection.getInt("killStreakThreshold"),
                waveConfigSection.getInt("minMobs"),
                waveConfigSection.getInt("maxMobs"),
                waveConfigSection.getStringList("possibleMobs")));
        }
    }
    
    @Override
    public void onDisable() {
    }
    
    @EventHandler
    private void onEntityDeathEvent(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null
            && getConfig().getStringList("worlds").contains(event.getEntity().getLocation().getWorld().getName())
            && event.getEntity().getLocation().getBlockY() >= getConfig().getInt("minY")) {
            // Limit to valid mob types
            switch(event.getEntityType()) {
                case ZOMBIE: 
                case SKELETON: 
                case SPIDER: 
                case WITHER:
                case CREEPER:
                case GIANT:
                case SLIME:
                case GHAST:
                case PIG_ZOMBIE:
                case ENDERMAN:
                case CAVE_SPIDER:
                case SILVERFISH:
                case BLAZE:
                case MAGMA_CUBE:
                case ENDER_DRAGON:
                case WITCH:
                    this.getLogger().info("Kill detected.");
                    // If the last kill was too long ago, reset the streak.
                    if(_lastKill < System.currentTimeMillis() - getConfig().getInt("killStreakTimer")*1000) {
                        _playerKills.clear();
                        _totalKills = 0;
                        _lastKill = System.currentTimeMillis();
                    }
                    
                    // Record the kill
                    if (_playerKills.containsKey(event.getEntity().getKiller().getName())) {
                        _playerKills.put(event.getEntity().getKiller().getName(), _playerKills.get(event.getEntity().getKiller().getName()) + 1);
                        _totalKills++;
                        trySpawningWave(event.getEntity().getLocation());
                    }
                    
                    if (_totalKills == getConfig().getInt("minKillStreak")) {
                        announceNearby(event.getEntity().getLocation(), getConfig().getString("killStreakStartedAnnouncement"));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void trySpawningWave(Location location) {
        Double riftChance = getConfig().getDouble("riftChance") + _totalKills * getConfig().getDouble("riftChanceIncrease");
        if (_randomizer.nextInt(100) >= riftChance) {
            Wave wave = getBestPossibleWave(_totalKills);
            if (wave != null) {
                getLogger().info(wave.getAnnouncement());
                location.getWorld().strikeLightning(location);
                announceNearby(location, wave.getAnnouncement());
                Integer numSpawns = wave.getMinMobs() + _randomizer.nextInt(wave.getMaxMobs() - wave.getMinMobs());
                if (wave.getMobs().size() > 0) {
                    for (int i = 0; i < numSpawns; i++) {
                        int xOffset = _randomizer.nextInt(getConfig().getInt("killDistance")*2) - getConfig().getInt("killDistance");
                        int zOffset = _randomizer.nextInt(getConfig().getInt("killDistance")*2) - getConfig().getInt("killDistance");
                        Location spawnLocation = location.clone();
                        spawnLocation.add(xOffset, 0, zOffset);
                        if (!spawnLocation.getBlock().isEmpty())
                            spawnLocation.setY(spawnLocation.getWorld().getHighestBlockAt(spawnLocation).getY());

                        String mobName = wave.getMobs().get(_randomizer.nextInt(wave.getMobs().size()));

                        if (mobName.equalsIgnoreCase("WitherSkeleton")) {
                            Skeleton spawn = (Skeleton) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SKELETON);
                            spawn.setSkeletonType(Skeleton.SkeletonType.WITHER);
                            switch(_randomizer.nextInt(3)) {
                                case 0:
                                    spawn.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
                                    break;
                                case 1: 
                                    spawn.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
                                    break;
                                case 2: 
                                    spawn.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
                                    break;
                                case 3: 
                                    spawn.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                                    break;
                            }
                        } else {
                            EntityType type = EntityType.fromName(mobName);
                            if (type != null) {
                                LivingEntity spawn = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, type);

                                // If it's a zombie pigman or a wolf, make it angry
                                if (type == EntityType.WOLF) {
                                    ((Wolf) spawn).setAngry(true);
                                }
                                if (type == EntityType.PIG_ZOMBIE) {
                                    switch(_randomizer.nextInt(3)) {
                                        case 0: 
                                            spawn.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD));
                                            break;
                                        case 1: 
                                            spawn.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
                                            break;
                                        case 2: 
                                            spawn.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
                                            break;
                                        case 3: 
                                            spawn.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                                            break;
                                    }
                                    ((PigZombie) spawn).setAngry(true);
                                    ((PigZombie) spawn).setAnger(24000);
                                }
                                if (type == EntityType.SKELETON) {
                                    spawn.getEquipment().setItemInHand(new ItemStack(Material.BOW));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void announceNearby(Location location, String announcement) {
        List<Player> players = location.getWorld().getPlayers();
        for (Player player : players) {
            if (location.distanceSquared(player.getLocation()) < Math.pow(getConfig().getInt("killDistance"),2)) {
                player.sendMessage(announcement);
            }
        }
    }

    private List<Wave> getPossibleWaves(Integer killCount) {
        List<Wave> possibleWaves = new ArrayList<>();
        for(Wave wave : _waves) {
            if (killCount > wave.getKillStreakThreshold())
                possibleWaves.add(wave);
        }
        return possibleWaves;
    }
    
    private Wave getBestPossibleWave(Integer killCount) {
        Wave bestPossibleWave = null;
        for(Wave wave : _waves) {
            if (killCount > wave.getKillStreakThreshold())
                if (bestPossibleWave == null 
                    || wave.getKillStreakThreshold() > bestPossibleWave.getKillStreakThreshold())
                    bestPossibleWave = wave;
        }
        return bestPossibleWave;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uvnode.uvsummoner;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 *
 * @author James Cornwell-Shiel
 */
public class Wave {
    private String _announcement;
    private int _maxMobs;
    private int _minMobs;
    private int _threatThreshold;
    private int _cooldown;
    private long _lastSpawn;
    
    private List<String> _mobs;
    Wave(String announcement, int killStreakThreshold, int minMobs, int maxMobs, List<String> mobList, int cooldown) {
        _announcement = announcement;
        _threatThreshold = killStreakThreshold;
        _minMobs = minMobs;
        _maxMobs = maxMobs;
        _mobs = mobList;
        _cooldown = cooldown * 1000;
    }

    public String getAnnouncement() {
        return _announcement;
    }

    public int getMaxMobs() {
        return _maxMobs;
    }

    public int getMinMobs() {
        return _minMobs;
    }

    public int getKillStreakThreshold() {
        return _threatThreshold;
    }

    public List<String> getMobs() {
        return _mobs;
    }
    public int getNumPossibleMobs() {
        if (_mobs != null)
            return _mobs.size();
        else
            return 0;
    }
    public boolean isOffCooldown(long currentTime) {
      if (currentTime >= _lastSpawn + _cooldown) {
        return true;
      } else {
        return false;
      }
    }
    public void markSpawned(long time) {
      _lastSpawn = time;
    }
    
}

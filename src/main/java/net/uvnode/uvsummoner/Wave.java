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
    private int _killStreakThreshold;
    private List<String> _mobs;
    Wave(String announcement, int killStreakThreshold, int minMobs, int maxMobs, List<String> mobList) {
        _announcement = announcement;
        _killStreakThreshold = killStreakThreshold;
        _minMobs = minMobs;
        _maxMobs = maxMobs;
        _mobs = mobList;
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
        return _killStreakThreshold;
    }

    public List<String> getMobs() {
        return _mobs;
    }
    
}

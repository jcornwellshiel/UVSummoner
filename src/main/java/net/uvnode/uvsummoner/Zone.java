/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.uvnode.uvsummoner;

import org.bukkit.Location;

/**
 *
 * @author James Cornwell-Shiel
 */
class Zone {
    private Integer _killCount;
    private long _lastKillTime;
    private Location _location;

    public Zone(Location location) {
        _location = location;
        _killCount = 1;
        _lastKillTime = System.currentTimeMillis();
    }
    
    public void addKill() {
        this._killCount++;
        this._lastKillTime = System.currentTimeMillis();
    }
    
    public long getLastKillTime() {
        return _lastKillTime;
    }

    public Integer getKillCount() {
        return _killCount;
    }

    public Location getLocation() {
        return _location;
    }
}

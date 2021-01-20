package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static org.kowboy.bukkit.finder.Util.formatBlockPoint;

/**
 * @author Craig McDaniel
 * @since ???
 */
public class EntityGroupSummary {
    private Location playerLoc;
    private EntityType type;
    private int count = 0;

    private Location nearest;
    private double nearestDist = Double.MAX_VALUE;

    public EntityGroupSummary(Player player, EntityType type) {
        this.type = type;
        this.playerLoc = player.getLocation();
    }

    public void acc(Entity entity) {
        this.count++;
        double d = playerLoc.distance(entity.getLocation());
        if (d < nearestDist) {
            nearestDist = d;
            nearest = entity.getLocation();
        }
    }

    @Override
    public String toString() {
        return ChatColor.GOLD +
                type.toString().toLowerCase() +
                ChatColor.GRAY +
                " * " +
                count +
                ": " +
                formatBlockPoint(nearest);
    }
}

package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import static org.kowboy.bukkit.finder.Util.formatBlockPoint;
import static org.kowboy.bukkit.finder.Util.getColor;

public class Vein implements Comparable<Vein> {
    // The first block found in this vein.
    private final Block block;

    // Approximate distance from the player - calculated from first block found in the vein.
    private double distance;

    // Keep a +1 sized bounding box around the vein, used for including newly found blocks.
    private BoundingBox box;

    // Track how many blocks are in this vein.
    private int count;

    // The block type for this vein.
    private final Material type;

    public Vein(Block block, Player player) {
        this.block = block;
        this.distance = player.getLocation().distance(block.getLocation());
        this.type = block.getType();
        this.box = BoundingBox.of(block).expand(1.0);
        this.count = 1;
    }

    /**
     * Does this vein contain the given block within it's bounding box? Is it the same material?
     *
     * @param block The block to test.
     * @return True if the block is part of the vein, false otherwise.
     */
    public boolean contains(Block block) {
        return type.equals(block.getType()) && box.contains(BoundingBox.of(block));
    }

    /**
     * If this block belongs in the vein, add it.
     *
     * @param block The block to possibly add to this vein.
     * @return The modified vein.
     */
    public Vein addBlock(Block block) {
        count++;
        box = box.union(BoundingBox.of(block).expand(1.0));
        return this;
    }

    /**
     * Detects if two veins overlap.
     *
     * @param v The vein to test against this one.
     * @return True if the two veins overlap, false otherwise.
     */
    public boolean overlaps(Vein v) {
        // Keep in mind that the bounding box is a +1 block buffer around each vein. We therefore need to shrink
        // one of them by 1 or we risk a false positive (where veins that are 1 block apart are considered overlapping).
        return box.overlaps(v.box.clone().expand(-1.0));
    }

    /**
     * Combines two veins together iff they overlap.
     *
     * @param v The vein to combine with this one.
     * @return The new combined vein iff they overlap, null otherwise.
     */
    public Vein combine(Vein v) {
        if (type.equals(v.type) && overlaps(v)) {
            this.count += v.count;
            this.distance = Math.min(this.distance, v.distance);
            this.box = box.union(v.box);
            return this;
        }
        return null;
    }

    @Override
    public int compareTo(Vein v) {
        return Double.compare(this.distance, v.distance);
    }

    /**
     * Example: diamond_ore * 4: (-13, 12, 72)
     *
     * @return String representation of this Vein.
     */
    @Override
    public String toString() {
        return getColor(this.type).toString() +
                this.type.name().toLowerCase() +
                ChatColor.GRAY +
                " * " +
                this.count +
                ": " +
                formatBlockPoint(this.block.getLocation());
    }
}

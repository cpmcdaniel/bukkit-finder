package org.kowboy.bukkit.finder;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.kowboy.bukkit.finder.Util.CHUNK_WIDTH;
import static org.kowboy.bukkit.finder.Util.getHighestBlockYAt;

public class VeinFinder implements Consumer<Chunk> {
    private final Player player;
    private final Map<Material, SortedSet<Vein>> veinsByType = new HashMap<>();
    private final Predicate<Material> blockTypeFilter;

    public VeinFinder(Player player, Predicate<Material> blockTypeFilter) {
        this.player = player;
        this.blockTypeFilter = blockTypeFilter;
    }

    @Override
    public void accept(Chunk chunk) {
        ChunkSnapshot snap = chunk.getChunkSnapshot(true, false, false);
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {
                for (int y = 1; y < getHighestBlockYAt(chunk.getWorld(), snap, x, z); y++) {
                    Material blockType = snap.getBlockType(x, y, z);
                    if (!blockTypeFilter.test(blockType)) {
                        continue;
                    }

                    // Initialize the vein set if it does not exist...
                    if (!veinsByType.containsKey(blockType)) {
                        veinsByType.put(blockType, new TreeSet<>());
                    }

                    Block block = chunk.getBlock(x, y, z);
                    Vein into = null;
                    for (Vein v : veinsByType.get(blockType)) {
                        if (v.contains(block)) {
                            into = v;
                            break;
                        }
                    }
                    if (into != null) {
                        into.addBlock(block);
                    } else {
                        Vein v = new Vein(block, player);
                        veinsByType.get(blockType).add(v);
                    }
                }
            }
        }

        // Now that we've processed an entire chunk, aggregate veins.
        combine();
    }

    // Combines vein instances that actually make up a larger vein.
    private void combine() {
        for (Material blockType : veinsByType.keySet()) {
            SortedSet<Vein> aggregated = new TreeSet<>();
            for (Vein v : veinsByType.get(blockType)) {
                Vein combined = null;
                for (Vein into : aggregated) {
                    combined = into.combine(v);
                    if (combined != null) {
                        break;
                    }
                }
                if (null == combined) {
                    aggregated.add(v);
                }
            }
            veinsByType.put(blockType, aggregated);
        }
    }

    public Collection<SortedSet<Vein>> values() {
        return veinsByType.values();
    }

    public Set<Map.Entry<Material, SortedSet<Vein>>> entries() {
        return veinsByType.entrySet();
    }
}


package org.kowboy.bukkit.finder;

import org.bukkit.Material;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlockWhitelist implements Predicate<Material> {
    private final FinderPlugin plugin;
    private final Set<Material> whiteList;
    public BlockWhitelist(FinderPlugin plugin) {
        this.plugin = plugin;
        this.whiteList = plugin.getConfig().getStringList("block-whitelist")
                .stream()
                .map(this::getMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Material getMaterial(String s) {
        Material m = Material.getMaterial(s.toUpperCase());
        if (null == m) {
            plugin.getLogger().warning("Invalid block type in block-whitelist: "+s);
        }
        return m;
    }

    @Override
    public boolean test(Material type) {
        return whiteList.contains(type);
    }
}

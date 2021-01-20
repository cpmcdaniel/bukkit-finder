package org.kowboy.bukkit.finder;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityBlacklist implements Predicate<Entity> {

    private final FinderPlugin plugin;
    private final Set<EntityType> blackList;

    public EntityBlacklist(FinderPlugin plugin) {
        this.plugin = plugin;

        this.blackList = plugin.getConfig().getStringList("entity-blacklist")
                .stream()
                .map(this::getLivingEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    private EntityType getLivingEntity(String s) {
        EntityType et = null;
        try {
            et = EntityType.valueOf(s.toUpperCase());
            if (!et.isAlive()) {
                plugin.getLogger().fine("entity-blacklist value " + s + " is not a living entity - ignoring");
                et = null;
            }
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Invalid entity type in entity-blacklist: " + s);
        }
        return et;
    }

    @Override
    public boolean test(Entity entity) {
        return !blackList.contains(entity.getType());
    }
}

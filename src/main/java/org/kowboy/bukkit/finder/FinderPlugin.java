package org.kowboy.bukkit.finder;

import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class FinderPlugin extends JavaPlugin {

    private static final String FIND_ENTITIES = "find-entities";
    private static final String FIND_ENTITY = "find-entity";
    private static final String FIND_BLOCKS = "find-blocks";
    private static final String FIND_BLOCK = "find-block";

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand(FIND_ENTITIES).setExecutor(new FindEntitiesExecutor(this));

        TabExecutor findEntity = new FindEntityExecutor(this);
        getCommand(FIND_ENTITY).setExecutor(findEntity);
        getCommand(FIND_ENTITY).setTabCompleter(findEntity);

        getCommand(FIND_BLOCKS).setExecutor(new FindBlocksExecutor(this));

        TabExecutor findBlock = new FindBlockExecutor(this);
        getCommand(FIND_BLOCK).setExecutor(findBlock);
        getCommand(FIND_BLOCK).setTabCompleter(findBlock);
    }

    @Override
    public void onDisable() {
        saveConfig();
        super.onDisable();
    }
}

package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

import static org.kowboy.bukkit.finder.Util.glow;

public class FindEntitiesExecutor implements CommandExecutor {
    private final FinderPlugin plugin;
    private final EntityBlacklist blackList;
    public FindEntitiesExecutor(FinderPlugin plugin) {
        this.plugin = plugin;
        this.blackList = new EntityBlacklist(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "---- ENTITIES FOUND ----" + ChatColor.RESET);

        Player player = (Player) sender;

        Map<EntityType, EntityGroupSummary> entityGroups = new HashMap<>();
        player.getWorld().getLivingEntities().stream()
                .filter(le -> !player.equals(le))
                .filter(blackList)
                .forEach(entity -> {
                    if (!entityGroups.containsKey(entity.getType())) {
                        entityGroups.put(entity.getType(), new EntityGroupSummary(player, entity.getType()));
                    }
                    entityGroups.get(entity.getType()).acc(entity);
                    glow(entity);
                });

        for (EntityGroupSummary summary : entityGroups.values()) {
            sender.sendMessage(summary.toString() + ChatColor.RESET);
        }
        return true;
    }
}

package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static org.kowboy.bukkit.finder.Util.getEntityType;
import static org.kowboy.bukkit.finder.Util.getPlayer;

public class FindEntityExecutor implements TabExecutor {
    private static final int TOP_N = 8;
    private final FinderPlugin plugin;
    private final Set<EntityType> entityTypes;
    private final List<String> completions;

    public FindEntityExecutor(FinderPlugin plugin) {
        this.plugin = plugin;
        this.entityTypes = Arrays.stream(EntityType.values())
                .filter(EntityType::isAlive)
                .collect(Collectors.toUnmodifiableSet());
        this.completions = entityTypes.stream()
                .map(EntityType::name)
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player = getPlayer(sender);
        if (null == player || args.length != 1) {
            return false;
        }
        EntityType typ = getEntityType(args[0]);
        if (null == typ || !entityTypes.contains(typ)) {
            sender.sendMessage(ChatColor.RED + "Invalid entity type: " + args[0] + ChatColor.RESET);
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "---- ENTITIES FOUND ----" + ChatColor.RESET);
        Location playerLoc = player.getLocation();
        player.getWorld().getLivingEntities().stream()
                .filter((le) -> typ.equals(le.getType()))
                .sorted(Comparator.comparingDouble(e -> playerLoc.distance(e.getLocation())))
                .limit(TOP_N)
                .map(Util::glow)
                .map(Util::formatEntity)
                .forEach(sender::sendMessage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length > 1) return Collections.emptyList();
        return new ArrayList<>(completions);
    }
}

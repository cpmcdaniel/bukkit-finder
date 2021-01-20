package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.kowboy.bukkit.finder.Util.chunkSpiral;
import static org.kowboy.bukkit.finder.Util.getPlayer;

public class FindBlockExecutor implements TabExecutor {
    private final FinderPlugin plugin;
    private final List<String> completions;
    private static final int TOP_N = 5;

    public FindBlockExecutor(FinderPlugin plugin) {
        this.plugin = plugin;

        this.completions = Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::name)
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player = getPlayer(sender);
        if (null == player || args.length != 1) return false;

        Material blockType = Material.getMaterial(args[0].toUpperCase());
        if (null == blockType) {
            sender.sendMessage(ChatColor.RED+"Invalid block type: " + args[0] + ChatColor.RESET);
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "---- VEINS FOUND ----" + ChatColor.RESET);

        VeinFinder finder = new VeinFinder(player, blockType::equals);
        chunkSpiral(player.getLocation(), finder);

        finder.values()
                .forEach(veins -> veins.stream().limit(TOP_N) // only show the N closest veins
                        .map(Vein::toString)
                        .forEach(sender::sendMessage));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length > 1) return Collections.emptyList();
        return completions;
    }
}

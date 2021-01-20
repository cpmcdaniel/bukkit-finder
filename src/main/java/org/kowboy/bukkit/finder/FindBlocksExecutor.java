package org.kowboy.bukkit.finder;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.SortedSet;

import static org.kowboy.bukkit.finder.Util.chunkSpiral;
import static org.kowboy.bukkit.finder.Util.getPlayer;

public class FindBlocksExecutor implements CommandExecutor {
    private final FinderPlugin plugin;
    private final BlockWhitelist whiteList;

    public FindBlocksExecutor(FinderPlugin plugin) {
        this.plugin = plugin;
        this.whiteList = new BlockWhitelist(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Player player = getPlayer(sender);
        if (null == player) return false;

        VeinFinder finder = new VeinFinder(player, whiteList);

        sender.sendMessage(ChatColor.GOLD + "---- BLOCKS FOUND ----" + ChatColor.RESET);
        chunkSpiral(player.getLocation(), finder);

        finder.values().stream()
                .map(SortedSet::first)
                .map(Vein::toString)
                .forEach(sender::sendMessage);

        return true;
    }
}

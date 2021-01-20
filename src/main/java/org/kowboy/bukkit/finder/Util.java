package org.kowboy.bukkit.finder;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

public class Util {
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MINUTE = 60 * TICKS_PER_SECOND;
    public static final int MAX_APOTHEM = 2;
    public static final int CHUNK_WIDTH = 16;

    public static Player getPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"This is a player-only command"+ChatColor.RESET);
            return null;
        }
        return (Player) sender;
    }

    public static EntityType getEntityType(String typeString) {
        try {
            return EntityType.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    public static String formatBlockPoint(Location location) {
        return formatBlockPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private static String formatBlockPoint(int blockX, int blockY, int blockZ) {
        return ChatColor.RESET + "("
                + ChatColor.RED + blockX
                + ChatColor.RESET + ", "
                + ChatColor.GREEN + blockY
                + ChatColor.RESET + ", "
                + ChatColor.BLUE + blockZ
                + ChatColor.RESET + ")";
    }

    public static String formatEntity(LivingEntity le) {
        return ChatColor.GOLD + le.getType().toString().toLowerCase() +
                ChatColor.GRAY + " - " + formatBlockPoint(le.getLocation());
    }

    /**
     * Gets the highest non-air block Y coordinate in the given chunk
     * at the given (x,z) within the chunk.
     *
     * @param w The world. Used to determine the world type.
     * @param snap The chunk snapshot.
     * @param x 0-15
     * @param z 0-15
     * @return The Y-coordinate of the highest non-air block. Returns 127 in the nether.
     */
    public static int getHighestBlockYAt(World w, ChunkSnapshot snap, int x, int z) {
        if (w.getEnvironment().equals(World.Environment.NETHER)) {
            return 127;
        }
        return snap.getHighestBlockYAt(x, z);
    }

    public static void chunkSpiral(Location center, Consumer<Chunk> consumer) {
        chunkSpiral(center, MAX_APOTHEM, consumer);
    }

    public static void chunkSpiral(Location center, int apothem, Consumer<Chunk> consumer) {
        consumer.accept(center.getChunk());
        for (int r = 1; r <= apothem; r++) {
            // Start at SW corner and go north (negative z direction)
            int x = center.getBlockX() - CHUNK_WIDTH * r;
            for (int z = center.getBlockZ() + CHUNK_WIDTH * r; z > center.getBlockZ() - CHUNK_WIDTH * r; z -= CHUNK_WIDTH) {
                Location loc = center.clone();
                loc.setX(x);
                loc.setZ(z);
                consumer.accept(loc.getChunk());
            }
            // Start at NW corner and go east (positive x direction)
            int z = center.getBlockZ() - CHUNK_WIDTH * r;
            for (x = center.getBlockX() - CHUNK_WIDTH * r; x < center.getBlockX() + CHUNK_WIDTH * r; x += CHUNK_WIDTH) {
                Location loc = center.clone();
                loc.setX(x);
                loc.setZ(z);
                consumer.accept(loc.getChunk());
            }
            // Start at NE corner and go south (positive z direction)
            x = center.getBlockX() + CHUNK_WIDTH * r;
            for (z = center.getBlockZ() - CHUNK_WIDTH * r; z < center.getBlockZ() + CHUNK_WIDTH * r; z += CHUNK_WIDTH) {
                Location loc = center.clone();
                loc.setX(x);
                loc.setZ(z);
                consumer.accept(loc.getChunk());
            }
            // Start at SE corner and go west (negative x direction)
            z = center.getBlockZ() + CHUNK_WIDTH * r;
            for (x = center.getBlockX() + CHUNK_WIDTH * r; x > center.getBlockX() - CHUNK_WIDTH * r; x -= CHUNK_WIDTH) {
                Location loc = center.clone();
                loc.setX(x);
                loc.setZ(z);
                consumer.accept(loc.getChunk());
            }
        }
    }

    public static ChatColor getColor(Material m) {
        ChatColor color;
        switch (m) {
            case COAL_ORE:
            case ANCIENT_DEBRIS:
                color = ChatColor.DARK_GRAY;
                break;
            case GRASS:
            case DIRT:
                color = ChatColor.DARK_GREEN;
                break;
            case EMERALD_ORE:
                color = ChatColor.GREEN;
                break;
            case REDSTONE_ORE:
                color = ChatColor.RED;
                break;
            case AIR:
                color = ChatColor.DARK_AQUA;
                break;
            case CLAY:
                color = ChatColor.DARK_PURPLE;
                break;
            case DIAMOND_ORE:
                color = ChatColor.AQUA;
                break;
            case WATER:
            case LAPIS_ORE:
                color = ChatColor.BLUE;
                break;
            case NETHER_QUARTZ_ORE:
            case IRON_ORE:
                color = ChatColor.WHITE;
                break;
            case NETHER_GOLD_ORE:
            case GOLD_ORE:
                color = ChatColor.GOLD;
                break;
            case GLOWSTONE:
            case SAND:
            case SANDSTONE:
                color = ChatColor.YELLOW;
                break;
            case LAVA:
                color = ChatColor.DARK_RED;
                break;
            default:
                color = ChatColor.GRAY;
        }
        return color;
    }

    public static LivingEntity glow(LivingEntity le) {
        le.setGlowing(false);
        le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, TICKS_PER_MINUTE * 5, 1));
        return le;
    }

}

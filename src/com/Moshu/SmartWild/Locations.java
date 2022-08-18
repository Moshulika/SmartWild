package com.Moshu.SmartWild;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.plugin.Plugin;
import org.popcraft.chunkyborder.BorderData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Locations {

    private static final ArrayList<Biome> blacklist = new ArrayList<>();

    static
    {

        for(Biome b : Biome.values())
        {

            if(b.toString().contains("OCEAN") || b.toString().contains("RIVER") || b.toString().contains("SNOW"))
            {
                blacklist.add(b);
            }

        }

    }

    /**
     * Checks if current location is in a WorldGuard region
     * @param loc the location
     * @param regionName the regions name
     * @return true/false
     */
    public static boolean isInRegion(Location loc, String regionName)
    {

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World w = BukkitAdapter.adapt(loc.getWorld());
        BlockVector3 v = BukkitAdapter.asBlockVector(loc);

        for (ProtectedRegion region : container.get(w).getApplicableRegions(v)) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the location is in a region at all
     * @param loc the location
     * @return true/false
     */
    public static boolean isInRegion(Location loc)
    {

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World w = BukkitAdapter.adapt(loc.getWorld());
        BlockVector3 v = BukkitAdapter.asBlockVector(loc);

        for (ProtectedRegion region : container.get(w).getApplicableRegions(v)) {
            if (!region.getId().equalsIgnoreCase("__global__")) {
                return true;
            }
        }

        return false;

    }

    /**
     * Checks if the chunk is in a region at all
     * @param c the chunk
     * @return true/false
     */
    public static boolean isInRegion(Chunk c, int y)
    {

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }

        int x = c.getX() << 4;
        int z = c.getZ() << 4;

        Location loc = new Location(c.getWorld(), x, y, z);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World w = BukkitAdapter.adapt(loc.getWorld());
        BlockVector3 v = BukkitAdapter.asBlockVector(loc);

        for (ProtectedRegion region : container.get(w).getApplicableRegions(v)) {
            if (!region.getId().equalsIgnoreCase("__global__")) {
                return true;
            }
        }

        return false;

    }

    /**
     * Checks if location is a liqud
     * @param loc the location
     * @return true/false
     */
    public static boolean isLiquid(Location loc)
    {

        return loc.getWorld().getBlockAt(loc).isLiquid() || loc.getWorld().getBlockAt(loc).getType() == Material.WATER;

    }

    /**
     * Checks if the location above the location is a liqud
     * @param loc the location
     * @return true/false
     */
    public static boolean isLiquidAbove(Location loc)
    {

        loc.setY(loc.getY() + 1);

        return loc.getWorld().getBlockAt(loc).isLiquid() || loc.getWorld().getBlockAt(loc).getType() == Material.WATER;

    }

    private static Location loc2;

    /**
     * Checks if location under the location is a liqud
     * @param loc the location
     * @return true/false
     */

    public static boolean isLiquidUnder(Location loc)
    {

        loc2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

        return loc.getWorld().getBlockAt(loc2).isLiquid() || loc.getWorld().getBlockAt(loc2).getType() == Material.WATER;

    }

    /**
     * Checks if a location is considered unsafe based on check()
     * @param loc the location
     * @return true/false
     */
    public static boolean isUnsafe(Location loc)
    {

        return !check(loc);

    }

    /**
     * Checks if a location is considere safe based on check()
     * @param loc the location
     * @return true/false
     */
    public static boolean isSafe(Location loc)
    {
        return check(loc);
    }


    /**
     *
     * @param loc, the location to be teleported to
     * @return true if the location is safe
     * and false if the location is not suitable
     * for a player
     */
    public static boolean check(Location loc)
    {

        return  isInBorder(loc) &&
                !blacklistedBiome(loc) &&
                !isLeaves(loc) &&
                !isLiquidUnder(loc);


    }

    /**
     * Get a safe, random location
     * @param w the world
     * @param distance the distance from the center of the world
     * @return the safe location
     */
    public static Location getRandomLocationMoreThan(org.bukkit.World w, int distance, int min) {

        Location loc = Utils.randomCoordonatesMoreThan(w, distance, min);

        int i = 0;

        while (Locations.isUnsafe(loc)) {

            if (i == 30) {
                return loc;
            }

            loc = Utils.randomCoordonatesMoreThan(w, distance, min);

            i++;
        }

        return loc;

    }

    /**
     * Get a safe, random location
     * @param w the world
     * @param distance the distance from the center of the world
     * @return the safe location
     */
    public static Location getRandomLocation(org.bukkit.World w, int distance) {

        Location loc = Utils.randomCoordonates(w, distance);

        int i = 0;

        while (Locations.isUnsafe(loc)) {

            if (i == 30) {
                return loc;
            }

            loc = Utils.randomCoordonates(w, distance);

            i++;
        }

        return loc;

    }

    /**
     * Check if a location is at least decent
     * @param loc the location
     * @return true/false
     */
    public static boolean goodEnough(Location loc)
    {
        return isInBorder(loc) &&
                !isLiquidUnder(loc);
    }


    /**
     * Checks if the location under the location is air
     * @param loc the location
     * @return true/false
     */
    public static boolean inAir(Location loc)
    {

        loc.setY(loc.getBlockY() - (double) 1);
        return loc.getBlock().isEmpty();

    }


    /**
     * Loads a chunk
     * @param l the location
     */
    public static void load(Location l)
    {

        if(!l.getWorld().isChunkLoaded(l.getBlockX(), l.getBlockZ()))
            l.getWorld().getChunkAt(l.getBlockX(), l.getBlockZ()).load();

    }


    /**
     * Checks if the biome at the location is a blacklisted biome
     * @param loc the location
     * @return true/false
     */
    public static boolean blacklistedBiome(Location loc)
    {

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        Biome b = loc.getWorld().getBiome(x, loc.getBlockY(), z);

        return blacklist.contains(b);

    }

    /**
     * Checks if the block at the location is a type of leaf
     * @param loc the location
     * @return true/false
     */
    public static boolean isLeaves(Location loc) {

        loc2 = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        return loc2.getWorld().getBlockAt(loc2).getType().toString().toLowerCase().contains("leaves");
    }

    /**
     * Checks if the block at the location is air
     * @param l the location
     * @return true/false
     */
    public static boolean isAir(Location l)
    {
        return l.getBlock().isEmpty();
    }

    /**
     * Checks if the current location is inside the world border
     * @param l the location
     * @return true/false
     */
    public static boolean isInBorder(Location l)
    {


        if(!Utils.isEnabled("ChunkyBorder"))
        {
            return true;
        }


        Plugin plugin = Bukkit.getPluginManager().getPlugin("ChunkyBorder");
        File f = new File(plugin.getDataFolder(), "borders.json");

        try
        {

            Map<String, BorderData> loadedBorders = new Gson().fromJson(new FileReader(f), new TypeToken<Map<String, BorderData>>() {}.getType());

            if (loadedBorders == null) return true;
            if (loadedBorders.size() == 0) return true;
            if (!loadedBorders.containsKey(l.getWorld().getName())) return true;

            double x = loadedBorders.get(l.getWorld().getName()).getRadiusX();
            double z = loadedBorders.get(l.getWorld().getName()).getRadiusZ();

            return !(l.getX() > x) && !(l.getZ() > z);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;

    }




}

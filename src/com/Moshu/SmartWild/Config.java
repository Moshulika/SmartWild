package com.Moshu.SmartWild;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Config {

    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("SmartWild");

    public static int getPrice(String s)
    {
        return plugin.getConfig().getInt("structures.options." + s + ".price", 0);
    }

    public static int getStructuresCooldown()
    {
        return plugin.getConfig().getInt("structures.cooldown", 1);
    }

    public static ArrayList<String> getEnabledWorlds()
    {

        ArrayList<String> w = new ArrayList<>();

        for(String s : plugin.getConfig().getStringList("wild.enabled-worlds"))
        {
            w.add(s);
        }

        return w;

    }

    public static boolean simpleMode()
    {
        return plugin.getConfig().getBoolean("other.simple-mode", false);
    }

    public static int simpleModeMaxDistance()
    {
        return plugin.getConfig().getInt("other.simple-mode-max-distance", 5000);
    }

    public static Material getGlassPane()
    {
        return Material.matchMaterial(plugin.getConfig().getString("wild.menu-glass", "BLACK_STAINED_GLASS_PANE"));
    }

    public static boolean structuresEnabled()
    {
        return plugin.getConfig().getBoolean("other.enable-structures");
    }

    public static boolean biomesEnabled()
    {
        return plugin.getConfig().getBoolean("other.enable-biomes") && Utils.isPaper();
    }

}

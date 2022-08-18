package com.Moshu.SmartWild;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Config {

    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("SmartWild");

    public static int getPrice(String s)
    {
        return plugin.getConfig().getInt("structures.options." + s + ".price");
    }

    public static int getStructuresCooldown()
    {
        return plugin.getConfig().getInt("structures.cooldown");
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

}

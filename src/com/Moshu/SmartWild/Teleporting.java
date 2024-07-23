package com.Moshu.SmartWild;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BiomeSearchResult;

public class Teleporting {

    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("SmartWild");

    public static int start;
    public static Cooldown c;

    public static void teleportToBiome(Player p, Biome type, int price)
    {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            p.sendTitle(Utils.parse(Utils.getLang("searching.title")).toString(), Utils.parse(Utils.getLang("searching.subtitle")).toString(), 15, 60, 15);

        });

        c = new Cooldown(p.getUniqueId(), "biome", Config.getBiomesCooldown());

        if (c.has() && !p.hasPermission("smartwild.admin")) {
            c.error();
            return;
        }

        World w = p.getWorld();

        BukkitRunnable run = new BukkitRunnable()
        {

            @Override
            public void run() {


                BiomeSearchResult restult = w.locateNearestBiome(p.getLocation(), Locations.getBorder(w), type);

                if(restult == null)
                {
                    Utils.sendParsed(p, Utils.getLang("no-biomes"));
                    return;
                }

                Location lok = restult.getLocation();

                if(lok == null)
                {
                    Utils.sendParsed(p, Utils.getLang("no-biomes"));
                    return;
                }

                Location loc = Utils.getHighestBlock(lok.getWorld(), lok.getBlockX(), lok.getBlockZ(), w.getSpawnLocation());

                Bukkit.getScheduler().runTask(plugin, () -> loc.getChunk().load());

                //Sync
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                {

                    p.setInvulnerable(true);
                    p.teleport(loc);

                    c.set();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    {

                        for(String s : plugin.getConfig().getStringList("messages.teleported-to-biome"))
                        {
                            Utils.sendParsed(p, s.replace("%biome%", type.name().replace("_", " ").toLowerCase()).replace("%price%", Integer.toString(price)));
                        }

                        p.sendTitle(Utils.parse(Utils.getLang("teleported.title")).toString(), Utils.parse(Utils.getLang("teleported.subtitle")).toString()
                                .replace("%x%", Integer.toString(loc.getBlockX()))
                                .replace("%y%", Integer.toString(loc.getBlockY()))
                                .replace("%z%", Integer.toString(loc.getBlockZ())), 30, 50, 30);

                        Utils.sendSound(p);

                        p.spawnParticle(Particle.valueOf(plugin.getConfig().getString("other.particle", "DRAGON_BREATH")), loc, 4);

                    });

                    //Sync
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {


                            if (p.isInvulnerable()) {
                                p.setInvulnerable(false);
                            }

                        }

                    }, 200);

                }, 40);

            }
        };

        run.runTask(plugin);

    }

    public static void teleportToStructure(Player p, StructureType type, int price)
    {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            p.sendTitle(Utils.parse(Utils.getLang("searching.title")).toString(), Utils.parse(Utils.getLang("searching.subtitle")).toString(), 15, 60, 15);

        });

        c = new Cooldown(p.getUniqueId(), "structure", Config.getStructuresCooldown());

        if (c.has() && !p.hasPermission("smartwild.admin")) {
            c.error();
            return;
        }

        World w = p.getWorld();

        BukkitRunnable run = new BukkitRunnable()
        {

            @Override
            public void run() {


                Location lok = w.locateNearestStructure(p.getLocation(), type, Locations.getBorder(w), false);

                if(lok == null)
                {
                    Utils.sendParsed(p, Utils.getLang("no-structures"));
                    return;
                }

                Location loc = Utils.getHighestBlock(lok.getWorld(), lok.getBlockX(), lok.getBlockZ(), w.getSpawnLocation());

                Bukkit.getScheduler().runTask(plugin, () -> loc.getChunk().load());

                //Sync
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                {

                    p.setInvulnerable(true);
                    p.teleport(loc);

                    c.set();

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    {

                        for(String s : plugin.getConfig().getStringList("messages.teleported-to-structure"))
                        {
                            Utils.sendParsed(p, s.replace("%structure%", type.getName().replace("_", " ").toLowerCase()).replace("%price%", Integer.toString(price)));
                        }

                        p.sendTitle(Utils.parse(Utils.getLang("teleported.title")).toString(), Utils.parse(Utils.getLang("teleported.subtitle")).toString()
                                .replace("%x%", Integer.toString(loc.getBlockX()))
                                .replace("%y%", Integer.toString(loc.getBlockY()))
                                .replace("%z%", Integer.toString(loc.getBlockZ())), 30, 50, 30);

                        Utils.sendSound(p);

                        p.spawnParticle(Particle.valueOf(plugin.getConfig().getString("other.particle", "DRAGON_BREATH")), loc, 4);

                    });

                    //Sync
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {


                            if (p.isInvulnerable()) {
                                p.setInvulnerable(false);
                            }

                        }

                    }, 200);

                }, 40);

            }
        };

        run.runTask(plugin);

    }

}

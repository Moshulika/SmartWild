package com.Moshu.SmartWild;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Wild {

    private static Main plugin;

    public Wild(Main plugin)
    {
        Wild.plugin = plugin;
    }

    private static HashMap<String, Integer> cooldowns = new HashMap<>();

    public static int start;
    public static Cooldown c;

    public static void randomTeleport(Player p, World world, int distance)
    {

        if(!Utils.hasMoney(p, distance)) return;

        if(!Config.getEnabledWorlds().contains(world.getName())) return;

        start = (int) System.currentTimeMillis();

        if(!cooldowns.containsKey(p.getName()))
        {
            cooldowns.put(p.getName(), 1);
        }

        c = new Cooldown(p.getUniqueId(), "wild", 30 * cooldowns.get(p.getName()));

        if (c.has() && !p.hasPermission("smartwild.admin")) {
            c.error();
            return;
        }

        BukkitRunnable run = new BukkitRunnable()
        {

            @Override
            public void run() {


                p.sendTitle(Utils.parse(Utils.getLang("searching.title")).toString(), Utils.parse(Utils.getLang("searching.subtitle")).toString(), 15, 60, 15);

                CompletableFuture<Location> l = CompletableFuture.supplyAsync(() -> Locations.getRandomLocation(world, distance));
                Location loc = l.join();

                Bukkit.getScheduler().runTask(plugin, () -> loc.getChunk().load());

                //Sync
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                {

                    if(plugin.getConfig().getBoolean("other.blindness", true)) {
                        PotionEffect nv = PotionEffectType.BLINDNESS.createEffect(40, 2);
                        p.addPotionEffect(nv, true);
                    }

                    p.setInvulnerable(true);
                    p.teleport(loc);
                    p.spawnParticle(Particle.valueOf(plugin.getConfig().getString("other.particle", "DRAGON_BREATH")), loc, 4);

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    {

                        p.sendTitle(Utils.parse(Utils.getLang("teleported.title")).toString(), Utils.parse(Utils.getLang("teleported.subtitle")).toString()
                                .replace("%x%", Integer.toString(loc.getBlockX()))
                                .replace("%y%", Integer.toString(loc.getBlockY()))
                                .replace("%z%", Integer.toString(loc.getBlockZ())), 30, 50, 30);


                        Utils.sendSound(p);

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

        run.runTaskAsynchronously(plugin);

        c.set();
        cooldowns.put(p.getName(), cooldowns.get(p.getName()) + 1);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            cooldowns.put(p.getName(), cooldowns.get(p.getName()) - 1);
        }, 60 * 20 * 15);

    }


}

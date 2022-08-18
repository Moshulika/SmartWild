package com.Moshu.SmartWild;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.structure.Structure;
import org.popcraft.chunkyborder.BorderData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Wild {

    private static Main plugin;

    private final static int small = 5000;
    private final static int medium = 8000;
    private final static int big = 13000;

    private final static int pricesmall = 500;
    private final static int pricemedium = 1500;
    private final static int pricebig = 2500;

    private final static int pricestronghold = 85;
    private final static int pricevillage = 40;
    private final static int priceocean = 60;
    private final static int pricetower = 50;
    private final static int pricemansion = 60;
    private final static int pricemineshaft = 80;

    private final static int defaultborder = 15000;

    private static HashMap<String, Integer> cooldowns = new HashMap<>();

    public Wild(Main plugin)
    {
        Wild.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {

        if(e.getView().getTitle().equals(plugin.getConfig().getString("structures.menu-title")))
        {

            if(e.getCurrentItem() == null) return;

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ = (Economy) rsp.getProvider();

            if(e.getSlot() == 11)
            {

                if(!Utils.isBeginner(p)) {

                    if (econ.getBalance(p) < pricesmall) {
                        Utils.errorAsItem(e.getCurrentItem(), Utils.getLang("no-money"));
                        return;
                    }

                }

                randomTeleport(p, p.getWorld(), small);

            }
            else if(e.getSlot() == 13)
            {
                if(!Utils.isBeginner(p)) {

                    if (econ.getBalance(p) < pricemedium) {
                        Utils.errorAsItem(e.getCurrentItem(), Utils.getLang("no-money"));
                        return;
                    }

                }

                randomTeleport(p, p.getWorld(), medium);
            }
            else if(e.getSlot() == 15)
            {

                if(!Utils.isBeginner(p)) {

                    if (econ.getBalance(p) < pricebig) {
                        Utils.errorAsItem(e.getCurrentItem(), Utils.getLang("no-money"));
                        return;
                    }

                }

                randomTeleport(p, p.getWorld(), big);

            }
            else if(e.getSlot() == 31)
            {

                openStructuresMenu(p);
                return;

            }

            p.closeInventory();

        }

        if(e.getView().getTitle().equals(plugin.getConfig().getString("structures.menu-title")))
        {

            if(e.getCurrentItem() == null) return;

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ = (Economy) rsp.getProvider();

            double bal = econ.getBalance(p);
            int z = e.getSlot();

            if(z > 9 && z < 17 || z > 18 && z < 26 || z > 27 && z < 35) {

                String n = e.getCurrentItem().getItemMeta().getDisplayName();
                StructureType type = StructureType.getStructureTypes().get(n.toUpperCase());




            }
                p.closeInventory();

        }


    }
    //wild to biome
    public static int getBorder(World w) {

        if (!Utils.isEnabled("ChunkyBorder")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError: &fChunkyBorder is not installed, /wild will not work."));
            return defaultborder;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("ChunkyBorder");
        File f = new File(plugin.getDataFolder(), "borders.json");

        try
        {

            Map<String, BorderData> loadedBorders = new Gson().fromJson(new FileReader(f), new TypeToken<Map<String, BorderData>>() {
            }.getType());

            if (loadedBorders == null) return defaultborder;
            if (loadedBorders.size() == 0) return defaultborder;
            if (!loadedBorders.containsKey(w.getName())) return defaultborder;

            return (int) loadedBorders.get(w.getName()).getRadiusX() - 1000;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultborder;

    }

    public static void openMenu(Player p)
    {

        Inventory inv = Bukkit.createInventory(null, 45, plugin.getConfig().getString("structures.menu-title"));

        int i = 9;
        int x = 0;

        ItemStack is;
        ItemMeta m;

        for(String s : plugin.getConfig().getConfigurationSection("wild.distances").getKeys(false))
        {

            if(x >= 3) break;

                is = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("wild.distances." + s + ".icon")));
                m = is.getItemMeta();
                m.setDisplayName(plugin.getConfig().getString("wild.distances." + s + ".name"));
                m.setLore(plugin.getConfig().getStringList("wild.distances." + s + ".lore"));
                is.setItemMeta(m);

                i += 2;

                inv.setItem(i, is);



            x++;

        }

        ItemStack str = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("structures.menu-item")));
        ItemMeta strm = str.getItemMeta();
        strm.displayName(Utils.parse(plugin.getConfig().getString("structures.menu-item-name")));
        str.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        strm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        strm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        strm.setLore(plugin.getConfig().getStringList("structures.menu-item.lore"));
        str.setItemMeta(strm);
        inv.setItem(31, str);

        Utils.fillWithGlass(inv);

        p.openInventory(inv);

    }

    public void openStructuresMenu(Player p)
    {

        Inventory inv = Bukkit.createInventory(null, 45, plugin.getConfig().getString("structures.menu-title"));

        ItemStack is;
        ItemMeta m;

        int i = 10;
        int x = 0;
        int z;

        for(String s : plugin.getConfig().getConfigurationSection("structures.options").getKeys(false))
        {

            z = i + x;

            if(z < 17 || z > 18 && z < 26 || z > 27 && z < 35)
            {

                is = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("structures.options." + s + ".icon")));
                m = is.getItemMeta();
                m.setDisplayName(Utils.setCapitals(plugin.getConfig().getString("structures.options." + s + ".type").toLowerCase()));
                m.setLore(plugin.getConfig().getStringList("structures.options." + s + ".lore"));
                is.setItemMeta(m);

                inv.setItem(i + x, is);

            }

            x++;

        }



        Utils.fillWithGlass(inv);

        p.openInventory(inv);

    }

    public static int start, stop, time;

    //public static Cache<String, Location> cache = new Cache<>(600, 300, 10);

    public static Cooldown c;

    public static boolean hasMoney(Player p, int distance)
    {

        RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = (Economy) rsp.getProvider();

        if(!Utils.isBeginner(p))
        {

            double bal = econ.getBalance(p);

            if(distance == small)
            {

                if (bal < pricesmall) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fNu ai suficienti bani!"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, pricesmall);

                return r.transactionSuccess();

            }
            else if(distance == medium)
            {

                if (bal <pricemedium) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fNu ai suficienti bani!"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, pricemedium);

                return r.transactionSuccess();
            }
            else if(distance == big)
            {

                if (bal < pricebig) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fNu ai suficienti bani!"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, pricebig);

                return r.transactionSuccess();
            }

        }

        return true;

    }

    //de verificat daca lumea in care e jucatoru e enabled
    //de facut cv economie
    public static void teleportToStructure(Player p, StructureType type, int price)
    {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            String v = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c&lSearching..");
            p.sendTitle(v, "", 15, 60, 15);

        });

        c = new Cooldown(p.getUniqueId(), "structure", 86400);

        if (c.has() && !p.hasPermission("engine.admin")) {
            c.error();
            return;
        }

        World w = p.getWorld();

        BukkitRunnable run = new BukkitRunnable()
        {

            @Override
            public void run() {

                //CompletableFuture<Location> l = CompletableFuture.supplyAsync(() -> Server.getMainWorld().locateNearestStructure(p.getLocation(), type, getBorder(Server.getMainWorld()), false));
                //Location lok = l.join();

                Location lok = w.locateNearestStructure(p.getLocation(), type, getBorder(w), false);

                if(lok == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fNu au fost gasite structuri in jurul tau."));
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

                        p.sendMessage(" ");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWild: &fTe-ai teleportat la cel mai apropiat " + type.getName().replace("_", " ").toLowerCase() + " pentru " + price + " ✪"));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTip: &fDaca nu esti fix la locatie, este posibil ca aceasta sa se afle sub tine!"));
                        p.sendMessage(" ");

                        String v1 = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c&lYou've been teleported");
                        String m2 = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cX: &f" + loc.getBlockX() + "&c Y: &f" + loc.getBlockY() + "&c Z: &f" + loc.getBlockZ());
                        p.sendTitle(v1, m2, 30, 50, 30);
                        Utils.sendSound(p);

                        p.spawnParticle(Particle.DRAGON_BREATH, loc, 4);

                    });

                    //Sync
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {


                            if (p.isInvulnerable() && !p.hasPermission("engine.god")) {
                                p.setInvulnerable(false);
                            }

                        }

                    }, 200);

                }, 40);

            }
        };

        run.runTask(plugin);

    }

    public static void randomTeleport(Player p, World world, int distance)
    {

        if(!hasMoney(p, distance)) return;

        start = (int) System.currentTimeMillis();

        if(!cooldowns.containsKey(p.getName()))
        {
            cooldowns.put(p.getName(), 1);
        }

        c = new Cooldown(p.getUniqueId(), "wild", 30 * cooldowns.get(p.getName()));

        if (c.has() && !p.hasPermission("engine.admin")) {
            c.error();
            return;
        }

        BukkitRunnable run = new BukkitRunnable()
        {

            @Override
            public void run() {

                String v = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c&lSearching..");
                p.sendTitle(v, "Hold tight!", 15, 60, 15);


                CompletableFuture<Location> l = CompletableFuture.supplyAsync(() -> Locations.getRandomLocation(world, distance));
                Location loc = l.join();


                //Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWild: &fTook &c" + (System.currentTimeMillis() - start) + "ms &fto find a location."));

                Bukkit.getScheduler().runTask(plugin, () -> loc.getChunk().load());

                //Sync
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                {

                    PotionEffect nv = PotionEffectType.BLINDNESS.createEffect(40, 2);
                    p.addPotionEffect(nv, true);

                    p.setInvulnerable(true);
                    p.teleport(loc);

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    {

                        String v1 = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c&lYou've been teleported");
                        String m2 = org.bukkit.ChatColor.translateAlternateColorCodes('&', "&cX: &f" + loc.getBlockX() + "&c Y: &f" + loc.getBlockY() + "&c Z: &f" + loc.getBlockZ());
                        p.sendTitle(v1, m2, 30, 50, 30);
                        Utils.sendSound(p);

                        //p.spawnParticle(Particle.DRAGON_BREATH, loc, 4);

                    });

                    //Sync
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {


                            if (p.isInvulnerable() && !p.hasPermission("engine.god")) {
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

    ArrayList<String> cooldown = new ArrayList<>();





}

package com.Moshu.SmartWild;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.StructureType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Locale;
import java.util.logging.Level;

public class Events implements Listener {

    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("SmartWild");

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {

        if(e.getView().getTitle().equals(plugin.getConfig().getString("wild.menu-title", "Random Teleport")))
        {

            if(e.getCurrentItem() == null) return;

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ = (Economy) rsp.getProvider();

            if(e.getSlot() == 11)
            {

                Wild.randomTeleport(p, p.getWorld(), plugin.getConfig().getInt("wild.distances.short.distance", 10));
                p.closeInventory();

            }
            else if(e.getSlot() == 13)
            {
                Wild.randomTeleport(p, p.getWorld(), plugin.getConfig().getInt("wild.distances.medium.distance", 10));
                p.closeInventory();
            }
            else if(e.getSlot() == 15)
            {
                Wild.randomTeleport(p, p.getWorld(), plugin.getConfig().getInt("wild.distances.long.distance", 10));
                p.closeInventory();
            }

            else if(e.getSlot() == 31 && !Config.biomesEnabled() && Config.structuresEnabled())
            {
                Menus.openStructuresMenu(p);
                return;
            }
            else if(e.getSlot() == 31 && Config.biomesEnabled() && !Config.structuresEnabled())
            {
                Menus.openBiomesMenu(p);
                return;
            }
            else if(e.getSlot() == 30 && Config.biomesEnabled() && Config.structuresEnabled())
            {
                Menus.openBiomesMenu(p);
                return;
            }
            else if(e.getSlot() == 32 && Config.biomesEnabled() && Config.structuresEnabled())
            {
                Menus.openStructuresMenu(p);
                return;
            }

        }

        if(e.getView().getTitle().equals(plugin.getConfig().getString("structures.menu-title", "Teleport to structures")))
        {

            if(e.getCurrentItem() == null) return;

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ = (Economy) rsp.getProvider();

            double bal = econ.getBalance(p);
            int z = e.getSlot();

            if(z > 9 && z < 17 || z > 18 && z < 26 || z > 27 && z < 35) {

                if(e.getCurrentItem().getType() == Config.getGlassPane()) return;

                String n = e.getCurrentItem().getItemMeta().getDisplayName();

                if(!StructureType.getStructureTypes().containsKey(n.toLowerCase().replace(" ", "_")))
                {
                    p.sendMessage("Plugin is misconfigured, tell an admin that the structures aren't valid.");
                    plugin.getLogger().log(Level.SEVERE, "Invalid structure in config.yml: " + n);
                    plugin.getLogger().log(Level.SEVERE, "List of available structures: ");

                    for(String s : StructureType.getStructureTypes().keySet())
                    {
                        Bukkit.getConsoleSender().sendMessage(s);
                    }

                    return;
                }

                StructureType type = StructureType.getStructureTypes().get(n.toLowerCase().replace(" ", "_"));

                for(String s : plugin.getConfig().getConfigurationSection("structures.options").getKeys(false))
                {

                    if(Utils.parse(Utils.setCapitals(plugin.getConfig().getString("structures.options." + s + ".type", "STONE").toLowerCase().replace("_", " "))).equalsIgnoreCase(n))
                    {

                        if(bal < Config.getPrice(s))
                        {
                            Utils.sendParsed(p, Utils.getLang("no-money"));
                            return;
                        }

                        EconomyResponse r = econ.withdrawPlayer(p, Config.getPrice(s));

                        if(r.transactionSuccess())
                        {

                            Teleporting.teleportToStructure(p, type, Config.getPrice(s));

                        }
                        else {
                            Utils.errorAsItem(e.getCurrentItem(), "Something went wrong..");
                        }

                    }

                }

                p.closeInventory();

            }


        }

        if(e.getView().getTitle().equals(plugin.getConfig().getString("biomes.menu-title", "Teleport to biomes")))
        {

            if(e.getCurrentItem() == null) return;

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();

            RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            Economy econ = (Economy) rsp.getProvider();

            double bal = econ.getBalance(p);
            int z = e.getSlot();

            if(z > 9 && z < 17 || z > 18 && z < 26 || z > 27 && z < 35) {

                if(e.getCurrentItem().getType() == Config.getGlassPane()) return;

                String n = e.getCurrentItem().getItemMeta().getDisplayName();

                Biome type = Biome.valueOf(n.toUpperCase().replace(" ", "_"));

                for(String s : plugin.getConfig().getConfigurationSection("biomes.options").getKeys(false))
                {

                    if(Utils.parse(Utils.setCapitals(plugin.getConfig().getString("biomes.options." + s + ".type", "STONE").toLowerCase().replace("_", " "))).equalsIgnoreCase(n))
                    {

                        if(bal < Config.getBiomePrice(s))
                        {
                            Utils.sendParsed(p, Utils.getLang("no-money"));
                            return;
                        }

                        EconomyResponse r = econ.withdrawPlayer(p, Config.getPrice(s));

                        if(r.transactionSuccess())
                        {

                            Teleporting.teleportToBiome(p, type, Config.getPrice(s));

                        }
                        else {
                            Utils.errorAsItem(e.getCurrentItem(), "Something went wrong..");
                        }

                    }

                }

                p.closeInventory();

            }


        }


    }

}

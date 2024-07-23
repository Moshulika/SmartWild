package com.Moshu.SmartWild;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Menus {
    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("SmartWild");

    public static void openMenu(Player p)
    {

        Inventory inv;

        if(Config.structuresEnabled() || Config.biomesEnabled()) {

            inv = Bukkit.createInventory(null, 45, Utils.parse(plugin.getConfig().getString("wild.menu-title", "Random Teleport")));

        }
        else {
            inv = Bukkit.createInventory(null, 27, Utils.parse(plugin.getConfig().getString("wild.menu-title", "Random Teleport")));
        }

        int i = 9;
        int x = 0;

        ItemStack is;
        ItemMeta m;

        for(String s : plugin.getConfig().getConfigurationSection("wild.distances").getKeys(false))
        {

            if(x >= 3) break;

            is = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("wild.distances." + s + ".icon", "STONE")));
            m = is.getItemMeta();
            m.setDisplayName(Utils.parse(plugin.getConfig().getString("wild.distances." + s + ".name")));

            ArrayList<String> lore = new ArrayList<>();

            for(String str : plugin.getConfig().getStringList("wild.distances." + s + ".lore"))
            {
                lore.add(Utils.parse(str));
            }

            m.setLore(lore);
            is.setItemMeta(m);

            i += 2;

            inv.setItem(i, is);

            x++;

        }

        ItemStack str = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("structures.menu-item", "STONE")));
        ItemMeta strm = str.getItemMeta();
        strm.setDisplayName(Utils.parse(plugin.getConfig().getString("structures.menu-item-name")));
        str.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        strm.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        strm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        strm.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        strm.setLore(plugin.getConfig().getStringList("structures.menu-item-lore"));
        str.setItemMeta(strm);

        if(Config.structuresEnabled() && Config.biomesEnabled()) {
            inv.setItem(32, str);
        }
        else if(Config.structuresEnabled() && !Config.biomesEnabled())
        {
            inv.setItem(31, str);
        }

        ItemStack str2 = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("biomes.menu-item", "STONE")));
        ItemMeta strm2 = str2.getItemMeta();
        strm2.setDisplayName(Utils.parse(plugin.getConfig().getString("biomes.menu-item-name")));
        str2.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        strm2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        strm2.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        strm2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        strm2.setLore(plugin.getConfig().getStringList("biomes.menu-item-lore"));
        str2.setItemMeta(strm2);

        if(Config.structuresEnabled() && Config.biomesEnabled()) {
            inv.setItem(30, str2);
        }
        else if(!Config.structuresEnabled() && Config.biomesEnabled())
        {
            inv.setItem(31, str2);
        }

        Utils.fillWithGlass(inv);

        p.openInventory(inv);

    }

    public static void openStructuresMenu(Player p)
    {

        Inventory inv = Bukkit.createInventory(null, 45, Utils.parse(plugin.getConfig().getString("structures.menu-title", "Teleport to structures")));

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

                is = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("structures.options." + s + ".icon", "STONE")));
                m = is.getItemMeta();
                m.setDisplayName(Utils.parse(Utils.setCapitals(plugin.getConfig().getString("structures.options." + s + ".type", "STONE").toLowerCase().replace("_", " "))));

                ArrayList<String> lore = new ArrayList<>();

                for(String str : plugin.getConfig().getStringList("structures.options." + s + ".lore"))
                {
                    lore.add(Utils.parse(str));
                }

                m.setLore(lore);

                is.setItemMeta(m);

                inv.setItem(i + x, is);

            }

            x++;

        }



        Utils.fillWithGlass(inv);

        p.openInventory(inv);

    }

    public static void openBiomesMenu(Player p)
    {

        Inventory inv = Bukkit.createInventory(null, 45, plugin.getConfig().getString("biomes.menu-title", "Teleport to biomes"));

        ItemStack is;
        ItemMeta m;

        int i = 10;
        int x = 0;
        int z;

        for(String s : plugin.getConfig().getConfigurationSection("biomes.options").getKeys(false))
        {

            z = i + x;

            if(z < 17 || z > 18 && z < 26 || z > 27 && z < 35)
            {

                is = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("biomes.options." + s + ".icon", "STONE")));
                m = is.getItemMeta();
                m.setDisplayName(Utils.parse(Utils.setCapitals(plugin.getConfig().getString("biomes.options." + s + ".type", "STONE").toLowerCase().replace("_", " "))));

                ArrayList<String> lore = new ArrayList<>();

                for(String str : plugin.getConfig().getStringList("biomes.options." + s + ".lore"))
                {
                    lore.add(Utils.parse(str));
                }

                m.setLore(lore);
                is.setItemMeta(m);

                inv.setItem(i + x, is);

            }

            x++;

        }



        Utils.fillWithGlass(inv);

        p.openInventory(inv);

    }

}

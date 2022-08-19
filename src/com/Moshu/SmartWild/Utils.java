package com.Moshu.SmartWild;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities class, lots of useful stuff
 */
public class Utils
{

    public static Main plugin;

    /**
     * @hidden
     */
    public Utils(Main plugin)
    {

        Utils.plugin = plugin;
    }

    /**
     * Checks if the server is running Paper or spigot.
     * @return true/false
     */
    public static boolean isPaper()
    {
        return Bukkit.getVersion().contains("Paper");
    }

    /**
     * @return a checkmark
     */
    public static String succesSymbol()
    {
        return "&8(&a✔&8) &f";
    }

    /**
     * @return an x
     */
    public static String errorSymbol()
    {
        return "&8(&c❌&8) &f";
    }

    public static boolean hasMoney(Player p, int distance)
    {

        RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = (Economy) rsp.getProvider();

        if(!Utils.isBeginner(p))
        {

            double bal = econ.getBalance(p);

            if(distance == plugin.getConfig().getInt("wild.distances.short.distance", 5000))
            {

                if (bal < plugin.getConfig().getInt("wild.distances.short.price", 5000)) {
                    Utils.sendParsed(p, Utils.getLang("no-money"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, plugin.getConfig().getInt("wild.distances.short.price", 5000));

                return r.transactionSuccess();

            }
            else if(distance == plugin.getConfig().getInt("wild.distances.medium.distance", 5000))
            {

                if (bal < plugin.getConfig().getInt("wild.distances.medium.price", 5000)) {
                    Utils.sendParsed(p, Utils.getLang("no-money"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, plugin.getConfig().getInt("wild.distances.medium.price", 5000));

                return r.transactionSuccess();
            }
            else if(distance == plugin.getConfig().getInt("wild.distances.long.distance", 5000))
            {

                if (bal < plugin.getConfig().getInt("wild.distances.long.price", 5000)) {
                    Utils.sendParsed(p, Utils.getLang("no-money"));
                    return false;
                }

                EconomyResponse r = econ.withdrawPlayer(p, plugin.getConfig().getInt("wild.distances.long.price", 5000));

                return r.transactionSuccess();
            }

        }

        return true;

    }

    /**
     * Fill an inventory with colored glass
     * @param inv the inventory to be filled
     */
    public static void fillWithGlass(Inventory inv)
    {

        ItemStack sticla = new ItemStack(Config.getGlassPane());

        ItemMeta sticlam = sticla.getItemMeta();
        sticlam.setDisplayName(" ");
        sticlam.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
        sticla.setItemMeta(sticlam);

        ItemStack sticlafinal = sticla;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {
            for(int i = 0; i < inv.getSize(); i++)
            {

                if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
                {

                    inv.setItem(i, sticlafinal);

                }

            }
        });
    }

    /**
     * Send an error as an item with custom display name.
     * When a player clicks on an item in an inventory and an error happens,
     * the item will transform into a Material.BARRIER and the error message will be displayed
     * for a brief period of time, after which the item will reappear in the menu as before.
     * @param is the inventory holding the item
     * @param error the error (should be as short as possible)
     */
    public static void errorAsItem(ItemStack is, String error)
    {

        Material initialmat = is.getType();
        String initialname = is.getItemMeta().getDisplayName();

        ItemMeta im = is.getItemMeta();

        is.setType(Material.BARRIER);
        im.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', "&c" + error));
        is.setItemMeta(im);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            is.setType(initialmat);
            im.setDisplayName(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', initialname));
            is.setItemMeta(im);
        }, 100);

        return;


    }

    public static long getPlayedTime(Player p)
    {
        //Tick, Secunde, Minute, Ore.
        return ((p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60;
    }

    public static boolean isBeginner(Player p)
    {
        //Aici trebuie sa luam din config
        if(getPlayedTime(p) <= plugin.getConfig().getInt("beginner-time"))
        {
            return true;
        }

        return false;

    }

    /**
     * Formats a message with legacy color codes and also HEX
     * @param message the message you want to apply colors to
     * @return the formatted string
     */
    public static String format(String message) {
//ceva nu merge aici daca e stringu prea mic cred si de aici se fute globalu??

        if (message == null || message.length() == 0) return message;

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        String color;

        while (matcher.find()) {
            color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }


        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);

    }

    /**
     * Check if a plugin is enabled
     * @param plugin the plugin to check
     * @return true/false
     */
    public static boolean isEnabled(String plugin) {

        return Bukkit.getPluginManager().getPlugin(plugin) != null && Bukkit.getPluginManager().getPlugin(plugin).isEnabled();

    }

    public static void sendConsoleParsed(String s)
    {

        if(isPaper())
        {
            MiniMessage mm = MiniMessage.miniMessage();
            Bukkit.getConsoleSender().sendMessage(mm.deserialize(s));
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }

    }

    public static String parse(String s)
    {
        if(isPaper())
        {
            MiniMessage mm = MiniMessage.miniMessage();
            return mm.serialize(mm.deserialize(s));
        }
        else {
            return ChatColor.translateAlternateColorCodes('&', s);
        }
    }

    public static void sendParsed(Player p, String s)
    {

        if(isPaper())
        {
            MiniMessage mm = MiniMessage.miniMessage();
            p.sendMessage(mm.deserialize(s));
        }
        else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }

    }

    /**
     * Sets a capital letter on the first word of the string
     * @param s the string
     * @return the formatted string
     */
    public static String setCapitals(String s)
    {

        if(s.length() < 1) return s;

        String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
        return cap;
    }


    public static String getLang(String path)
    {
        return plugin.getConfig().getString("messages." + path);
    }

    public static Location getHighestBlock(World world, int x, int z, Location backup)
    {

        int i = 255;

        while (i >= 60) {
            if (!new Location(world, x, i, z).getBlock().isEmpty())
            {
                return new Location(world, x, i, z).add(0.0D, 1.0D, 0.0D);
            }
            i--;
        }

        return backup;
    }

    /**
     * Get the highest block at a location for a nether world
     * @param world the world
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the location with the highest block
     */
    public static Location getHighestBlockNether(World world, int x, int z, Location backup)
    {

        int i = 31;

        while (i <= 120) {
            if (new Location(world, x, i, z).getBlock().isEmpty()) {
                return new Location(world, x, i, z);
            }
            i++;
        }

        return backup;
    }

    /**
     * Get the highest block at a location for an end world
     * @param world the world
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the location with the highest block
     */
    public static Location getHighestBlockEnd(World world, int x, int z)
    {

        int i = 15;

        while (i <= 255) {
            if (new Location(world, x, i, z).getBlock().isEmpty()) {
                return new Location(world, x, i, z);
            }
            i++;
        }

        return world.getSpawnLocation();
    }


    /**
     *
     * @param world, world to teleport into
     * @param max, max coordinates to teleport
     * @return the highest block at a locations
     */

    public static Location randomCoordonates(World world, int max)
    {

        int x = (int) (Math.random() * max + 1);
        int z = (int) (Math.random() * max + 1);

        return getHighestBlock(world, x, z, world.getSpawnLocation());
    }

    /**
     *
     * @param world, world to teleport into
     * @param max, max coordinates to teleport
     * @return the highest block at a locations
     */

    public static Location randomCoordonatesMoreThan(World world, int max, int min)
    {

        int x = (int) (Math.random() * max + 1);
        int z = (int) (Math.random() * max + 1);

        while(x < min || z < min)
        {
            x = (int) (Math.random() * max + 1);
            z = (int) (Math.random() * max + 1);
        }

        return getHighestBlock(world, x, z, world.getSpawnLocation());
    }


    /**
     * @hidden
     */
    public static void sendNoAccess(Player p)
    {
        p.sendTitle(Utils.parse(Utils.getLang("no-access.title")).toString(), Utils.parse(Utils.getLang("no-access.subtitle")).toString(), 30, 50, 30);
        sendSound(p);
    }

    /**
     * @hidden
     */
    public static void sendNotPlayer()
    {
        sendConsoleParsed(getLang("not-player"));
    }

    /**
     * @hidden
     */
    public static void sendTargetNull(Player p)
    {
        String i = ChatColor.translateAlternateColorCodes('&', "&c&lOops");
        String m = ChatColor.translateAlternateColorCodes('&', "&fPlayer is offline");
        sendSound(p);
        p.sendTitle(i, m, 30, 50, 30);
    }

    /**
     * @hidden
     */
    public static void sendSound(Player p)
    {
        p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("other.sound")), 1.0F, 1.0F);
    }

    /**
     * Send a player a certain sound
     * @param p the player
     * @param s the sound
     */
    public static void sendSound(Player p, Sound s)
    {
        p.playSound(p.getLocation(), s, 1.0F, 1.0F);
    }

    /**
     * @hidden
     */
    public static void sendSoundHigh(Player p)
    {
        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
    }

    /**
     * @hidden
     */
    public static void sendBreakSound(Player p)
    {
        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1.0F, 1.0F);
    }

    /**
     * @hidden
     */
    public static void sendLevelupSound(Player p)
    {
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    /**
     * @hidden
     */
    public static void sendNotInt(Player p)
    {
        String i = ChatColor.translateAlternateColorCodes('&', "&c&lOops");
        String m = ChatColor.translateAlternateColorCodes('&', "&fArgumentul trebuie sa fie un numar");
        sendSound(p);
        p.sendTitle(i, m, 30, 50, 30);
    }

    /**
     * Check if a string contains only letters
     * @param s the string
     * @return true/false
     */
    public static boolean validString(String s)
    {
        String regex = "^[a-zA-Z]*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }

    /**
     * Check if a string contains only alphanumerical characters
     * @param s the string
     * @return true/false
     */
    public static boolean validAlphanumericString(String s)
    {
        String regex = "^[a-zA-Z0-9]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        return matcher.matches();
    }

    /**
     * @hidden
     */
    public static void sendError(Player p, String s)
    {
        String i = ChatColor.translateAlternateColorCodes('&', "&c&lOops");
        String m = ChatColor.translateAlternateColorCodes('&', s);
        sendSound(p);
        p.sendTitle(i, m, 30, 50, 30);
    }

    /**
     * @hidden
     */
    public static String[] wrapText(String s)
    {

        StringBuilder sb = new StringBuilder(s);

        int i = 0;
        while ((i = sb.indexOf(" ", i + 30)) != -1) {
            sb.replace(i, i + 1, "♥");
        }

        return sb.toString().split("♥");
    }

    /**
     * Checks if the player has all the slots of the inventory occupied
     * @param p the player
     * @return true/false
     */
    public static boolean hasFullInventory(Player p)
    {
        return p.getInventory().firstEmpty() == -1;
    }


    /**
     * Check if a String is actually an int
     * @param str the string
     * @return true/false
     */
    public static boolean isInt(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {}
        return false;
    }

    /**
     * Check if a char is actually an int
     * @param c the char
     * @return true/false
     */
    public static boolean isInt(char c)
    {
        try
        {
            Integer.parseInt(String.valueOf(c));
            return true;
        }
        catch (NumberFormatException e) {}
        return false;
    }


    /**
     * Check if a String is actually a double
     * @param str the string
     * @return true/false
     */
    public static boolean isDouble(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (NumberFormatException e) {}
        return false;
    }



    /**
     * Get a property from server.properties file
     * @param s the string you want to get
     * @param f the file you wish to access
     * @return the value you wish to get
     */
    public static String getProperty(String s, File f)
    {
        Properties pr = new Properties();

        try
        {
            FileInputStream in = new FileInputStream(f);
            pr.load(in);
            String string = pr.getProperty(s);
            return string;
        }

        catch (IOException e)
        { }

        return "";
    }

    /**
     * Get the main world of the server
     * @return the main world's name
     */
    public static String getMainWorld()
    {

        File s = new File("server.properties");
        return getProperty("level-name", s);

    }




}


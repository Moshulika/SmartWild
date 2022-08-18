package com.Moshu.SmartWild;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private Wild wild = new Wild(this);

    public void onEnable()
    {

        CommandSender s = Bukkit.getConsoleSender();
        long start = (int) System.currentTimeMillis();
        PluginDescriptionFile pdfFile = getDescription();

        s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lEngine &f---------------------"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fAuthor: &cMoshu"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fVersion: &c" + pdfFile.getVersion()));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fCommands: &c" + pdfFile.getCommands().size()));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fPermissions: &c" + pdfFile.getPermissions().size()));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));

        createFiles();
        setupEconomy();

        getCommand("wild").setExecutor(new Commands());
        Bukkit.getPluginManager().registerEvents(wild, this);

        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        econ = (Economy) rsp.getProvider();

        long stop = (int) System.currentTimeMillis();

        long time = stop - start;

        s.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&c&lStartup: &fTook &c" + time + "ms &fto enable."));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lmc.b-zone.ro &f--------------------"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));

    }

    public void onDisable()
    {

    }

    File cooldownsf;
    FileConfiguration cooldowns;

    public FileConfiguration getCooldownsFile()
    {
        return cooldowns;
    }


    public void createFiles()
    {

        cooldownsf = new File(getDataFolder() + "cooldowns.yml");

        if(!cooldownsf.exists())
        {
            cooldownsf.getParentFile().mkdirs();
            saveResource("cooldowns.yml", false);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l* &cCooldowns.yml &fnot found, creating."));
        }

        cooldowns = new YamlConfiguration();

        try
        {
            cooldowns.load(cooldownsf);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

    }

    public boolean hasDependencies()
    {

        if(!setupEconomy())
        {
            return false;
        }

        else if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null)
        {
            return false;
        }

        return true;
    }

    Economy econ;

    public boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        econ = (Economy)rsp.getProvider();

        if (econ != null)
        {
            return true;
        }

        return false;
    }



}

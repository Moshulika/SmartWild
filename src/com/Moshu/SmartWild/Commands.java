package com.Moshu.SmartWild;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {

        if(!(sender instanceof Player))
        {
            Utils.sendNotPlayer();
            return true;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("engine.wild"))
        {
            Utils.sendNoAccess(p);
            return true;
        }

        World world = p.getWorld();

        if(!world.getName().equals("world") && !world.getName().equals("smp"))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fTrebuie sa fii in lumea normala!"));
            return true;
        }

        if(Cooldown.hasCooldown(p.getUniqueId(), "wild") && !p.hasPermission("engine.admin"))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8(&c✖&8) &fMai ai de asteptat " + Cooldown.getRemainingTimeMinutes(p.getUniqueId(), "wild") + " minut(e)."));
            return true;
        }

        Wild.openMenu(p);

        return true;
    }

}

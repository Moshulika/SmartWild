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

        if(!p.hasPermission("smartwild.use"))
        {
            Utils.sendNoAccess(p);
            return true;
        }

        World world = p.getWorld();

        if(!Config.getEnabledWorlds().contains(world.getName()))
        {
            Utils.sendParsed(p, Utils.getLang("disabled-world"));
            return true;
        }

        if(Cooldown.hasCooldown(p.getUniqueId(), "wild") && !p.hasPermission("smartwild.admin"))
        {
            Utils.sendParsed(p, Utils.getLang("cooldown").replace("%time%", Integer.toString(Cooldown.getRemainingTimeMinutes(p.getUniqueId(), "wild"))));
            return true;
        }

        Wild.openMenu(p);

        return true;
    }

}

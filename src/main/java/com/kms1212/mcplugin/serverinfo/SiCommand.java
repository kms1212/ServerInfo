package com.kms1212.mcplugin.serverinfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SiCommand implements CommandExecutor, TabCompleter {
    private ServerInfo plugin;
    private List<String> list;

    public SiCommand(JavaPlugin plugin) {
        this.plugin = (ServerInfo)plugin;
        this.list = new ArrayList<>();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (list.isEmpty()) {
            list.add("help");
            list.add("reload");
            list.add("pause");
            list.add("resume");
        }
        List<String> ret = new ArrayList<>();
        if (args.length == 1) {
            for (String a : list) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    ret.add(a);
            }
            return ret;
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 0)
        {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.AQUA + "================== "
                        + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.AQUA + " ==================");
                sender.sendMessage(ChatColor.AQUA + "/" + cmd.getName() + " reload");
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] "
                        + ChatColor.YELLOW + "Reloading ServerInfo config...");
                plugin.onDisable();
                plugin.onEnable();
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] "
                        + ChatColor.AQUA + "Reloaded!");
                return true;
            } else if (args[0].equalsIgnoreCase("pause")) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] "
                        + ChatColor.BLUE + "Resource usage logging paused.");
                plugin.getTask().pause();
                return true;
            } else if (args[0].equalsIgnoreCase("resume")) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] "
                        + ChatColor.BLUE + "Resource usage logging resumed.");
                plugin.getTask().resume();
                return true;
            }
        }
        return false;
    }
}

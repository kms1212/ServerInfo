package com.kms1212.mcplugin.serverinfo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SiCommand implements CommandExecutor, TabCompleter {
    private ServerInfo plugin;

    public SiCommand(JavaPlugin plugin) {
        this.plugin = (ServerInfo)plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        list.clear();
        if (args.length == 1) {
            list.add("help");
            list.add("reload");
            list.add("pause");
            list.add("resume");
            list.add("config");
        } else if (args.length == 2) {
            Set<String> configSet = plugin.getConfig().getConfigurationSection("").getKeys(true);
            List<String> targetList = new ArrayList<>(configSet);

            for (int i = 0; i < targetList.size(); i++) {
                if (targetList.get(i).contains(".")) {
                    list.add(targetList.get(i));
                }
            }
        }

        List<String> ret = new ArrayList<>();
        if (args.length < 3) {
            for (String a : list) {
                if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
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
                plugin.getLogger().info("Resource usage logging paused.");
                plugin.getTask().pause();
                return true;
            } else if (args[0].equalsIgnoreCase("resume")) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] "
                        + ChatColor.BLUE + "Resource usage logging resumed.");
                plugin.getLogger().info("Resource usage logging resumed.");
                plugin.getTask().resume();
                return true;
            } else if (args[0].equalsIgnoreCase("config")) {
                boolean isArgValid = false;
                Set<String> configSet = plugin.getConfig().getConfigurationSection("").getKeys(true);
                List<String> targetList = new ArrayList<>(configSet);

                if (args.length == 3) {
                    for (int i = 0; i < targetList.size(); i++) {
                        if (targetList.get(i).contains(".")) {
                            isArgValid = args[1].equals(targetList.get(i));
                            if (isArgValid) {
                                break;
                            }
                        } else {
                            targetList.remove(i);
                        }
                    }

                    if (isArgValid) {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] " +
                            String.format("Value changed: %s > %s",
                                    plugin.getConfig().get(args[1]).toString(), args[2]));
                        plugin.getLogger().info(String.format("Value changed: %s > %s",
                                plugin.getConfig().get(args[1]).toString(), args[2]));

                        plugin.getConfig().set(args[1], Integer.parseInt(args[2]));
                        plugin.saveConfig();
                    }
                    return isArgValid;
                } else {
                    for (int i = 0; i < targetList.size(); i++) {
                        if (targetList.get(i).contains(".")) {
                            isArgValid = args[1].equals(targetList.get(i));
                            if (isArgValid) {
                                break;
                            }
                        } else {
                            targetList.remove(i);
                        }
                    }

                    if (isArgValid) {
                        sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Server" + ChatColor.YELLOW + "Info" + ChatColor.WHITE + "] " +
                                String.format("%s: %s", args[1], plugin.getConfig().get(args[1]).toString()));
                    }
                    return isArgValid;
                }
            }
        }
        return false;
    }
}

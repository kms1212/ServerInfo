package com.kms1212.mcplugin.serverinfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public final class ServerInfo extends JavaPlugin {
    private Logger logger;
    private Connection conn;
    private PreparedStatement stmt;
    private String host, port, database, username, password, url;
    private BukkitTask task;
    private GetData getData;

    public GetData getTask() {
        return getData;
    }

    public Connection getConn() {
        return conn;
    }

    public PreparedStatement getStmt() {
        return stmt;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        saveDefaultConfig();
        host = getConfig().getString("sql.host");
        port = getConfig().getString("sql.port");
        database = getConfig().getString("sql.database");
        username = getConfig().getString("sql.username");
        password = getConfig().getString("sql.password");

        url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        PluginCommand sicommand;
        SiCommand exec = new SiCommand(this);

        sicommand = this.getCommand("serverinfo");
        sicommand.setExecutor(exec);
        sicommand.setTabCompleter(exec);

        sicommand = this.getCommand("si");
        sicommand.setExecutor(new SiCommand(this));
        sicommand.setTabCompleter(new SiCommand(this));

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(ChatColor.RED + "JDBC Driver not found.");
            return;
        }
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(ChatColor.RED + "SQL connection error.");
            return;
        }
        logger.info(ChatColor.AQUA + "Successfully connected to SQL server.");
        logger.info(ChatColor.WHITE + "this >> " + url);

        try {
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ServerInfo ( DataIndex INT(10) NOT NULL, CPUUsage INT(3), RAMUsage INT(10), ErrorMessage TEXT(65535), ExceptionMessage TEXT(65535), PRIMARY KEY (DataIndex));");
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        getData = new GetData(this);

        task = getData.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            stmt = conn.prepareStatement("DROP TABLE ServerInfo;");
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (conn != null && !conn.isClosed()) {
                task.cancel();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.kms1212.mcplugin.serverinfo;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class GetData extends BukkitRunnable {
    private OperatingSystemMXBean osBean;
    private final ServerInfo plugin;
    private Connection conn;
    private PreparedStatement stmt;
    private Logger logger;
    private Runtime runtime;
    private boolean isPaused;

    public GetData(JavaPlugin plugin) {
        isPaused = false;
        this.plugin = (ServerInfo)plugin;
        conn = this.plugin.getConn();
        stmt = this.plugin.getStmt();
        this.logger = plugin.getLogger();
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    @Override
    public void run() {
        if (!isPaused) {
            runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576;
            int cpuUsage = (int)Math.round(osBean.getCpuLoad() * 100);
            int index = queryRow();

            try {
                stmt = conn.prepareStatement("INSERT INTO " + plugin.getTable() +" VALUE (" + String.format("%d, %d, %d, \"\", \"\"", index, cpuUsage, usedMemory) + ");");
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (index >= plugin.getConfig().getInt("datasize")) {
                try {
                    stmt = conn.prepareStatement("DELETE FROM " + plugin.getTable() +" WHERE DataIndex=" +
                            String.format("%d", (index - plugin.getConfig().getInt("datasize"))) + ";");
                    stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int queryRow() {
        int ret = 0;
        try {
            stmt = conn.prepareStatement("SELECT DataIndex FROM " + plugin.getTable() +";");
            ResultSet res = stmt.executeQuery();
            res.last();
            ret = res.getInt(1) + 1;
        } catch (Exception e) { }
        return ret;
    }
}

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
    private int dataCount;
    private int lastExec;

    public GetData(JavaPlugin plugin) {
        dataCount = 0;
        lastExec = 0;
        isPaused = false;
        this.plugin = (ServerInfo)plugin;
        conn = this.plugin.getConn();
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

            if ((usedMemory > plugin.getConfig().getInt("alert.ramUsageHigh") || cpuUsage > plugin.getConfig().getInt("alert.cpuUsageHigh")) && dataCount >= lastExec + plugin.getConfig().getInt("interval.alert") ||
                    dataCount >= lastExec + plugin.getConfig().getInt("interval.default")) {
                try {
                    stmt = conn.prepareStatement(String.format("INSERT INTO %s VALUE (%d, %d, %d, \"\", \"\");", plugin.getTableName(), dataCount, cpuUsage, usedMemory));
                    stmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    stmt = conn.prepareStatement(String.format("SELECT COUNT(*) FROM %s;", plugin.getTableName()));
                    ResultSet rs = stmt.executeQuery();
                    rs.first();
                    int rowCount = rs.getInt("COUNT(*)");

                    if (rowCount > plugin.getConfig().getInt("datasize")) {
                        stmt = conn.prepareStatement(String.format("DELETE FROM %s LIMIT %d;", plugin.getTableName(), 1));
                        stmt.executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lastExec = dataCount;
            }
        }
        dataCount++;
    }
}

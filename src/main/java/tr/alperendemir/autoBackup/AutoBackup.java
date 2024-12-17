package tr.alperendemir.autoBackup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.text.SimpleDateFormat;

public final class AutoBackup extends JavaPlugin {

    private int backupFrequency; // Frequency in seconds
    private int maxBackups; // Maximum backups to retain
    private String backupPath; // Backup folder path
    private List<String> worlds; // List of worlds to backup

    @Override
    public void onEnable() {
        // Load the configuration
        saveDefaultConfig();
        loadConfigValues();

        // Create backup directory if it doesn't exist
        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
            getLogger().info("Backup directory created at: " + backupDir.getAbsolutePath());
        }

        // Start the periodic backup task
        startBackupTask();

        getLogger().info("AutoBackup plugin enabled! Backing up every " + backupFrequency + " seconds.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoBackup plugin disabled!");
    }

    private void loadConfigValues() {
        backupFrequency = getConfig().getInt("backup-frequency", 3600);
        maxBackups = getConfig().getInt("max-backups", 5);
        backupPath = getConfig().getString("backup-path", "backups");
        worlds = getConfig().getStringList("worlds");
    }

    private void startBackupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                performBackup();
            }
        }.runTaskTimerAsynchronously(this, 0L, backupFrequency * 20L); // Convert seconds to ticks
    }

    private void performBackup() {
        getLogger().info("Starting backup process...");

        for (String worldName : worlds) {
            File worldDir = new File(worldName);
            if (worldDir.exists() && worldDir.isDirectory()) {
                String backupFileName = backupPath + "/" + worldName + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".zip";
                try {
                    zipDirectory(worldDir.toPath(), Paths.get(backupFileName));
                    getLogger().info("Backed up world: " + worldName + " to " + backupFileName);
                } catch (IOException e) {
                    getLogger().severe("Failed to backup world: " + worldName + " - " + e.getMessage());
                }
            } else {
                getLogger().warning("World directory not found: " + worldName);
            }
        }

        cleanupOldBackups();
        getLogger().info("Backup process completed.");
    }

    private void cleanupOldBackups() {
        File backupDir = new File(backupPath);
        if (!backupDir.exists() || !backupDir.isDirectory()) return;

        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
        if (backups == null) return;

        // Sort backups by last modified time
        Arrays.sort(backups, Comparator.comparingLong(File::lastModified));

        // Delete old backups if they exceed maxBackups
        int backupsToDelete = backups.length - maxBackups;
        for (int i = 0; i < backupsToDelete; i++) {
            if (backups[i].delete()) {
                getLogger().info("Deleted old backup: " + backups[i].getName());
            } else {
                getLogger().warning("Failed to delete old backup: " + backups[i].getName());
            }
        }
    }

    private void zipDirectory(Path sourceDir, Path zipFilePath) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
             Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(path -> !Files.isDirectory(path)) // Skip directories
                    .filter(path -> !path.getFileName().toString().equals("session.lock")) // Skip session.lock file
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            getLogger().severe("Error while zipping file: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }
}

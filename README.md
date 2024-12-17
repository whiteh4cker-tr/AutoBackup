# AutoBackup
 AutoBackup Spigot plugin - Schedule and perform automatic Minecraft server world backups

**Feature list:**

-   Automatic backup
-   Configurable backup frequency

-   Backup into specified directory
-   Selective backups of certain worlds

-   Automatically delete old world backups
-   Compress the data using the Deflate algorithm

**Example file structure:**
![alt text](https://i.imgur.com/k0dntKN.png)

**Requirements:**

-   Java 17 or higher
-   Spigot/Paper/forks MC v1.21.4

**Default config.yml:**
```
backup-frequency: 10800 # in seconds (e.g., 3600 seconds = 1 hour)
max-backups: 15 # maximum number of world backups to keep
backup-path: "backups" # relative path to the backup directory
worlds:
- world
- world_nether
- world_the_end
```

**Steps to install and set up:**

1.  Download the .jar file and place it within the plugins/ directory of your Minecraft server
2.  Restart the Minecraft server to generate the default configuration files
3.  Edit the configuration file to reflect your needs
# AutoBackup
 AutoBackup Spigot plugin - Schedule and perform automatic Minecraft server world backups

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/autobackup?style=flat&label=Modrinth%20Downloads)](https://modrinth.com/plugin/autobackup)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/121361?style=flat&label=Spigot%20Downloads)](https://www.spigotmc.org/resources/autobackup.121361/)
[![Hangar Views](https://img.shields.io/hangar/views/AutoBackup?style=flat&label=Hangar%20Views)](https://hangar.papermc.io/icecubetr/AutoBackup)
![GitHub License](https://img.shields.io/github/license/whiteh4cker-tr/AutoBackup)
[![CodeFactor](https://www.codefactor.io/repository/github/whiteh4cker-tr/autobackup/badge)](https://www.codefactor.io/repository/github/whiteh4cker-tr/autobackup)

<big>Supported Platforms</big><br>
[![spigot software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/compact-minimal/supported/spigot_vector.svg)](https://www.spigotmc.org/)
[![paper software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/supported/paper_vector.svg)](https://papermc.io/)
[![purpur software](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/supported/purpur_vector.svg)](https://purpurmc.org/)

**Feature list:**

-   Automatic backup
-   Dropbox support

-   FTPS (Implicit TLS) support
-   Configurable backup frequency

-   Backup into specified directory
-   Selective backups of certain worlds

-   Automatically delete old local world backups
-   Compress the data using the Deflate algorithm

**Example file structure:**
![alt text](https://i.imgur.com/k0dntKN.png)

**Requirements:**

-   Java 21 or higher
-   Spigot/Paper/forks MC v1.21.4

**Default config.yml:**
```
# Backup Configuration
backup-frequency: 10800 # in seconds (e.g., 3600 seconds = 1 hour)
max-backups: 15 # maximum number of world backups to keep
backup-path: "backups" # relative path to the backup directory
worlds:
  - world
  - world_nether
  - world_the_end

# FTP Backup Configuration
ftp:
  enabled: false # Set to true to enable FTP backups
  host: "ftp.example.com"
  port: 990 # Implicit TLS Port
  username: "your_username"
  password: "your_password"
  remote-path: "/backups"
  use-implicit-tls: true

# Dropbox Backup Configuration
dropbox:
  enabled: false # Set to true to enable Dropbox backups
  access-token: "your-dropbox-access-token"
  remote-path: "/backups"
```

**Steps to install and set up:**

1.  Download the .jar file and place it within the plugins/ directory of your Minecraft server
2.  Restart the Minecraft server to generate the default configuration files
3.  Edit the configuration file to reflect your needs

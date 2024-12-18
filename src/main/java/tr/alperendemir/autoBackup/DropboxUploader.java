package tr.alperendemir.autoBackup;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class DropboxUploader {
    private final DbxClientV2 client;
    private final Logger logger;
    private final String remotePath;

    public DropboxUploader(String accessToken, String remotePath, Logger logger) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("AutoBackupPlugin").build();
        this.client = new DbxClientV2(config, accessToken);
        this.logger = logger;
        this.remotePath = remotePath;
    }

    public boolean uploadBackups(List<String> backupFiles) {
        boolean allUploadsSuccessful = true;

        for (String localFilePath : backupFiles) {
            String dropboxFilePath = remotePath + "/" + new java.io.File(localFilePath).getName();
            try (InputStream in = new FileInputStream(localFilePath)) {
                logger.info("Uploading to Dropbox: " + localFilePath);
                FileMetadata metadata = client.files().uploadBuilder(dropboxFilePath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(in);
                logger.info("Uploaded to Dropbox: " + metadata.getPathLower());
            } catch (Exception e) {
                logger.severe("Error uploading to Dropbox: " + localFilePath + " - " + e.getMessage());
                allUploadsSuccessful = false;
            }
        }

        return allUploadsSuccessful;
    }
}

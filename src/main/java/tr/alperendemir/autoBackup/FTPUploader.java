package tr.alperendemir.autoBackup;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class FTPUploader {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String remotePath;
    private final boolean useImplicitTLS;
    private final Logger logger;

    public FTPUploader(String host, int port, String username, String password,
                       String remotePath, boolean useImplicitTLS, Logger logger) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.remotePath = remotePath;
        this.useImplicitTLS = useImplicitTLS;
        this.logger = logger;
    }

    public boolean uploadBackups(List<String> backupFiles) {
        if (backupFiles == null || backupFiles.isEmpty()) {
            logger.info("No backup files to upload.");
            return true;
        }

        FTPSClient ftpsClient = new FTPSClient(useImplicitTLS);
        boolean allUploadsSuccessful = true;

        try {
            ftpsClient.setConnectTimeout(30000); // Increase connection timeout to 30s
            ftpsClient.setBufferSize(1024 * 1024); // Set socket buffer size to 1MB
            ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

            connectToServer(ftpsClient);
            prepareRemoteDirectory(ftpsClient);

            for (String backupFile : backupFiles) {
                if (!uploadSingleFile(ftpsClient, backupFile)) {
                    allUploadsSuccessful = false;
                }
            }
        } catch (IOException e) {
            logger.severe("FTP upload error: " + e.getMessage());
            allUploadsSuccessful = false;
        } finally {
            disconnectFromServer(ftpsClient);
        }

        return allUploadsSuccessful;
    }

    private void connectToServer(FTPSClient ftpsClient) throws IOException {
        logger.info("Connecting to FTP server...");
        ftpsClient.connect(host, port);

        if (!FTPReply.isPositiveCompletion(ftpsClient.getReplyCode())) {
            throw new IOException("FTP server refused connection. Reply code: " + ftpsClient.getReplyCode());
        }

        if (!ftpsClient.login(username, password)) {
            throw new IOException("FTP login failed. Check username/password.");
        }

        ftpsClient.execPBSZ(0); // Protection buffer size
        ftpsClient.execPROT("P"); // Private data channel
        ftpsClient.setFileType(FTPSClient.BINARY_FILE_TYPE); // Set binary mode for non-text files
        ftpsClient.enterLocalPassiveMode(); // Default to passive mode
        logger.info("Connected to FTP server with implicit TLS: " + useImplicitTLS);
    }

    private boolean uploadSingleFile(FTPSClient ftpsClient, String localFilePath) {
        File localFile = new File(localFilePath);
        String remoteFileName = localFile.getName();

        try (FileInputStream fis = new FileInputStream(localFile)) {
            logger.info("Uploading file: " + localFilePath);
            if (ftpsClient.storeFile(remoteFileName, fis)) {
                logger.info("Uploaded file successfully: " + remoteFileName);
                return true;
            } else {
                logger.severe("Failed to upload file: " + remoteFileName);

                // Switch to active mode dynamically if passive mode fails
                logger.info("Switching to active mode...");
                ftpsClient.enterLocalActiveMode();
                if (ftpsClient.storeFile(remoteFileName, fis)) {
                    logger.info("Uploaded file successfully in active mode: " + remoteFileName);
                    return true;
                } else {
                    logger.severe("Active mode also failed for file: " + remoteFileName);
                }
            }
        } catch (IOException e) {
            logger.severe("Error uploading file: " + e.getMessage());
            return false;
        }
        return false;
    }


    private void prepareRemoteDirectory(FTPSClient ftpsClient) throws IOException {
        if (!ftpsClient.changeWorkingDirectory(remotePath)) {
            if (ftpsClient.makeDirectory(remotePath)) {
                logger.info("Created remote directory: " + remotePath);
                ftpsClient.changeWorkingDirectory(remotePath);
            } else {
                throw new IOException("Failed to create or change to remote directory: " + remotePath);
            }
        }
    }

    private void disconnectFromServer(FTPSClient ftpsClient) {
        try {
            if (ftpsClient.isConnected()) {
                ftpsClient.logout();
                ftpsClient.disconnect();
                logger.info("Disconnected from FTP server.");
            }
        } catch (IOException e) {
            logger.severe("Error closing FTP connection: " + e.getMessage());
        }
    }
}

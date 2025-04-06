package transactionmanagement;

import java.io.*;
import java.nio.file.*;

public class FileBackupUtility {

    public static void backupFile(File sourceFile, File backupFile) throws IOException {
        Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void restoreFile(File backupFile, File targetFile) throws IOException {
        try {
            Files.copy(backupFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error while restoring file: " + e.getMessage());
            throw e;
        }
    }

}

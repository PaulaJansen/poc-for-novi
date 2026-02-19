package nl.novi.endassignment.pocbackend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(Path uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String saveFile(MultipartFile file, String subfolder) throws IOException {
        Path folderPath = uploadDirectory.resolve(subfolder);
        Files.createDirectories(folderPath);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = folderPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return subfolder + "/" + filename;
    }

    public void deleteFile(String relativePath) throws IOException {
        Path path = uploadDirectory.resolve(relativePath);
        Files.deleteIfExists(path);
    }
}

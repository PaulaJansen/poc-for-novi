package nl.novi.endassignment.pocbackend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class StorageConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Bean
    public Path uploadDirectory() {
        try {
            Path path = Path.of(uploadDir).toAbsolutePath();
            Files.createDirectories(path);
            return path;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create upload directory", e);
        }
    }
}

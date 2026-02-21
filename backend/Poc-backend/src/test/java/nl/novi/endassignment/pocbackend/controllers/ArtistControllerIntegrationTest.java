package nl.novi.endassignment.pocbackend.controllers;

import jakarta.transaction.Transactional;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class ArtistControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist savedArtist;

    @BeforeEach
    void setUp() {
        Artist artist = new Artist();
        artist.setUsername("johnnydoee");
        artist.setPassword("Password@123");
        artist.setEmail("john@example.nl");
        artist.setFirstName("John");
        artist.setLastName("Doe");
        artist.setCity("Amsterdam");
        artist.setTypeOfArt("Schilderijen");
        artist.setBiography("Ik ben John en ik schilder.");
        artist.setRoles(new ArrayList<>());
        savedArtist = artistRepository.save(artist);
    }

    @Test
    @DisplayName("Should return artist by id")
    void testGetArtistById() throws Exception {

        mockMvc.perform(get("/artists/{id}", savedArtist.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johnnydoee"));
    }

    @Test
    @DisplayName("Should return 404 when artist not found")
    void testGetArtistById_NotFound() throws Exception {

        long nonExistentId = 999L;

        mockMvc.perform(get("/artists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a new artist")
    void testCreateArtist() throws Exception {

        MockMultipartFile profilePicture = new MockMultipartFile(
                "profilePictureFile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image".getBytes()
        );

        mockMvc.perform(multipart("/artists/register")
                        .file(profilePicture)
                        .param("username", "janiedoee")
                        .param("email", "jane@example.nl")
                        .param("password", "Password@456")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("biography", "Ik ben Jane en ik schilder.")
                        .param("city", "Amsterdam")
                        .param("typeOfArt", "Schilderijen"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("janiedoee"))
                .andExpect(jsonPath("$.email").value("jane@example.nl"))
                .andExpect(jsonPath("$.id").isNumber());
    }
}
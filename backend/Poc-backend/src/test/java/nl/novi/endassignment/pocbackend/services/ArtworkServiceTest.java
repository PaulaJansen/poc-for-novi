package nl.novi.endassignment.pocbackend.services;

import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.models.Artist;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Genre;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.GenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static nl.novi.endassignment.pocbackend.models.AvailabilityType.AVAILABLE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtworkServiceTest {

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private ArtworkMapper artworkMapper;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private GenreService genreService;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    ArtworkService artworkService;

    private Artwork artwork;
    private ArtworkInputDto artworkInputDto;
    private ArtworkResponseDto artworkDto;
    private Artist artist;
    private Path testUploadDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testUploadDirectory = Files.createTempDirectory("test-uploads");
        artworkInputDto = mock(ArtworkInputDto.class);
        artist = new Artist();
        artwork = new Artwork("Sunflowers", new BigDecimal("599.95"), AVAILABLE, artist, 100, 100, 2);
        artworkDto = new ArtworkResponseDto("Sunflowers", new BigDecimal("599.95"), "AVAILABLE", "John Doe", 100, 100, 2);
        artworkService = new ArtworkService(artworkRepository, artistRepository, genreService, artworkMapper, genreRepository, testUploadDirectory);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (testUploadDirectory != null && Files.exists(testUploadDirectory)) {
            try (var paths = Files.walk(testUploadDirectory)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.err.println("Could not delete file: " + file.getAbsolutePath());
                            }
                        });
            }
        }
    }

    @Test
    @DisplayName("Should create new artwork")
    public void test1() throws IOException {

        when(artworkInputDto.getGenreNames()).thenReturn(List.of("PAINTING"));
        when(artworkInputDto.getImages()).thenReturn(List.of(multipartFile));
        when(genreService.findOrCreate(any())).thenReturn(new Genre("PAINTING"));
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(artworkMapper.toEntity(any(), any())).thenReturn(artwork);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("John Doe");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(artistRepository.findByUsername(anyString())).thenReturn(Optional.of(artist));
        when(artworkMapper.toDto(artwork)).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.createArtwork(artworkInputDto);

        assertNotNull(result);
        assertEquals("Sunflowers", result.getTitle());
        assertEquals("AVAILABLE", result.getAvailability());
        assertEquals("John Doe", result.getArtistName());
        assertEquals(new BigDecimal("599.95"), result.getPrice());
        assertEquals(100, result.getWidthInCm());
        assertEquals(100, result.getLengthInCm());
        assertEquals(2, result.getHeightInCm());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toEntity(eq(artworkInputDto), any());
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should throw exception when file upload fails")
    public void test2() throws IOException {

        when(multipartFile.getInputStream()).thenThrow(new IOException("Er is iets mis"));
        when(artworkInputDto.getImages()).thenReturn(List.of(multipartFile));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> artworkService.createArtwork(artworkInputDto)
        );

        assertTrue(exception.getMessage().contains("Kan bestand niet opslaan"));

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when artist not found")
    public void test4() throws IOException {

        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(artworkMapper.toEntity(any(), any())).thenReturn(artwork);
        when(artworkInputDto.getImages()).thenReturn(List.of(multipartFile));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("John");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(artistRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.createArtwork(artworkInputDto)
        );

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all artworks")
    public void getAllArtworks() {

        List<Artwork> artworks = List.of(artwork);
        List<ArtworkResponseDto> dtos = List.of(artworkDto);

        when(artworkRepository.findAll()).thenReturn(artworks);
        when(artworkMapper.toDtoList(artworks)).thenReturn(dtos);

        List<ArtworkResponseDto> result = artworkService.getAllArtworks();

        assertThat(result).hasSize(1);
        assertEquals("Sunflowers", result.getFirst().getTitle());

        verify(artworkRepository).findAll();
        verify(artworkMapper).toDtoList(artworks);
    }

    @Test
    @DisplayName("Should return artwork by id")
    public void getArtworkById() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(artwork)).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.getArtworkById(1L);

        assertEquals("Sunflowers", result.getTitle());
        assertEquals("AVAILABLE", result.getAvailability());
        assertEquals("John Doe", result.getArtistName());
        assertEquals(new BigDecimal("599.95"), result.getPrice());
        assertEquals(100, result.getWidthInCm());
        assertEquals(100, result.getLengthInCm());
        assertEquals(2, result.getHeightInCm());

        verify(artworkRepository).findById(1L);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should throw exception when artwork with id... not found")
    public void test300() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.getArtworkById(1L));

        verify(artworkRepository).findById(1L);
        verifyNoInteractions(artworkMapper);
    }

    @Test
    @DisplayName("Should filter artworks")
    void filterArtworks() {
    }

    @Test
    @DisplayName("Should update artwork")
    void updateArtwork() {
    }

    @Test
    @DisplayName("Should delete artwork")
    void deleteArtwork() {
    }
}
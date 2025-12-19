package nl.novi.endassignment.pocbackend.services;

import jakarta.persistence.criteria.*;
import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.models.*;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
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
    private java.nio.file.Path testUploadDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testUploadDirectory = java.nio.file.Files.createTempDirectory("test-uploads");
        artworkInputDto = new ArtworkInputDto();
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
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("Could not delete: " + p);
                                e.printStackTrace();
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
    public void test3() throws IOException {

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
    public void test4() {

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
    public void test5() {

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
    public void test6() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.getArtworkById(1L));

        verify(artworkRepository).findById(1L);
        verifyNoInteractions(artworkMapper);
    }

    @Test
    @DisplayName("Should return artworks without filters")
    public void test7() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null, null, null, null, null);

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }


    @Test
    @DisplayName("Should return artworks with title filter")
    public void test8() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                "Sunflowers", null, null, null, null, null, null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should skip when title is empty")
    public void test9() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        " ", null, null, null, null, null, null);

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with first + last name filters")
    public void test10() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, "Vincent", "van Gogh", null, null, null, null
        );

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should skip when first + last name are empty")
    public void test11() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        null, " ", " ", null, null, null, null);

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with first name filter")
    public void test12() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, "Vincent", null, null, null, null, null
        );

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with last name filter")
    public void test13() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, "van Gogh", null, null, null, null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should execute artist specification")
    public void test14() {

        @SuppressWarnings("unchecked")
        Root<Artwork> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Join<Artwork, Artist> artistJoin = mock(Join.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate firstPredicate = mock(Predicate.class);
        Predicate lastPredicate = mock(Predicate.class);

        doReturn(artistJoin).when(root).join("artist");
        when(cb.conjunction()).thenReturn(basePredicate);
        when(cb.like(any(), anyString())).thenReturn(firstPredicate, lastPredicate);
        when(cb.and(any(), any())).thenReturn(firstPredicate, lastPredicate);

        Specification<Artwork> specNull = artworkService.buildArtistSpecification(null, null);
        Predicate resultNull = specNull.toPredicate(root, query, cb);
        assertNotNull(resultNull);

        Specification<Artwork> specFirst = artworkService.buildArtistSpecification("Vincent", null);
        Predicate resultFirst = specFirst.toPredicate(root, query, cb);
        assertNotNull(resultFirst);

        Specification<Artwork> specFirstEmpty = artworkService.buildArtistSpecification(" ", null);
        Predicate resultFirstEmpty = specFirstEmpty.toPredicate(root, query, cb);
        assertNotNull(resultFirstEmpty);

        Specification<Artwork> specLast = artworkService.buildArtistSpecification(null, "van Gogh");
        Predicate resultLast = specLast.toPredicate(root, query, cb);
        assertNotNull(resultLast);

        Specification<Artwork> specLastEmpty = artworkService.buildArtistSpecification(null, " ");
        Predicate resultLastEmpty = specLastEmpty.toPredicate(root, query, cb);
        assertNotNull(resultLastEmpty);

        Specification<Artwork> specBoth = artworkService.buildArtistSpecification("Vincent", "van Gogh");
        Predicate resultBoth = specBoth.toPredicate(root, query, cb);
        assertNotNull(resultBoth);

        Specification<Artwork> specBothEmpty = artworkService.buildArtistSpecification(" ", " ");
        Predicate resultBothEmpty = specBothEmpty.toPredicate(root, query, cb);
        assertNotNull(resultBothEmpty);

        verify(root, times(7)).join("artist");
        verify(cb, atLeast(7)).conjunction();
    }

    @Test
    @DisplayName("Should return artworks with min + max price filters")
    public void test15() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(600),
                null, null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with min price filter")
    public void test16() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null,
                BigDecimal.valueOf(100),
                null,
                null, null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with max price filter")
    public void test17() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null,
                null,
                BigDecimal.valueOf(600),
                null, null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should return artworks with genre filter")
    public void test18() {

        Genre genre = new Genre();

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));
        when(genreRepository.findByNameIgnoreCase("Modern")).thenReturn(Optional.of(genre));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null, null, null,
                List.of("Modern"),
                null
        );

        verify(genreRepository).findByNameIgnoreCase("Modern");
        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should throw exception when genre not found")
    public void test19() {

        when(genreRepository.findByNameIgnoreCase("Geen idee")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.filterArtworks(
                        null, null, null, null, null,
                        List.of("Geen idee"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Should skip when genre is empty")
    public void test20() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        null, null, null, null, null, List.of(), null);

        assertEquals(1, result.size());

        verify(genreRepository, never()).findByNameIgnoreCase(anyString());
        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should execute genre specification")
    public void test21() {

        @SuppressWarnings("unchecked")
        Root<Artwork> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        Join<Artwork, Genre> genreJoin = mock(Join.class);
        Predicate predicate = mock(Predicate.class);

        doReturn(genreJoin).when(root).join("genres");
        when(genreJoin.in(anyList())).thenReturn(predicate);

        List<Genre> genres = List.of(new Genre());
        Specification<Artwork> spec = artworkService.buildGenreSpecification(genres);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).join("genres");
        verify(genreJoin).in(genres);
    }

    @Test
    @DisplayName("Should return artworks with availability filter")
    public void test22() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null, null, null,
                null,
                List.of("available")
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should skip when availability is empty")
    public void test23() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        null, null, null, null, null, null, List.of());

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should execute availability specification")
    public void test24() {

        @SuppressWarnings("unchecked")
        Root<Artwork> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        @SuppressWarnings("unchecked")
        Path<AvailabilityType> availabilityPath = mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        doReturn(availabilityPath).when(root).get("availability");
        when(availabilityPath.in(anyList())).thenReturn(predicate);

        List<AvailabilityType> availabilities = List.of(AvailabilityType.AVAILABLE);
        Specification<Artwork> spec = artworkService.buildAvailabilitySpecification(availabilities);
        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).get("availability");
        verify(availabilityPath).in(availabilities);
    }

    @Test
    @DisplayName("Should throw exception when artwork not found")
    public void test25() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.updateArtwork(1L, artworkInputDto));

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update title of artwork")
    public void test26() {

        artworkInputDto.setTitle("New title");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setTitle(a.getTitle());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkInputDto);

        assertEquals("New title", artwork.getTitle());
        assertEquals("New title", result.getTitle());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should update price and dimensions of artwork")
    public void test27() {

        artworkInputDto.setPrice(BigDecimal.valueOf(500));
        artworkInputDto.setWidthInCm(50);
        artworkInputDto.setLengthInCm(100);
        artworkInputDto.setHeightInCm(25);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setPrice(a.getPrice());
            artworkResponseDto.setWidthInCm(a.getWidthInCm());
            artworkResponseDto.setLengthInCm(a.getLengthInCm());
            artworkResponseDto.setHeightInCm(a.getHeightInCm());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkInputDto);

        assertEquals(0, BigDecimal.valueOf(500).compareTo(artwork.getPrice()));
        assertEquals(50, artwork.getWidthInCm());
        assertEquals(100, artwork.getLengthInCm());
        assertEquals(25, artwork.getHeightInCm());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(result.getPrice()));
        assertEquals(50, result.getWidthInCm());
        assertEquals(100, result.getLengthInCm());
        assertEquals(25, result.getHeightInCm());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should update availability of artwork")
    public void test28() {

        artworkInputDto.setAvailability("SOLD");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setAvailability(a.getAvailability().name());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkInputDto);

        assertEquals(AvailabilityType.SOLD, artwork.getAvailability());
        assertEquals(AvailabilityType.SOLD, result.getAvailability());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should update genres of artwork")
    public void test29() {

        artworkInputDto.setGenreNames(List.of("Impressionisme"));

        Genre genre = new Genre();

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenReturn(artworkDto);
        when(genreService.findOrCreate(any())).thenReturn(genre);

        artworkService.updateArtwork(1L, artworkInputDto);

        assertTrue(artwork.getGenres().contains(genre));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
        verify(genreService).findOrCreate(any());
    }

    @Test
    @DisplayName("Should remove images if artwork")
    public void testUpdateArtwork_RemoveImages() {

        artwork.getImages().add("old.png");

        artworkInputDto.setRemoveImages(List.of("old.png"));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenReturn(artworkDto);

        artworkService.updateArtwork(1L, artworkInputDto);

        assertFalse(artwork.getImages().contains("old.png"));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should add images to artwork")
    public void testUpdateArtwork_AddImages() {

        MockMultipartFile file = new MockMultipartFile("file", "new.png",
                "image/png", "content".getBytes());

        artworkInputDto.setImages(List.of(file));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkMapper.toDto(any(Artwork.class))).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkInputDto);

        assertEquals(1, artwork.getImages().size());
        assertTrue(result.getImages().getFirst().contains("new.png"));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDto(artwork);
    }

    @Test
    @DisplayName("Should throw exception when images cannot be added")
    public void testUpdateArtwork_AddImages_IOException() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("slechte.png");
        when(file.getInputStream()).thenThrow(new IOException("Kan bestand niet lezen"));

        artworkInputDto.setImages(List.of(file));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> artworkService.updateArtwork(1L, artworkInputDto));
        assertTrue(ex.getMessage().contains("Kan bestand niet opslaan"));
    }

    @Test
    @DisplayName("Should delete artwork")
    public void deleteArtwork() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));

        String result = artworkService.deleteArtwork(1L);

        assertEquals("Kunstwerk met id 1 is verwijderd.", result);

        verify(artworkRepository).delete(artwork);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing artwork")
    public void test200() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.deleteArtwork(1L));

        verify(artworkRepository, never()).delete(any(Artwork.class));
    }
}

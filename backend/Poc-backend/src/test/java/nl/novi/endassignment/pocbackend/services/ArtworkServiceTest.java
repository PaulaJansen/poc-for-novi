package nl.novi.endassignment.pocbackend.services;

import jakarta.persistence.criteria.*;
import nl.novi.endassignment.pocbackend.dtos.ArtworkInputDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.ArtworkUpdateDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.models.*;
import nl.novi.endassignment.pocbackend.repositories.ArtistRepository;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.VisitorRepository;
import nl.novi.endassignment.pocbackend.security.ownership.ArtworkSecurity;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.*;

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
    private MultipartFile multipartFile;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ArtworkSecurity artworkSecurity;

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    ArtworkService artworkService;

    private Artwork artwork;
    private ArtworkInputDto artworkInputDto;
    private ArtworkUpdateDto artworkUpdateDto;
    private ArtworkResponseDto artworkDto;
    private Artist artist;

    @BeforeEach
    void setUp() throws IOException {
        artworkInputDto = new ArtworkInputDto();
        artworkUpdateDto = new ArtworkUpdateDto();
        artist = new Artist();
        artwork = new Artwork("Sunflowers", new BigDecimal("599.95"), AVAILABLE, artist, 100, 100, 2);
        artworkDto = new ArtworkResponseDto();
            artworkDto.setId(1L);
            artworkDto.setTitle("Sunflowers");
            artworkDto.setPrice(new BigDecimal("599.95"));
            artworkDto.setAvailability("AVAILABLE");
            artworkDto.setArtistId(1L);
            artworkDto.setArtistName("John Doe");
            artworkDto.setWidthInCm(100);
            artworkDto.setLengthInCm(100);
            artworkDto.setHeightInCm(2);
        artworkService = new ArtworkService(artworkRepository, artistRepository, genreService, artworkMapper, fileStorageService, artworkSecurity, visitorRepository);
    }

    @Test
    @DisplayName("Should create new artwork")
    public void test1() throws IOException {

        artworkInputDto.setGenreNames(List.of("PAINTING"));
        artworkInputDto.setImages(List.of(multipartFile));

        when(artworkMapper.toEntity(eq(artworkInputDto), any())).thenReturn(artwork);

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

        artworkInputDto.setImages(List.of(multipartFile));

        when(multipartFile.getOriginalFilename()).thenReturn("slechte.png");
        when(fileStorageService.saveFile(multipartFile, "artworks"))
                .thenThrow(new IOException("Er is iets mis"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> artworkService.createArtwork(artworkInputDto)
        );

        assertTrue(exception.getMessage().contains("Kan bestand niet opslaan"));
        assertTrue(exception.getMessage().contains("slechte.png"));

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should clean up uploaded files and throw exception if upload fails")
    public void test3() throws IOException {

        MultipartFile goodFile = mock(MultipartFile.class);
        when(goodFile.isEmpty()).thenReturn(false);
        when(goodFile.getOriginalFilename()).thenReturn("good.png");
        when(fileStorageService.saveFile(goodFile, "artworks")).thenReturn("good.png");

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.isEmpty()).thenReturn(false);
        lenient().when(badFile.getOriginalFilename()).thenReturn("bad.png");
        when(fileStorageService.saveFile(badFile, "artworks")).thenThrow(new IOException("Upload gefaald"));

        artworkInputDto.setImages(List.of(goodFile, badFile));

        lenient().when(artworkMapper.toEntity(any(), anyList())).thenCallRealMethod();
        lenient().when(artistRepository.findByUsername(anyString())).thenReturn(Optional.of(new Artist()));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> artworkService.createArtwork(artworkInputDto));

        assertTrue(e.getMessage().startsWith("Kan bestand niet opslaan"));

        verify(fileStorageService).saveFile(goodFile, "artworks");
        verify(fileStorageService).saveFile(badFile, "artworks");
        verify(fileStorageService).deleteFile("good.png");
        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should upload files during artwork creation")
    public void test4() throws IOException {

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("John Doe");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        MultipartFile goodFile = mock(MultipartFile.class);
        when(goodFile.isEmpty()).thenReturn(false);
        lenient().when(goodFile.getOriginalFilename()).thenReturn("good.png");
        when(fileStorageService.saveFile(goodFile, "artworks")).thenReturn("good.png");

        artworkInputDto.setImages(List.of(goodFile));

        when(artworkMapper.toEntity(any(), anyList())).thenReturn(artwork);
        when(artistRepository.findByUsername(anyString())).thenReturn(Optional.of(new Artist()));
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDto(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.createArtwork(artworkInputDto);

        assertNotNull(result);
        verify(fileStorageService).saveFile(goodFile, "artworks");
        verify(artworkRepository).save(artwork);
    }

    @Test
    @DisplayName("Should skip empty files during artwork creation")
    public void test5() throws IOException {

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("John Doe");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        artworkInputDto.setImages(List.of(emptyFile));

        when(artworkMapper.toEntity(any(), anyList())).thenReturn(artwork);
        when(artistRepository.findByUsername(anyString())).thenReturn(Optional.of(new Artist()));
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDto(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.createArtwork(artworkInputDto);

        assertNotNull(result);
        verify(fileStorageService, never()).saveFile(any(), any());
        verify(artworkRepository).save(artwork);
    }


    @Test
    @DisplayName("Should throw exception when artist not found")
    public void test6() {

        when(artistRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("John");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.createArtwork(artworkInputDto)
        );

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return all artworks")
    public void test7() {

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
    public void test8() {

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
    public void test9() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.getArtworkById(1L));

        verify(artworkRepository).findById(1L);
        verifyNoInteractions(artworkMapper);
    }

    @Test
    @DisplayName("Should return artworks for artist with id")
    public void test10() {

        long artistId = 1L;
        artwork.setId(10L);
        artworkDto.setId(10L);

        List<Artwork> artworks = List.of(artwork);
        List<ArtworkResponseDto> dtos = List.of(artworkDto);

        when(artworkRepository.findByArtistId(artistId)).thenReturn(artworks);
        when(artworkMapper.toDtoList(artworks)).thenReturn(dtos);

        List<ArtworkResponseDto> result = artworkService.getArtworksByArtistId(artistId);

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getId());

        verify(artworkRepository).findByArtistId(1L);
        verify(artworkMapper).toDtoList(artworks);
    }

    @Test
    @DisplayName("Should return empty list when artist has no artworks")
    public void test11() {

        long artistId = 2L;

        when(artworkRepository.findByArtistId(artistId)).thenReturn(List.of());
        when(artworkMapper.toDtoList(List.of())).thenReturn(List.of());

        List<ArtworkResponseDto> result = artworkService.getArtworksByArtistId(artistId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return artworks without filters")
    public void test12() {

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
    public void test13() {

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
    public void test14() {

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
    public void test15() {

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
    public void test16() {

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
    public void test17() {

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
    public void test18() {

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
    public void test19() {

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
    public void test20() {

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
    public void tes21() {

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
    public void test22() {

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
    @DisplayName("Should return artworks with partial genre match")
    public void test23() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null,
                null, null,
                List.of("imp"),
                null
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should not throw exception when genre does not exist")
    public void test24() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of());
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of());

        assertDoesNotThrow(() ->
                artworkService.filterArtworks(
                        null, null, null,
                        null, null,
                        List.of("bestaatniet"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Should skip genre filter when genre is empty")
    public void test25() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        null, null, null, null, null, List.of(), null);

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should skip genre filter when genre is blank")
    public void test26() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result =
                artworkService.filterArtworks(
                        null, null, null,
                        null, null,
                        List.of("   "),
                        null
                );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should build genre like specification")
    public void test27() {

        @SuppressWarnings("unchecked")
        Root<Artwork> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        @SuppressWarnings("unchecked")
        Join<Artwork, Genre> genreJoin = mock(Join.class);

        Path<String> namePath = mock(Path.class);
        Expression<String> lowerExpression = mock(Expression.class);
        Predicate predicate = mock(Predicate.class);

        doReturn(genreJoin).when(root).join("genres");
        doReturn(namePath).when(genreJoin).get("name");
        doReturn(lowerExpression).when(cb).lower(namePath);
        doReturn(predicate).when(cb).like(eq(lowerExpression), contains("imp"));
        doReturn(query).when(query).distinct(true);

        Specification<Artwork> spec = artworkService.buildGenreSpecification("imp");

        Predicate result = spec.toPredicate(root, query, cb);

        assertNotNull(result);

        verify(root).join("genres");
        verify(genreJoin).get("name");
        verify(cb).lower(namePath);
        verify(cb).like(eq(lowerExpression), eq("%imp%"));
        verify(query).distinct(true);
    }

    @Test
    @DisplayName("Should throw AssertionError when query is null")
    public void test28() {
        Root<Artwork> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Specification<Artwork> spec = artworkService.buildGenreSpecification("imp");

        assertThrows(AssertionError.class, () -> spec.toPredicate(root, null, cb));
    }

    @Test
    @DisplayName("Should return artworks with availability filter")
    public void test29() {

        when(artworkRepository.findAll(any(Specification.class))).thenReturn(List.of(artwork));
        when(artworkMapper.toDtoList(anyList())).thenReturn(List.of(artworkDto));

        List<ArtworkResponseDto> result = artworkService.filterArtworks(
                null, null, null, null, null,
                null,
                List.of(AvailabilityType.valueOf("AVAILABLE"))
        );

        assertEquals(1, result.size());

        verify(artworkRepository).findAll(any(Specification.class));
        verify(artworkMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should skip when availability is empty")
    public void test30() {

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
    public void test31() {

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
    public void test32() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.updateArtwork(1L, artworkUpdateDto, null));

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw Exception when user is not owner")
    public void test33() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(false);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> artworkService.updateArtwork(1L, artworkUpdateDto, null));

        assertEquals("Je bent geen eigenaar van dit kunstwerk", ex.getMessage());

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update title of artwork")
    public void test34() throws IOException {

        artworkUpdateDto.setTitle("New title");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setTitle(a.getTitle());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals("New title", artwork.getTitle());
        assertEquals("New title", result.getTitle());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should update price and dimensions of artwork")
    public void test35() throws IOException {

        artworkUpdateDto.setPrice(BigDecimal.valueOf(500));
        artworkUpdateDto.setWidthInCm(50);
        artworkUpdateDto.setLengthInCm(100);
        artworkUpdateDto.setHeightInCm(25);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setPrice(a.getPrice());
            artworkResponseDto.setWidthInCm(a.getWidthInCm());
            artworkResponseDto.setLengthInCm(a.getLengthInCm());
            artworkResponseDto.setHeightInCm(a.getHeightInCm());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(0, BigDecimal.valueOf(500).compareTo(artwork.getPrice()));
        assertEquals(50, artwork.getWidthInCm());
        assertEquals(100, artwork.getLengthInCm());
        assertEquals(25, artwork.getHeightInCm());
        assertEquals(0, BigDecimal.valueOf(500).compareTo(result.getPrice()));
        assertEquals(50, result.getWidthInCm());
        assertEquals(100, result.getLengthInCm());
        assertEquals(25, result.getHeightInCm());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should skip width update when widthInCm is null")
    public void test36() throws IOException {

        artworkUpdateDto.setWidthInCm(null);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(100, artwork.getWidthInCm());
    }

    @Test
    @DisplayName("Should skip width update when widthInCm is 0")
    public void test37() throws IOException {

        artworkUpdateDto.setWidthInCm(0);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(100, artwork.getWidthInCm());
    }

    @Test
    @DisplayName("Should skip length update when lengthInCm is null")
    public void test38() throws IOException {

        artworkUpdateDto.setLengthInCm(null);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(100, artwork.getLengthInCm());
    }

    @Test
    @DisplayName("Should skip length update when lengthInCm is 0")
    public void test39() throws IOException {

        artworkUpdateDto.setLengthInCm(0);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(100, artwork.getLengthInCm());
    }

    @Test
    @DisplayName("Should skip height update when heightInCm is null")
    public void test40() throws IOException {

        artworkUpdateDto.setHeightInCm(null);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(2, artwork.getHeightInCm());
    }

    @Test
    @DisplayName("Should skip height update when heightInCm is 0")
    public void test41() throws IOException {

        artworkUpdateDto.setHeightInCm(0);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(2, artwork.getHeightInCm());
    }

    @Test
    @DisplayName("Should update availability of artwork")
    public void test42() throws IOException {

        artworkUpdateDto.setAvailability("SOLD");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setAvailability(a.getAvailability().name());
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertEquals(AvailabilityType.SOLD, artwork.getAvailability());
        assertEquals("SOLD", result.getAvailability());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should update genres of artwork")
    public void test43() throws IOException {

        artworkUpdateDto.setGenreNames(List.of("Impressionisme"));

        Genre genre = new Genre();
        genre.setName("Impressionisme");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(genreService.findOrCreate(any())).thenReturn(genre);
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setGenreNames(a.getGenres()
                    .stream()
                    .map(Genre::getName)
                    .toList()
            );
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertTrue(artwork.getGenres().contains(genre));
        assertTrue(result.getGenreNames().contains("Impressionisme"));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
        verify(genreService).findOrCreate(any());
    }

    @Test
    @DisplayName("Should handle genreNames is null without error")
    public void test44() throws IOException {

        artworkUpdateDto.setGenreNames(null);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertNotNull(result);
        assertTrue(true);

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should handle genreNames is empty without error")
    public void test45() throws IOException {

        artworkUpdateDto.setGenreNames(List.of());

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertNotNull(result);
        assertTrue(artwork.getGenres().isEmpty());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should remove images of artwork")
    public void test46() throws IOException {

        artwork.getImages().add("old.png");

        artworkUpdateDto.setRemoveImages(List.of("old.png"));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setImages(new ArrayList<>(a.getImages()));
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertFalse(artwork.getImages().contains("old.png"));
        assertFalse(result.getImages().contains("old.png"));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should not remove image if it is not in removeImages list")
    public void test47() throws IOException {

        artwork.getImages().add("keep.png");
        artworkUpdateDto.setRemoveImages(List.of("other.png"));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertTrue(artwork.getImages().contains("keep.png"));
    }

    @Test
    @DisplayName("Should swallow exception when deleting image fails")
    public void test48() throws IOException {

        artwork.getImages().add("old.png");
        artworkUpdateDto.setRemoveImages(List.of("old.png"));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);

        doThrow(new IOException("simulated error")).when(fileStorageService).deleteFile("old.png");

        when(artworkMapper.toDtoForEdit(artwork)).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertFalse(artwork.getImages().contains("old.png"));
        assertEquals(artworkDto, result);

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should skip removeImages if removeImages is null")
    public void test49() throws IOException {

        artwork.setImages(new ArrayList<>(List.of("img.png")));
        artworkUpdateDto.setRemoveImages(null);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertTrue(artwork.getImages().contains("img.png"));
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    @DisplayName("Should skip removeImages if removeImages is empty")
    public void test50() throws IOException {

        artwork.setImages(new ArrayList<>(List.of("img.png")));
        artworkUpdateDto.setRemoveImages(new ArrayList<>());

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertTrue(artwork.getImages().contains("img.png"));
        verify(fileStorageService, never()).deleteFile(any());
    }

    @Test
    @DisplayName("Should add images to artwork")
    public void test51() throws IOException {

        MockMultipartFile file = new MockMultipartFile("file", "new.png",
                "image/png", "content".getBytes());

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(fileStorageService.saveFile(file, "artworks")).thenReturn("uploads/new.png");
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenAnswer(invocation -> {
            Artwork a = invocation.getArgument(0);
            ArtworkResponseDto artworkResponseDto = new ArtworkResponseDto();
            artworkResponseDto.setImages(new ArrayList<>(a.getImages()));
            return artworkResponseDto;
        });

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, List.of(file));

        assertTrue(artwork.getImages().contains("uploads/new.png"));
        assertTrue(result.getImages().contains("uploads/new.png"));

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
        verify(fileStorageService).saveFile(file, "artworks");
    }

    @Test
    @DisplayName("Should throw exception when images cannot be added")
    public void test52() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("slechte.png");
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(fileStorageService.saveFile(file, "artworks"))
                .thenThrow(new IOException("Kan bestand niet lezen"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> artworkService.updateArtwork(1L, artworkUpdateDto, List.of(file)));

        assertTrue(ex.getMessage().contains("slechte.png"));
        assertTrue((ex.getMessage().startsWith("Kan bestand niet opslaan")));

        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle images is null without error")
    public void test53() throws IOException {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertNotNull(result);
        assertTrue(result.getImages() == null || result.getImages().isEmpty());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should handle images is empty without error")
    public void test54() throws IOException {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkRepository.save(artwork)).thenReturn(artwork);
        when(artworkSecurity.isOwner(any(Artwork.class))).thenReturn(true);
        when(artworkMapper.toDtoForEdit(any(Artwork.class))).thenReturn(artworkDto);

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, null);

        assertNotNull(result);
        assertTrue(result.getImages() == null || result.getImages().isEmpty());

        verify(artworkRepository).save(artwork);
        verify(artworkMapper).toDtoForEdit(artwork);
    }

    @Test
    @DisplayName("Should skip empty files when updating artwork")
    public void test55() throws IOException {

        artwork.setImages(new ArrayList<>());
        artworkUpdateDto.setGenreNames(List.of("Abstract"));

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        List<MultipartFile> images = List.of(emptyFile);

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner((Artwork) any())).thenReturn(true);
        when(artworkRepository.save(any())).thenReturn(artwork);
        when(artworkMapper.toDtoForEdit(any())).thenReturn(new ArtworkResponseDto());

        ArtworkResponseDto result = artworkService.updateArtwork(1L, artworkUpdateDto, images);

        assertTrue(artwork.getImages().isEmpty());
        verify(fileStorageService, never()).saveFile(any(), any());
    }

    @Test
    @DisplayName("Should clean up uploaded files and throw exception when adding new images fails")
    public void test56() throws IOException {

        artwork.setId(1L);
        artwork.setImages(new ArrayList<>());

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(artworkSecurity.isOwner(artwork)).thenReturn(true);

        MultipartFile goodFile = mock(MultipartFile.class);
        when(goodFile.isEmpty()).thenReturn(false);
        lenient().when(goodFile.getOriginalFilename()).thenReturn("good.png");
        when(fileStorageService.saveFile(goodFile, "artworks")).thenReturn("good.png");

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.isEmpty()).thenReturn(false);
        when(badFile.getOriginalFilename()).thenReturn("bad.png");
        when(fileStorageService.saveFile(badFile, "artworks")).thenThrow(new IOException("Kan bestand niet lezen"));

        List<MultipartFile> images = List.of(goodFile, badFile);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> artworkService.updateArtwork(1L, artworkUpdateDto, images));

        assertTrue(e.getMessage().contains("bad.png"));
        assertTrue(e.getMessage().startsWith("Kan bestand niet opslaan"));

        verify(fileStorageService).saveFile(goodFile, "artworks");
        verify(fileStorageService).saveFile(badFile, "artworks");
        verify(fileStorageService).deleteFile("good.png");
        verify(artworkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete artwork")
    public void test57() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
        when(visitorRepository.findAll()).thenReturn(Collections.emptyList());

        String result = artworkService.deleteArtwork(1L);

        assertEquals("Kunstwerk met id 1 is verwijderd.", result);

        verify(artworkRepository).delete(artwork);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing artwork")
    public void test58() {

        when(artworkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> artworkService.deleteArtwork(1L));

        verify(artworkRepository, never()).delete(any(Artwork.class));
    }
}

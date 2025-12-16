package nl.novi.endassignment.pocbackend.services;

import nl.novi.endassignment.pocbackend.dtos.ArtworkResponseDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorInputDto;
import nl.novi.endassignment.pocbackend.dtos.VisitorResponseDto;
import nl.novi.endassignment.pocbackend.exceptions.RecordNotFoundException;
import nl.novi.endassignment.pocbackend.mappers.ArtworkMapper;
import nl.novi.endassignment.pocbackend.mappers.VisitorMapper;
import nl.novi.endassignment.pocbackend.models.Artwork;
import nl.novi.endassignment.pocbackend.models.Role;
import nl.novi.endassignment.pocbackend.models.RoleType;
import nl.novi.endassignment.pocbackend.models.Visitor;
import nl.novi.endassignment.pocbackend.repositories.ArtworkRepository;
import nl.novi.endassignment.pocbackend.repositories.RoleRepository;
import nl.novi.endassignment.pocbackend.repositories.VisitorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private VisitorMapper visitorMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    ArtworkRepository artworkRepository;

    @Mock
    ArtworkMapper artworkMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    VisitorService visitorService;

    private Visitor visitor;
    private VisitorResponseDto visitorDto;
    private Path testUploadDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testUploadDirectory = Files.createTempDirectory("test-uploads");
        visitor = new Visitor("John", "john@test.nl", "Password@123", "John Doe");
        visitor.setFavorites(new ArrayList<>());
        visitorDto = new VisitorResponseDto("John", "john@test.nl", "John Doe");
        visitorService = new VisitorService(visitorRepository, visitorMapper, roleRepository, artworkRepository, artworkMapper, passwordEncoder, testUploadDirectory);
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
    @DisplayName("Should return all visitors")
    public void test1() {

        List<Visitor> visitors = List.of(visitor);
        List<VisitorResponseDto> dtos = List.of(visitorDto);

        when(visitorRepository.findAll()).thenReturn(visitors);
        when(visitorMapper.toDtoList(visitors)).thenReturn(dtos);

        List<VisitorResponseDto> result = visitorService.getAllVisitors();

        assertThat(result).hasSize(1);
        assertEquals("John", result.getFirst().getUsername());

        verify(visitorRepository).findAll();
        verify(visitorMapper).toDtoList(visitors);
    }

    @Test
    @DisplayName("Should return visitor by id")
    public void test2() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        VisitorResponseDto result = visitorService.getVisitorById(1L);

        assertEquals("John", result.getUsername());
        assertEquals("john@test.nl", result.getEmail());
        assertEquals("John Doe", result.getName());

        verify(visitorRepository).findById(1L);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should throw exception when visitor with id... not found")
    void test3() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.getVisitorById(1L));

        verify(visitorRepository).findById(1L);
        verifyNoInteractions(visitorMapper);
    }

    @Test
    @DisplayName("Should return visitor by name")
    public void test4() {

        List<Visitor> visitors = List.of(visitor);
        List<VisitorResponseDto> dtos = List.of(visitorDto);

        when(visitorRepository.findByName("John Doe")).thenReturn(visitors);
        when(visitorMapper.toDtoList(visitors)).thenReturn(dtos);

        List<VisitorResponseDto> result = visitorService.getVisitorByName("John Doe");

        assertThat(result).hasSize(1);
        assertEquals("John Doe", result.getFirst().getName());

        verify(visitorRepository).findByName("John Doe");
        verify(visitorMapper).toDtoList(visitors);
    }

    @Test
    @DisplayName("Should create new visitor")
    public void test5() throws IOException {

        MockMultipartFile profilePicture = new MockMultipartFile(
                "profilePictureFile",
                "profile.png",
                "image/png",
                "mock content".getBytes()
        );

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setUsername("John");
        visitorInputDto.setEmail("john@test.nl");
        visitorInputDto.setPassword("Password@123");
        visitorInputDto.setProfilePictureFile(profilePicture);
        visitorInputDto.setName("John Doe");

        Visitor savedVisitor = new Visitor("John", "john@test.nl", "encodedPassword", "John Doe");
        savedVisitor.setRoles(new ArrayList<>());
        savedVisitor.setProfilePicture("profilepicture.png");

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword");
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRepository.findByRoleName(RoleType.VISITOR)).thenReturn(Optional.of(new Role(RoleType.VISITOR)));
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto =
                    new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setProfilePicture(v.getProfilePicture());
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.createVisitor(visitorInputDto);

        assertNotNull(result);
        assertEquals("John", result.getUsername());
        assertEquals("john@test.nl", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertNotNull(result.getProfilePicture());
        assertTrue(result.getProfilePicture().startsWith("/uploads/"));

        String fileName = result.getProfilePicture().replace("/uploads/", "");
        Path savedFilePath = testUploadDirectory.resolve(fileName);
        assertTrue(Files.exists(savedFilePath), "Profile picture should exist in test directory");

        assertFalse(visitor.getRoles().isEmpty());
        assertEquals(RoleType.VISITOR, visitor.getRoles().getFirst().getRoleName());

        verify(visitorRepository).save(visitor);
        verify(passwordEncoder).encode("Password@123");
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    public void test6() {

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> visitorService.createVisitor(visitorInputDto));
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    public void test7() {

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword("");

        assertThrows(IllegalArgumentException.class, () -> visitorService.createVisitor(visitorInputDto));
    }

    @Test
    @DisplayName("Should not add role if visitor already exists")
    public void test8() throws IOException {

        visitor.setRoles(List.of(new Role(RoleType.VISITOR)));

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword("Password@123");

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(any())).thenReturn(visitorDto);

        visitorService.createVisitor(visitorInputDto);

        verify(roleRepository, never()).findByRoleName(any());
    }

    @Test
    @DisplayName("Should not add role if visitor already has roles")
    public void test9() throws IOException {

        visitor.setRoles(List.of(new Role(RoleType.VISITOR)));

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword("Password@123");

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        visitorService.createVisitor(visitorInputDto);

        verify(roleRepository, never()).findByRoleName(any());
    }

    @Test
    @DisplayName("Should initialize roles list when null")
    public void test10() throws IOException {

        visitor.setRoles(null);

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword("Password@123");

        Role visitorRole = new Role(RoleType.VISITOR);
        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);
        when(roleRepository.findByRoleName(RoleType.VISITOR)).thenReturn(Optional.of(visitorRole));

        visitorService.createVisitor(visitorInputDto);

        assertNotNull(visitor.getRoles());
        assertEquals(1, visitor.getRoles().size());
        assertEquals(RoleType.VISITOR, visitor.getRoles().getFirst().getRoleName());
    }

    @Test
    @DisplayName("Should not create visitor without profile picture")
    public void test11() throws IOException {

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setPassword("Password@123");

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(any())).thenReturn(visitorDto);

        VisitorResponseDto result = visitorService.createVisitor(visitorInputDto);

        assertNull(result.getProfilePicture());
    }

    @Test
    @DisplayName("Should create visitor without profile picture")
    public void test12() throws IOException {

        visitor.setRoles(new ArrayList<>());

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setUsername("John");
        visitorInputDto.setEmail("john@test.nl");
        visitorInputDto.setPassword("Password@123");
        visitorInputDto.setName("John Doe");
        visitorInputDto.setProfilePictureFile(null);

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);
        when(roleRepository.findByRoleName(RoleType.VISITOR)).thenReturn(Optional.of(new Role(RoleType.VISITOR)));

        VisitorResponseDto result = visitorService.createVisitor(visitorInputDto);

        assertNotNull(result);
        assertNull(visitor.getProfilePicture());
    }

    @Test
    @DisplayName("Should create visitor without profile picture (False)")
    void test13() throws IOException {

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setUsername("John");
        visitorInputDto.setEmail("john@test.nl");
        visitorInputDto.setPassword("Password@123");
        visitorInputDto.setName("John Doe");

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        visitorInputDto.setProfilePictureFile(emptyFile);

        visitor.setRoles(new ArrayList<>());

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setProfilePicture(v.getProfilePicture());
            return visitorResponseDto;
        });
        when(roleRepository.findByRoleName(RoleType.VISITOR)).thenReturn(Optional.of(new Role(RoleType.VISITOR)));

        VisitorResponseDto result = visitorService.createVisitor(visitorInputDto);

        assertNotNull(result);
        assertEquals("John", result.getUsername());
        assertEquals("john@test.nl", result.getEmail());
        assertEquals("John Doe", result.getName());
        assertNull(result.getProfilePicture(), "Profile picture should remain null/empty");
        assertFalse(visitor.getRoles().isEmpty());
        assertEquals(RoleType.VISITOR, visitor.getRoles().getFirst().getRoleName());
    }

    @Test
    @DisplayName("Should update visitor")
    public void test14() throws Exception {

        long visitorId = 1L;

        visitor.setProfilePicture("oldpicture.png");
        Path oldFilePath = testUploadDirectory.resolve("oldpicture.png");
        Files.writeString(oldFilePath, "dummy content");

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setUsername("Jane");
        visitorInputDto.setEmail("jane@test.nl");
        visitorInputDto.setName("Jane Doe");
        visitorInputDto.setProfilePicture("newpicture.png");

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            return new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
        });

        VisitorResponseDto result = visitorService.updateVisitor(visitorId, visitorInputDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@test.nl", result.getEmail());
        assertEquals("Jane", result.getUsername());
        assertEquals("newpicture.png", visitor.getProfilePicture());

        assertFalse(Files.exists(oldFilePath));

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing visitor")
    public void test15() {

        VisitorInputDto inputDto = new VisitorInputDto();

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.updateVisitor(1L, inputDto));
    }

    @Test
    @DisplayName("Should update visitor when no old profile picture exists")
    public void test16() {

        visitor.setProfilePicture(null);

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setProfilePicture("newpicture.png");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        visitorService.updateVisitor(1L, visitorInputDto);

        assertEquals("newpicture.png", visitor.getProfilePicture());
    }

    @Test
    @DisplayName("Should handle exception when deleting old profile picture")
    public void test17() {

        visitor.setProfilePicture("oldpicture.png");

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setProfilePicture("newpicture.png");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.deleteIfExists(any(Path.class)))
                    .thenThrow(new IOException("Test IO exception"));

            VisitorResponseDto result = visitorService.updateVisitor(1L, visitorInputDto);

            assertEquals("newpicture.png", visitor.getProfilePicture());
            assertNotNull(result);

            filesMock.verify(() -> Files.deleteIfExists(any(Path.class)));
        }
    }

    @Test
    @DisplayName("Should update visitor without changing profile picture")
    public void test18() {

        visitor.setProfilePicture("existingpicture.png");

        VisitorInputDto visitorInputDto = new VisitorInputDto();
        visitorInputDto.setUsername("Jane");
        visitorInputDto.setEmail("jane@test.nl");
        visitorInputDto.setName("Jane Doe");
        visitorInputDto.setProfilePicture(null);

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setProfilePicture(v.getProfilePicture());
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.updateVisitor(1L, visitorInputDto);

        assertEquals("existingpicture.png", visitor.getProfilePicture());
        assertEquals("existingpicture.png", result.getProfilePicture());
        assertEquals("Jane", visitor.getUsername());
        assertEquals("Jane Doe", visitor.getName());
        assertEquals("jane@test.nl", result.getEmail());
    }

    @Test
    @DisplayName("Should return all favorites for visitor")
    public void test19() {

        Artwork artwork1 = new Artwork();
        artwork1.setId(1L);
        artwork1.setTitle("The best artwork");

        Artwork artwork2 = new Artwork();
        artwork2.setId(2L);
        artwork2.setTitle("The bestest artwork");

        visitor.setFavorites(List.of(artwork1, artwork2));

        ArtworkResponseDto dto1 = new ArtworkResponseDto();
        dto1.setTitle("The best artwork");

        ArtworkResponseDto dto2 = new ArtworkResponseDto();
        dto2.setTitle("The bestest artwork");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(artworkMapper.toDtoList(visitor.getFavorites())).thenReturn(List.of(dto1, dto2));

        List<ArtworkResponseDto> result = visitorService.getFavorites(1L);

        assertEquals(2, result.size());
        assertEquals("The best artwork", result.get(0).getTitle());
        assertEquals("The bestest artwork", result.get(1).getTitle());

        verify(artworkMapper).toDtoList(visitor.getFavorites());
    }

    @Test
    @DisplayName("Should return empty list when visitor has no favorites")
    public void test20() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(artworkMapper.toDtoList(visitor.getFavorites())).thenReturn(List.of());

        List<ArtworkResponseDto> result = visitorService.getFavorites(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(artworkMapper).toDtoList(visitor.getFavorites());
    }

    @Test
    @DisplayName("Should throw exception when visitor not found")
    public void test21() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.getFavorites(1L)
        );

        verifyNoInteractions(artworkMapper);
    }

    @Test
    @DisplayName("Should add artwork to favorites of visitor")
    public void test22() {

        long visitorId = 1L;
        long artworkId = 10L;

        Artwork artwork = new Artwork();
        artwork.setId(artworkId);
        artwork.setTitle("The best artwork");

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(artworkId)).thenReturn(Optional.of(artwork));
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setFavoritesTitles(
                    v.getFavorites()
                            .stream()
                            .map(Artwork::getTitle)
                            .toList()
            );
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.addFavorites(visitorId, artworkId);

        assertEquals(1, visitor.getFavorites().size());
        assertTrue(visitor.getFavorites().contains(artwork));
        assertEquals(List.of("The best artwork"), result.getFavoritesTitles());

        verify(visitorRepository).save(visitor);
    }

    @Test
    @DisplayName("Should not add artwork to favorites if artwork is already in favorites")
    public void test23() {

        long visitorId = 1L;
        long artworkId = 10L;

        Artwork artwork = new Artwork();
        artwork.setId(artworkId);
        artwork.setTitle("The best artwork");

        visitor.getFavorites().add(artwork);

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(artworkId)).thenReturn(Optional.of(artwork));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        visitorService.addFavorites(visitorId, artworkId);

        assertEquals(1, visitor.getFavorites().size());

        verify(visitorRepository).save(visitor);
    }

    @Test
    @DisplayName("Should throw exception when adding favorite to non-existing visitor")
    public void test24() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.addFavorites(1L, 10L)
        );
    }

    @Test
    @DisplayName("Should throw exception when adding non-existing artwork to favorites")
    public void test25() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.addFavorites(1L, 10L)
        );
    }

    @Test
    @DisplayName("Should remove artwork from favorites")
    public void test26() {

        long visitorId = 1L;
        long artworkId = 10L;

        Artwork artwork = new Artwork();
        artwork.setId(artworkId);
        artwork.setTitle("The best artwork");

        visitor.getFavorites().add(artwork);

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(artworkId)).thenReturn(Optional.of(artwork));
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setFavoritesTitles(List.of());
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.removeFavorites(visitorId, artworkId);

        assertTrue(visitor.getFavorites().isEmpty());
        assertTrue(result.getFavoritesTitles().isEmpty());

        verify(visitorRepository).save(visitor);
    }

    @Test
    @DisplayName("Should throw exception when removing favorite from non-existing visitor")
    public void test27() {

        long visitorId = 1L;
        long artworkId = 10L;

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.removeFavorites(visitorId, artworkId)
        );

        verify(visitorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when removing non-existing artwork from favorites")
    public void test28() {

        long visitorId = 1L;
        long artworkId = 10L;

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(artworkId)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.removeFavorites(visitorId, artworkId)
        );

        verify(visitorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete visitor")
    public void test29() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));

        String result = visitorService.deleteVisitor(1L);

        assertEquals("Bezoeker met id 1 is verwijderd.", result);

        verify(visitorRepository).delete(visitor);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing visitor")
    public void test30() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.deleteVisitor(1L));

        verify(visitorRepository, never()).delete(any());
    }
}
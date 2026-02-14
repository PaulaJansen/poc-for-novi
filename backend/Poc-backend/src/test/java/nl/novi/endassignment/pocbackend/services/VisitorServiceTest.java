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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    VisitorService visitorService;

    private Visitor visitor;
    private VisitorResponseDto visitorDto;
    private VisitorInputDto visitorInputDto;
    private Path testUploadDirectory;

    @BeforeEach
    void setUp() throws IOException {
        testUploadDirectory = Files.createTempDirectory("test-uploads");
        visitor = new Visitor("John", "john@test.nl", "Password@123", "John Doe");
        visitor.setFavorites(new ArrayList<>());
        visitorInputDto = new VisitorInputDto();
        visitorDto = new VisitorResponseDto("John", "john@test.nl", "John Doe");
        visitorService = new VisitorService(visitorRepository, visitorMapper, roleRepository, artworkRepository, artworkMapper, passwordEncoder, fileStorageService);
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

        visitorInputDto.setUsername("John");
        visitorInputDto.setEmail("john@test.nl");
        visitorInputDto.setPassword("Password@123");
        visitorInputDto.setProfilePictureFile(profilePicture);
        visitorInputDto.setName("John Doe");

        when(visitorMapper.toEntity(visitorInputDto)).thenReturn(visitor);
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(RoleType.VISITOR)).thenReturn(Optional.of(new Role(RoleType.VISITOR)));
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fileStorageService.saveFile(any(MultipartFile.class), eq("profile"))).thenReturn("profile.png");
        when(visitorMapper.toDto(visitor)).thenAnswer(invocation -> {
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
        assertFalse(visitor.getRoles().isEmpty());
        assertEquals(RoleType.VISITOR, visitor.getRoles().getFirst().getRoleName());

        verify(visitorRepository).save(visitor);
        verify(passwordEncoder).encode("Password@123");
        verify(fileStorageService).saveFile(profilePicture, "profile");
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    public void test6() {

        visitorInputDto.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> visitorService.createVisitor(visitorInputDto));
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    public void test7() {

        visitorInputDto.setPassword("");

        assertThrows(IllegalArgumentException.class, () -> visitorService.createVisitor(visitorInputDto));
    }

    @Test
    @DisplayName("Should not add role if visitor already exists")
    public void test8() throws IOException {

        visitor.setRoles(List.of(new Role(RoleType.VISITOR)));
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
    @DisplayName("Should create visitor without profile picture (false)")
    void test13() throws IOException {

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

        MockMultipartFile newProfilePicture = new MockMultipartFile(
                "profilePictureFile",
                "newpicture.png",
                "image/png",
                "dummy content".getBytes()
        );

        visitorInputDto.setUsername("Jane");
        visitorInputDto.setEmail("jane@test.nl");
        visitorInputDto.setName("Jane Doe");
        visitorInputDto.setProfilePictureFile(newProfilePicture);
        visitorInputDto.setProfilePicture("newpicture.png");

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenAnswer(invocation -> invocation.getArgument(0));
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setProfilePicture(v.getProfilePicture());
            return visitorResponseDto;
        });

        when(fileStorageService.saveFile(newProfilePicture, "profile")).thenReturn("newpicture.png");
        doNothing().when(fileStorageService).deleteFile("oldpicture.png");

        VisitorResponseDto result = visitorService.updateVisitor(visitorId, visitorInputDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@test.nl", result.getEmail());
        assertEquals("Jane", result.getUsername());
        assertEquals("newpicture.png", visitor.getProfilePicture());

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
        verify(fileStorageService).deleteFile("oldpicture.png");
        verify(fileStorageService).saveFile(newProfilePicture, "profile");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing visitor")
    public void test15() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.updateVisitor(1L, visitorInputDto));
    }

    @Test
    @DisplayName("Should update visitor when no old profile picture exists")
    public void test16() throws IOException {

        visitor.setProfilePicture(null);

        MockMultipartFile newProfilePicture = new MockMultipartFile(
                "profilePictureFile",
                "newpicture.png",
                "image/png",
                "dummy content".getBytes()
        );
        visitorInputDto.setProfilePictureFile(newProfilePicture);

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(any(Visitor.class))).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(v.getUsername(), v.getEmail(), v.getName());
            visitorResponseDto.setProfilePicture(v.getProfilePicture() != null ? "/uploads/" + v.getProfilePicture() : null);
            return visitorResponseDto;
        });

        when(fileStorageService.saveFile(newProfilePicture, "profile")).thenReturn("newpicture.png");

        VisitorResponseDto result = visitorService.updateVisitor(1L, visitorInputDto);

        assertNotNull(result);
        assertEquals("newpicture.png", visitor.getProfilePicture());
        assertEquals("/uploads/newpicture.png", result.getProfilePicture());

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
        verify(fileStorageService).saveFile(newProfilePicture, "profile");
    }

    @Test
    @DisplayName("Should handle exception when deleting old profile picture")
    public void test17() throws IOException {

        visitor.setProfilePicture("oldpicture.png");
        MockMultipartFile newProfilePicture = new MockMultipartFile(
                "profilePictureFile",
                "newpicture.png",
                "image/png",
                "dummy content".getBytes()
        );
        visitorInputDto.setProfilePictureFile(newProfilePicture);


        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        when(fileStorageService.saveFile(newProfilePicture, "profile")).thenReturn("newpicture.png");
        doThrow(new IOException("Test IO exception"))
                .when(fileStorageService).deleteFile("oldpicture.png");

        VisitorResponseDto result = visitorService.updateVisitor(1L, visitorInputDto);

        assertEquals("newpicture.png", visitor.getProfilePicture());
        assertNotNull(result);

        verify(fileStorageService).deleteFile("oldpicture.png");
        verify(fileStorageService).saveFile(newProfilePicture, "profile");
        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }


    @Test
    @DisplayName("Should update visitor without changing profile picture")
    public void test18() throws IOException {

        visitor.setProfilePicture("existingpicture.png");
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
        assertEquals("/uploads/existingpicture.png", result.getProfilePicture());
        assertEquals("Jane", visitor.getUsername());
        assertEquals("Jane Doe", visitor.getName());
        assertEquals("jane@test.nl", result.getEmail());

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should update visitor without profile picture file")
    void test19() throws IOException {

        visitor.setProfilePicture("existing.png");
        visitorInputDto.setProfilePictureFile(null);

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        visitorService.updateVisitor(1L, visitorInputDto);

        assertEquals("existing.png", visitor.getProfilePicture());
        verify(fileStorageService, never()).saveFile(any(), any());
    }

    @Test
    @DisplayName("Should not update profile picture when file is empty")
    void test20() throws IOException {

        visitor.setProfilePicture("existing.png");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "profilePictureFile",
                "empty.png",
                "image/png",
                new byte[0]
        );

        visitorInputDto.setProfilePictureFile(emptyFile);

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenReturn(visitorDto);

        visitorService.updateVisitor(1L, visitorInputDto);

        assertEquals("existing.png", visitor.getProfilePicture());

        verify(fileStorageService, never()).saveFile(any(), any());
        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should not set profilePicture in DTO when savedVisitor profilePicture is null")
    void test21() throws IOException {

        long visitorId = 1L;

        visitor.setProfilePicture(null);

        visitorInputDto.setUsername("Jane");
        visitorInputDto.setEmail("jane@test.nl");
        visitorInputDto.setName("Jane Doe");

        when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toDto(visitor)).thenAnswer(invocation -> {
            Visitor v = invocation.getArgument(0);
            VisitorResponseDto visitorResponseDto = new VisitorResponseDto(
                    v.getUsername(),
                    v.getEmail(),
                    v.getName()
            );
            visitorResponseDto.setProfilePicture(null);
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.updateVisitor(visitorId, visitorInputDto);

        assertNull(result.getProfilePicture());

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should return all favorites for visitor")
    public void test22() {

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
    public void test23() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(artworkMapper.toDtoList(visitor.getFavorites())).thenReturn(List.of());

        List<ArtworkResponseDto> result = visitorService.getFavorites(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(artworkMapper).toDtoList(visitor.getFavorites());
    }

    @Test
    @DisplayName("Should throw exception when visitor not found")
    public void test24() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.getFavorites(1L)
        );

        verifyNoInteractions(artworkMapper);
    }

    @Test
    @DisplayName("Should add artwork to favorites of visitor")
    public void test25() {

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
            visitorResponseDto.setFavoritesIds(
                    v.getFavorites()
                            .stream()
                            .map(Artwork::getId)
                            .toList()
            );
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.addFavorites(visitorId, artworkId);

        assertEquals(1, visitor.getFavorites().size());
        assertTrue(visitor.getFavorites().contains(artwork));
        assertEquals(List.of(artworkId), result.getFavoritesIds());

        verify(visitorRepository).save(visitor);
    }

    @Test
    @DisplayName("Should not add artwork to favorites if artwork is already in favorites")
    public void test26() {

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
    public void test27() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.addFavorites(1L, 10L)
        );
    }

    @Test
    @DisplayName("Should throw exception when adding non-existing artwork to favorites")
    public void test28() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(artworkRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.addFavorites(1L, 10L)
        );
    }

    @Test
    @DisplayName("Should remove artwork from favorites")
    public void test29() {

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
            List<Long> favoriteIds = v.getFavorites()
                    .stream()
                    .map(Artwork::getId)
                    .toList();
            visitorResponseDto.setFavoritesIds(favoriteIds);
            return visitorResponseDto;
        });

        VisitorResponseDto result = visitorService.removeFavorites(visitorId, artworkId);

        assertTrue(visitor.getFavorites().isEmpty());
        assertEquals(List.of(), result.getFavoritesIds());

        verify(visitorRepository).save(visitor);
        verify(visitorMapper).toDto(visitor);
    }

    @Test
    @DisplayName("Should throw exception when removing favorite from non-existing visitor")
    public void test30() {

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
    public void test31() {

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
    public void test32() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));

        String result = visitorService.deleteVisitor(1L);

        assertEquals("Bezoeker met id 1 is verwijderd.", result);

        verify(visitorRepository).delete(visitor);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing visitor")
    public void test33() {

        when(visitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> visitorService.deleteVisitor(1L));

        verify(visitorRepository, never()).delete(any());
    }
}
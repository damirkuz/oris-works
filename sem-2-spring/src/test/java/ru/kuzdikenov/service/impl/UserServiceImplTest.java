package ru.kuzdikenov.service.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kuzdikenov.config.properties.MailProperties;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.Role;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.RoleRepository;
import ru.kuzdikenov.repository.UserJpaRepository;
import ru.kuzdikenov.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MailProperties mailProperties = new MailProperties(
                "NoReply",
                "noreply@example.com",
                "Verify account",
                "Hello, $name. Open $url",
                "http://localhost:8080"
        );
        userService = new UserServiceImpl(
                userRepository,
                userJpaRepository,
                roleRepository,
                passwordEncoder,
                mailProperties,
                mailSender
        );
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        given(passwordEncoder.encode("parol")).willReturn("encoded");
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        userService.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(mailSender).send(mimeMessage);

        User savedUser = userCaptor.getValue();
        assertEquals("Damir", savedUser.getUsername());
        assertEquals("encoded", savedUser.getPassword());
        assertEquals("damir@example.com", savedUser.getMail());
        assertNotNull(savedUser.getVerificationCode());
        assertFalse(savedUser.isVerified());

        assertEquals("Verify account", mimeMessage.getSubject());
        assertEquals("damir@example.com", mimeMessage.getAllRecipients()[0].toString());
        String content = mimeMessage.getContent().toString();
        assertTrue(content.contains("Damir"));
        assertTrue(content.contains("/verification?code="));
    }

    @Test
    void testVerifyUser() {
        User user = User.builder().verificationCode("code").verified(false).build();
        given(userRepository.findByVerificationCode("code")).willReturn(Optional.of(user));

        userService.verifyUser("code");

        assertTrue(user.isVerified());
        assertNull(user.getVerificationCode());
    }

    @Test
    void testVerifyUserThrowsWhenCodeMissing() {
        given(userRepository.findByVerificationCode("code")).willReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.verifyUser("code")
        );

        assertEquals("verification код не существует", exception.getMessage());
    }

    @Test
    void testGetUser() {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        User user = User.builder().username("Damir").build();
        given(userRepository.findByUsername("Damir")).willReturn(Optional.of(user));

        User actual = userService.getUser(dto);

        assertSame(user, actual);
    }

    @Test
    void testGetUsers() {
        given(userRepository.findAll()).willReturn(List.of(
                User.builder().username("Damir").build(),
                User.builder().username("Ivan").build()
        ));

        List<UserDto> actual = userService.getUsers();

        assertEquals(List.of(new UserDto("Damir"), new UserDto("Ivan")), actual);
    }

    @Test
    void testRegisterNewUser() {
        Role role = Role.builder().name("USER").build();
        given(userJpaRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));
        given(passwordEncoder.encode("parol")).willReturn("encoded");

        userService.registerNewUser("Damir", "parol");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userJpaRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("Damir", savedUser.getUsername());
        assertEquals("encoded", savedUser.getPassword());
        assertEquals(List.of(role), savedUser.getRoles());
    }

    @Test
    void testRegisterNewUserThrowsWhenUserExists() {
        given(userJpaRepository.findByUsername("Damir")).willReturn(Optional.of(User.builder().username("Damir").build()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerNewUser("Damir", "parol")
        );

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userJpaRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterNewUserThrowsWhenRoleMissing() {
        given(userJpaRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.registerNewUser("Damir", "parol")
        );

        assertEquals("Роль USER не найдена в БД", exception.getMessage());
        verify(userJpaRepository, never()).save(any(User.class));
    }
}

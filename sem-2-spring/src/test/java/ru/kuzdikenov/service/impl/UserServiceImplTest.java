package ru.kuzdikenov.service.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kuzdikenov.config.properties.MailProperties;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.Role;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.RoleRepository;
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
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    private UserServiceImpl userService;
    private MailProperties verificationEnabledMailProperties;
    private MailProperties verificationDisabledMailProperties;

    @BeforeEach
    void setUp() {
        verificationEnabledMailProperties = new MailProperties(
                "NoReply",
                "noreply@example.com",
                "Verify account",
                "Hello, $name. Open $url",
                "http://localhost:8080",
                true
        );
        verificationDisabledMailProperties = new MailProperties(
                "NoReply",
                "noreply@example.com",
                "Verify account",
                "Hello, $name. Open $url",
                "http://localhost:8080",
                false
        );
        userService = new UserServiceImpl(
                userRepository,
                roleRepository,
                passwordEncoder,
                verificationEnabledMailProperties,
                mailSender
        );
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        Role role = Role.builder().name("USER").build();
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(userRepository.findByMail("damir@example.com")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));
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
        assertEquals(List.of(role), savedUser.getRoles());

        assertEquals("Verify account", mimeMessage.getSubject());
        assertEquals("damir@example.com", mimeMessage.getAllRecipients()[0].toString());
        String content = mimeMessage.getContent().toString();
        assertTrue(content.contains("Damir"));
        assertTrue(content.contains("/verification?code="));
    }

    @Test
    void testCreateUserWithoutVerification() {
        UserServiceImpl userServiceWithoutVerification = new UserServiceImpl(
                userRepository,
                roleRepository,
                passwordEncoder,
                verificationDisabledMailProperties,
                mailSender
        );
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        Role role = Role.builder().name("USER").build();
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(userRepository.findByMail("damir@example.com")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));
        given(passwordEncoder.encode("parol")).willReturn("encoded");

        userServiceWithoutVerification.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("Damir", savedUser.getUsername());
        assertEquals("encoded", savedUser.getPassword());
        assertEquals("damir@example.com", savedUser.getMail());
        assertTrue(savedUser.isVerified());
        assertNull(savedUser.getVerificationCode());
        assertEquals(List.of(role), savedUser.getRoles());
        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testCreateUserContinuesWhenMailAuthFails() {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        Role role = Role.builder().name("USER").build();
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(userRepository.findByMail("damir@example.com")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.of(role));
        given(passwordEncoder.encode("parol")).willReturn("encoded");
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        org.mockito.Mockito.doThrow(new MailAuthenticationException("bad creds"))
                .when(mailSender).send(mimeMessage);

        userService.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        verify(mailSender).send(mimeMessage);
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isVerified());
        assertNull(savedUser.getVerificationCode());
    }

    @Test
    void testCreateUserThrowsWhenUsernameExists() {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        given(userRepository.findByUsername("Damir"))
                .willReturn(Optional.of(User.builder().username("Damir").build()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(dto)
        );

        assertEquals("Пользователь с таким именем уже существует", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testCreateUserThrowsWhenMailExists() {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(userRepository.findByMail("damir@example.com"))
                .willReturn(Optional.of(User.builder().mail("damir@example.com").build()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(dto)
        );

        assertEquals("Пользователь с такой почтой уже существует", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void testCreateUserThrowsWhenRoleMissing() {
        CreateUserDto dto = new CreateUserDto("Damir", "parol", "damir@example.com");
        given(userRepository.findByUsername("Damir")).willReturn(Optional.empty());
        given(userRepository.findByMail("damir@example.com")).willReturn(Optional.empty());
        given(roleRepository.findByName("USER")).willReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.createUser(dto)
        );

        assertEquals("Роль USER не найдена в БД", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
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
}

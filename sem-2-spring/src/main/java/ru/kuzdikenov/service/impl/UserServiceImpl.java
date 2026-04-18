package ru.kuzdikenov.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzdikenov.aop.Benchmarkable;
import ru.kuzdikenov.aop.Metricable;
import ru.kuzdikenov.config.properties.MailProperties;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.dto.UserDto;
import ru.kuzdikenov.model.Role;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.RoleRepository;
import ru.kuzdikenov.repository.UserRepository;
import ru.kuzdikenov.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    @Metricable
    @Benchmarkable
    public void createUser(CreateUserDto createUserDto) {
        if (userRepository.findByUsername(createUserDto.username()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByMail(createUserDto.mail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена в БД"));

        boolean verificationEnabled = mailProperties.verificationEnabled();
        String verificationCode = verificationEnabled ? UUID.randomUUID().toString() : null;
        User user = User.builder()
                .username(createUserDto.username())
                .password(passwordEncoder.encode(createUserDto.password()))
                .mail(createUserDto.mail())
                .verificationCode(verificationCode)
                .verified(!verificationEnabled)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);

        if (verificationEnabled && !sendVerificationMail(createUserDto, verificationCode)) {
            user.setVerificationCode(null);
            user.setVerified(true);
        }
    }

    private boolean sendVerificationMail(CreateUserDto createUserDto, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = mailProperties.content();
        try {
            mimeMessageHelper.setFrom(mailProperties.from(), mailProperties.sender());
            mimeMessageHelper.setTo(createUserDto.mail());
            mimeMessageHelper.setSubject(mailProperties.subject());

            content = content.replace("$name", createUserDto.username());
            content = content.replace("$url", mailProperties.baseUrl() +
                    "/verification?code=" + verificationCode);

            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.warn("Verification email is unavailable, registration will continue without verification for user {}", createUserDto.username(), e);
            return false;
        }
    }

    @Transactional
    public void verifyUser(String code) {
        userRepository.findByVerificationCode(code)
                .ifPresentOrElse(user -> {
                            user.setVerified(true);
                            user.setVerificationCode(null);
                        },
                        () -> {
                            throw new IllegalArgumentException("verification код не существует");
                        });
    }

//
//    @Transactional(readOnly = true)
//    public List<User> findAll() {
//        return userRepository.findAll();

    /// /        return userRepositoryHiber.findAll();
//    }
//
    @Transactional
    public User getUser(CreateUserDto createUserDto) {
        return userRepository.findByUsername(createUserDto.username()).get();
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(user.getUsername())).toList();
    }
}

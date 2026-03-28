package ru.kuzdikenov.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzdikenov.config.properties.MailProperties;
import ru.kuzdikenov.dto.CreateUserDto;
import ru.kuzdikenov.model.Role;
import ru.kuzdikenov.model.User;
import ru.kuzdikenov.repository.RoleRepository;
import ru.kuzdikenov.repository.UserJpaRepository;
import ru.kuzdikenov.repository.UserRepository;
import ru.kuzdikenov.service.UserService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserJpaRepository userJpaRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;

    @Override
    public void createUser(CreateUserDto createUserDto) {
        String verificationCode = UUID.randomUUID().toString();
        User user = User.builder()
                .username(createUserDto.username())
                .password(passwordEncoder.encode(createUserDto.password()))
                .mail(createUserDto.mail())
                .verificationCode(verificationCode)
                .build();
        userRepository.save(user);

        sendVerificationMail(createUserDto, verificationCode);
    }

    private void sendVerificationMail(CreateUserDto createUserDto, String verificationCode) {
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
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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
//
//    @Transactional
//    public void deleteUser(CreateUserDto userWithUsernameDto) {
//        userRepository.deleteByUsername(userWithUsernameDto.username());
//    }
//
//    @Transactional
//    public void updateUser(UserWithIdAndUsernameDto userWithIdAndUsernameDto) {
//        Optional<User> user = userRepository.findById(userWithIdAndUsernameDto.id());
//        if (user.isPresent()) {
//            User user1 = user.get();
//            user1.setUsername(userWithIdAndUsernameDto.username());
//            userRepository.save(user1);
//        }
//    }

    @Transactional
    public void registerNewUser(String username, String rawPassword) {
        if (userJpaRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена в БД"));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles(List.of(userRole))
                .build();

        userJpaRepository.save(user);
    }
}

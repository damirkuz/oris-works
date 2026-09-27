# Kuzdikenov_400_oris_works — учебные работы по курсу ОРИС (ИТИС КФУ)

Коллекция семестровых работ и домашних заданий по дисциплине «Основы профессиональной деятельности» (группа 11-400): от самописного HTTP-клиента до Spring Boot с безопасностью и AOP.

## Описание

Монорепозиторий отражает прогресс обучения веб-разработке на Java: каждая папка — отдельное занятие с даты. Использованы чистые сервлеты, FreeMarker, JDBC, сокеты, JavaFX и в конце — Spring Boot. Модули независимы, у каждого свой сборочный файл.

## Модули

| Папка | Тема | Что внутри |
|---|---|---|
| `2025-09-06-HttpModule` | HTTP | Самописный HTTP-клиент (`HttpClient`, `ConnectionOperations`) на сокетах, GET/POST |
| `2025-09-20-ServletTomcat` | Servlets | Первые сервлеты (Hello, Login, SignUp) под Tomcat 10, JSP/HTML, `web.xml` |
| `2025-10-01-Freemarker-Filters` | Servlets | FreeMarker-шаблоны, фильтры: аутентификация, логирование |
| `2025-10-04-Jdbc` | JDBC | DAO-слой, PostgreSQL, `DatabaseConnectionUtil`, те же фильтры |
| `2025-10-11-ajax` | AJAX | AJAX-валидация логина, загрузка файлов, Cloudinary, DTO, обработка исключений |
| `2025-11-15-protocols` | Сети | Multi-module: TCP/UDP-серверы (greeting), JavaFX-чат; web-модуль с сервлетами |
| `2025-11-29-javafx` | JavaFX | Чат-приложение с командами: `/weather` (OpenWeather API), `/exchange` (курсы валют) |
| `sem-2-spring` | Spring Boot | Заметки (notes): Spring Security, JPA/Hibernate, Liquibase, AOP, FreeMarker, подтверждение e-mail, JaCoCo |

## Технологии

- Java 16–24 (зависит от модуля), Maven / Gradle (Kotlin DSL)
- Servlet API, Tomcat, FreeMarker, Jackson
- PostgreSQL, JDBC, Hibernate/JPA, Liquibase
- Spring Boot 3.4.4 (Web, Data JPA, Security, AOP, Mail), Lombok
- JavaFX 21, сокеты (TCP/UDP), java.net.http
- JUnit 5, JaCoCo (порог покрытия 50% в sem-2-spring)

## Запуск

Каждый модуль собирается отдельно из своей папки.

Spring Boot (sem-2-spring) — нужны PostgreSQL и `.env` по образцу `.env.example`:

```bash
cd sem-2-spring
cp .env.example .env   # заполнить APP_DB_* и APP_MAIL_*
./gradlew bootRun      # http://localhost:8080
```

Сервлетные модули (пример):

```bash
cd 2025-10-11-ajax
mvn clean package      # WAR -> развернуть в Tomcat
```

Сокетный модуль:

```bash
cd 2025-11-15-protocols/net
mvn compile exec:java -Dexec.mainClass=ru.kuzdikenov.udp.GreetingServer
```

## Структура проекта

```
2025-09-06-HttpModule/    — Maven, пакет ru.kpfu.itis.kuzdikenov.http
2025-09-20-ServletTomcat/ — webapp, servlet/, http/
2025-10-01-Freemarker-Filters/ — servlet/, filter/, http/, WEB-INF
2025-10-04-Jdbc/          — + dao/, entity/, util/ (JDBC)
2025-10-11-ajax/          — + dto/, exceptions/, util/ (Cloudinary, FormParser)
2025-11-15-protocols/     — net/ (tcp, udp, fx.chat) + web/
2025-11-29-javafx/        — fx.chat: view/, service/, parse/, model/
sem-2-spring/             — controller/, service/, repository/, aop/, config/, db/changelog
```

Примечание: это учебные работы — часть кода намеренно упрощена (например, `CredentialsLoggingFilter` демонстрирует перехват параметров фильтром и пишет логин/пароль в лог; MD5-хеширование паролей).

import java.util.*

plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
}

group = "ru.kuzdikenov"
version = "1.0-SNAPSHOT"

//val springVersion: String by project
//val jakartaVersion: String by project
//val hibernateVersion: String by project
val postgresVersion: String by project
val freemarkerVersion: String by project
//val hikariVersion: String by project
//val springDataVersion: String by project
val lombokVersion: String by project
//val jacksonVersion: String by project
val springSecurityVersion: String by project

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")

    implementation("org.liquibase:liquibase-core:4.33.0")

//    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("info.picocli:picocli:4.6.3")
    liquibaseRuntime("org.postgresql:postgresql:${postgresVersion}")
}


//application {
//    mainClass = "ru.kuzdikenov.Main"
//}

tasks.test {
    useJUnitPlatform()
}

val props = Properties()
props.load(file("src/main/resources/db/liquibase.properties").inputStream())

liquibase {
    activities.register("generate") {
        arguments = mapOf(
            "changelogFile" to props.getProperty("change-log-file"),
            "url" to props.getProperty("url"),
            "username" to props.getProperty("username"),
            "password" to props.getProperty("password"),
            "driver" to props.getProperty("driver"),
            "defaultSchemaName" to props.getProperty("defaultSchemaName"),
            "schemas" to props.getProperty("defaultSchemaName")
        )
    }
}

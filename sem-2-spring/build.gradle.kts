import java.util.*

plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
    id("jacoco")
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
    implementation("org.springframework.boot:spring-boot-starter-aop")
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

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")


}


//application {
//    mainClass = "ru.kuzdikenov.Main"
//}


val props = Properties()
file("src/main/resources/db/liquibase.properties").reader(Charsets.UTF_8).use(props::load)

val envProps = Properties()
val envFile = file(".env")
if (envFile.exists()) {
    envFile.reader(Charsets.UTF_8).use(envProps::load)
}

fun resolveLiquibaseProperty(propertyKey: String, envKey: String): String? {
    return envProps.getProperty(envKey)
        ?: System.getenv(envKey)
        ?: props.getProperty(propertyKey)?.takeIf { it.isNotBlank() }
}

liquibase {
    activities.register("generate") {
        val defaultSchemaName = resolveLiquibaseProperty("defaultSchemaName", "LIQUIBASE_SCHEMA")
        val liquibaseArguments = mutableMapOf<String, Any>(
            "changelogFile" to props.getProperty("change-log-file"),
            "driver" to props.getProperty("driver")
        )
        resolveLiquibaseProperty("url", "LIQUIBASE_URL")?.let { liquibaseArguments["url"] = it }
        resolveLiquibaseProperty("username", "LIQUIBASE_USERNAME")?.let { liquibaseArguments["username"] = it }
        resolveLiquibaseProperty("password", "LIQUIBASE_PASSWORD")?.let { liquibaseArguments["password"] = it }
        defaultSchemaName?.let {
            liquibaseArguments["defaultSchemaName"] = it
            liquibaseArguments["schemas"] = it
        }
        arguments = liquibaseArguments
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.5)
            }
        }
    }
}

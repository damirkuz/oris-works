plugins {
    id("java")
//    id("application")
    id("war")
}

group = "ru.kuzdikenov"
version = "1.0-SNAPSHOT"

val springVersion: String by project
val jakartaVersion: String by project
val hibernateVersion: String by project
val postgresVersion: String by project
val freemarkerVersion: String by project
val hikariVersion: String by project
val springDataVersion: String by project
val lombokVersion: String by project
val jacksonVersion: String by project
val springSecurityVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-webmvc:$springVersion")
    implementation("org.springframework:spring-jdbc:$springVersion")
    implementation("org.springframework:spring-orm:$springVersion")
    implementation("org.springframework:spring-context-support:$springVersion")
//    implementation("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")

    providedCompile("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.freemarker:freemarker:$freemarkerVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.springframework.data:spring-data-jpa:${springDataVersion}")
    implementation("org.springframework.security:spring-security-core:${springSecurityVersion}")
    implementation("org.springframework.security:spring-security-web:${springSecurityVersion}")
    implementation("org.springframework.security:spring-security-config:${springSecurityVersion}")
    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")

    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

//    testImplementation(platform("org.junit:junit-bom:5.10.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
}


//application {
//    mainClass = "ru.kuzdikenov.Main"
//}

tasks.test {
    useJUnitPlatform()
}
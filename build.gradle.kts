plugins {
    id("java")
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "udemy.clone"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.4.2")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.0.6")

    /**
     * ElasticSearch
     */
    implementation("co.elastic.clients:elasticsearch-java:9.0.1")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.4")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.4")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:5.3.4")


    /**
     * Amazon S3
     */
    implementation("io.minio:minio:8.5.17")


    /**
     * Utils & Logging
     */
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    /**
     * Kafka
     */
    implementation("org.springframework.kafka:spring-kafka")
}

tasks.test {
    useJUnitPlatform()
}
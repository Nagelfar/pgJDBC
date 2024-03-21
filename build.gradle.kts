plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.postgresql:postgresql:42.7.2")
    compileOnly("org.jetbrains:annotations:24.1.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.postgresql:postgresql:42.7.2")
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")

    testCompileOnly("org.jetbrains:annotations:24.1.0")
    testImplementation("org.slf4j:slf4j-jdk14:2.0.12")

}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}
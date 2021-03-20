import java.net.URL

plugins {
    id("maven-publish")
    id("signing")
    id("jacoco")
    id("org.jetbrains.kotlin.jvm") version ("1.4.31")
    id("org.jetbrains.dokka") version ("1.4.30")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.31")
    implementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("pl.droidsonroids.yaml:snakeyaml:1.18-android")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.mockito:mockito-core:3.8.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.5.5")
}

jacoco {
    toolVersion = "0.8.6"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = true
    }
}

group = property("POM_GROUP") as String
version = property("POM_VERSION") as String

tasks.dokkaJavadoc {
    outputDirectory.set(tasks.javadoc.get().destinationDir)
    dokkaSourceSets {
        configureEach {
            includes.from(files("extra.md"))
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL(property("POM_URL") as String))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

tasks.named("javadoc") {
    dependsOn("dokkaJavadoc")
}

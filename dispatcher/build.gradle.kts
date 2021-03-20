plugins {
    jacoco
    id("org.jetbrains.kotlin.jvm") version ("1.4.31")
    id("org.jetbrains.dokka") version ("1.4.30")
    id("com.vanniktech.maven.publish") version ("0.13.0")
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

group = property("GROUP") as String
version = property("VERSION_NAME") as String

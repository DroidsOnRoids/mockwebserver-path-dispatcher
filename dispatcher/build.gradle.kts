plugins {
    jacoco
    id("org.jetbrains.kotlin.jvm") version ("1.5.31")
    id("org.jetbrains.dokka") version ("1.5.30")
    id("com.vanniktech.maven.publish") version ("0.18.0")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.yaml:snakeyaml:1.29:android")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.7.1")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

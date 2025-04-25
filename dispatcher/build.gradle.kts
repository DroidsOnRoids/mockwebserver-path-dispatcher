plugins {
    jacoco
    id("org.jetbrains.kotlin.jvm") version ("2.1.20")
    id("org.jetbrains.dokka") version ("2.0.0")
    id("com.vanniktech.maven.publish") version ("0.31.0")
}

dependencies {
    implementation("com.squareup.okhttp3:mockwebserver:4.9.2")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.yaml:snakeyaml:2.4")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.7.2")
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

kotlin {
    jvmToolchain(17)
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

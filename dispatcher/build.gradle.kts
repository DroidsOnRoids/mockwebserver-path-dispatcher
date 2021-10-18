import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    id("org.jetbrains.kotlin.jvm") version ("1.5.31")
    id("org.jetbrains.dokka") version ("1.5.31")
    id("com.vanniktech.maven.publish") version ("0.18.0")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("com.squareup.okhttp3:mockwebserver:4.9.2")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.yaml:snakeyaml:1.29:android")
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

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

group = property("GROUP") as String
version = property("VERSION_NAME") as String

plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

dependencies {
    implementation(libs.mockwebserver)
    implementation(libs.commons.text)
    implementation(libs.snakeyaml)

    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.junit)
    testImplementation(libs.equalsverifier)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
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

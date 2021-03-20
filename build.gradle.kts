allprojects {
    repositories {
        mavenCentral()
        jcenter {
            content {
                includeModule("org.jetbrains.kotlinx", "kotlinx-html-jvm")
            }
        }
    }
    buildscript {
        repositories {
            mavenCentral()
        }
    }
}
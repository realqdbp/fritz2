plugins {
    kotlin("multiplatform") version "1.9.0" apply false
    kotlin("plugin.serialization") version "1.9.0" apply false
    id("org.jetbrains.dokka") version "1.8.10"
    id("maven-publish")
    signing
}

//// needed to work on Apple Silicon. Should be fixed by 1.6.20 (https://youtrack.jetbrains.com/issue/KT-49109#focus=Comments-27-5259190.0-0)
//rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
//}

// consider moving to idiomatic solution of gradle for dependency sharing once it is ready:
// https://docs.gradle.org/current/userguide/platforms.html
ext {
    // Dependencies
    set("kotlinVersion", "1.9.0")
    set("coroutinesVersion", "1.7.1")
    set("kotlinpoetVersion", "1.14.2")
    set("compileTestingVersion", "1.5.0")
    set("stylisVersion", "4.3.0")
    set("murmurhashVersion", "2.0.1")
    set("logbackVersion", "1.4.7")

    set("ktorVersion", "1.6.8")
    set("serializationVersion", "1.5.1")
    set("kspVersion", "1.9.0-1.0.11")
    set("autoServiceVersion", "1.1.1")
    set("junitJupiterParamsVersion", "5.8.2")
    set("assertJVersion", "3.23.1")
}

allprojects {
    //manage common setting and dependencies
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = "dev.fritz2"
    version = "0.14.6"
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(rootDir.resolve("api"))
}

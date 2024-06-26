import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile
import java.nio.file.Files
import java.util.Date
import java.text.SimpleDateFormat
import java.util.regex.Pattern

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.bartholdy"
version = "1.0-SNAPSHOT"
//version = SimpleDateFormat("ssSSS").format(Date())


repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.onelitefeather.microtus:Microtus:1.4.2")
    testImplementation("net.onelitefeather.microtus.testing:testing:1.4.2")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("redis.clients:jedis:5.1.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.build {
    val timestamp = SimpleDateFormat("MM-dd-yyyy_hh-mm").format(Date())
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "me.bartholdy.endlessjump.Server.Main"
        }
    }

//    exec {
//        commandLine("kill", "-9", "1000").setIgnoreExitValue(true)
//    }

//    exec {
//        commandLine("find","./build/libs", "-regex", "/([A-Z])\\w+-\\d{5}(?!\\d).jar")
//        val name = File("./build/libs/").list()
//
//        commandLine("echo The size is ", name.size)
//        val pattern = Pattern.compile("([A-Z])\\w+-\\d{5}(?!\\d).jar")
//        val matcher = pattern.matcher(name.toString())
//        matcher.find()
//        val strippedVersion = matcher.find();
//        commandLine("echo", strippedVersion)
//    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix
//        rename("project-1.5.jar", "project.jar")
    }
}
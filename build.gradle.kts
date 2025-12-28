plugins {
    id("java")
}

group = "com.github.groundbreakingmc.menux"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
    maven("https://repo.codemc.io/repository/maven-releases/") { name = "codemc-releases" }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") { name = "placeholderapi" }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

    // https://mvnrepository.com/artifact/it.unimi.dsi/fastutil
    compileOnly("it.unimi.dsi:fastutil:8.5.18")

    // https://github.com/retrooper/packetevents
    compileOnly("com.github.retrooper:packetevents-spigot:2.10.1")

    // https://docs.papermc.io/adventure/minimessage/api/
    compileOnly("net.kyori:adventure-text-minimessage:4.25.0")

    // https://docs.papermc.io/adventure/platform/
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")

    // https://github.com/SpongePowered/Configurate
//    compileOnly("org.spongepowered:configurate-core:4.2.0")
    compileOnly("org.spongepowered:configurate-yaml:4.2.0") // TODO remove after tests

    // https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Hook-into-PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")

    // https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.2.9.Final")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 21
    }

    withType<Jar> {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }
    }

    withType<Javadoc> {
        options {
            encoding = "UTF-8"
            (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
}
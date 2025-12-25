plugins {
    id("java")
    id("application")
}

group = "git.campones76"
version = "1.0"

application {
    mainClass.set("git.campones76.EventPhotoOrganizer")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Add this to handle duplicate resources
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.gotson:webp-imageio:0.2.2")
}

tasks.test {
    useJUnitPlatform()
}

// Create a fat JAR with all dependencies
tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "git.campones76.EventPhotoOrganizer"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

// Task to create native installer using jpackage
tasks.register<Exec>("createInstaller") {
    dependsOn("fatJar")

    doFirst {
        val os = org.gradle.internal.os.OperatingSystem.current()
        val jarFile = tasks.getByName<Jar>("fatJar").archiveFile.get().asFile
        val buildDir = layout.buildDirectory.get().asFile
        val appVersion = version.toString()

        val jpackageArgs = mutableListOf(
            "jpackage",
            "--input", buildDir.resolve("libs").absolutePath,
            "--name", "EventPhotoOrganizer",
            "--main-jar", jarFile.name,
            "--main-class", "git.campones76.EventPhotoOrganizer",
            "--type", when {
                os.isMacOsX -> "dmg"
                os.isWindows -> "msi"
                else -> "deb"
            },
            "--app-version", appVersion,
            "--vendor", "Gabe Fernando",
            "--copyright", "Copyright © 2025 Gabe Fernando",
            "--dest", buildDir.resolve("installer").absolutePath
        )

        // Add icon based on platform
        val resourcesDir = projectDir.resolve("src/main/resources/assets/ico")
        when {
            os.isMacOsX -> {
                val icns = resourcesDir.resolve("app-icon.icns")
                if (icns.exists()) {
                    jpackageArgs.addAll(listOf("--icon", icns.absolutePath))
                    jpackageArgs.addAll(listOf(
                        "--mac-package-name", "Event Photo Organizer",
                        "--mac-package-identifier", "git.campones76.eventphotoorganizer"
                    ))
                }
            }
            os.isWindows -> {
                val ico = resourcesDir.resolve("app-icon.ico")
                if (ico.exists()) {
                    jpackageArgs.addAll(listOf("--icon", ico.absolutePath))
                    jpackageArgs.addAll(listOf(
                        "--win-dir-chooser",
                        "--win-menu",
                        "--win-shortcut"
                    ))
                }
            }
            else -> {
                val png = resourcesDir.resolve("app-icon.png")
                if (png.exists()) {
                    jpackageArgs.addAll(listOf("--icon", png.absolutePath))
                }
            }
        }

        println("Creating installer with command:")
        println(jpackageArgs.joinToString(" "))

        commandLine(jpackageArgs)
    }
}
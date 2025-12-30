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
        // Add these for better macOS integration
        attributes["Apple-DockIcon-Name"] = "Event Photo Organizer"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/*.MF")
    exclude("META-INF/LICENSE*")
    exclude("META-INF/NOTICE*")
}

// Task to create native installer using jpackage
tasks.register<Exec>("createInstaller") {
    dependsOn("fatJar")

    doFirst {
        val os = org.gradle.internal.os.OperatingSystem.current()
        val jarFile = tasks.getByName<Jar>("fatJar").archiveFile.get().asFile
        val buildDir = layout.buildDirectory.get().asFile
        val appVersion = version.toString()

        // Ensure installer directory exists
        val installerDir = buildDir.resolve("installer")
        installerDir.mkdirs()

        // Create a temp directory for jpackage input
        val tempInput = buildDir.resolve("jpackage-input")
        tempInput.mkdirs()

        // Copy the fat JAR to the temp input directory
        val inputJar = tempInput.resolve(jarFile.name)
        jarFile.copyTo(inputJar, overwrite = true)

        val jpackageArgs = mutableListOf(
            "jpackage",
            "--input", tempInput.absolutePath,
            "--name", "EventPhotoOrganizer",
            "--main-jar", jarFile.name,
            "--main-class", "git.campones76.EventPhotoOrganizer",
            "--app-version", appVersion,
            "--vendor", "Gabe Fernando",
            "--dest", installerDir.absolutePath,
            "--verbose"
        )

        // Determine package type
        when {
            os.isMacOsX -> jpackageArgs.addAll(listOf("--type", "dmg"))
            os.isWindows -> jpackageArgs.addAll(listOf("--type", "msi"))
            else -> jpackageArgs.addAll(listOf("--type", "deb"))
        }

        // Add Java runtime options
        if (os.isMacOsX) {
            jpackageArgs.add("--java-options")
            jpackageArgs.add("-Xmx1024m")
            jpackageArgs.add("--java-options")
            jpackageArgs.add("-Dapple.laf.useScreenMenuBar=true")
            jpackageArgs.add("--java-options")
            jpackageArgs.add("-Dapple.awt.application.name=EventPhotoOrganizer")
            jpackageArgs.add("--java-options")
            jpackageArgs.add("-Djava.awt.headless=false")
        }

        // Add icon
        val resourcesDir = projectDir.resolve("src/main/resources/assets/ico")
        when {
            os.isMacOsX -> {
                val icns = resourcesDir.resolve("app-icon.icns")
                if (icns.exists()) {
                    jpackageArgs.add("--icon")
                    jpackageArgs.add(icns.absolutePath)
                }
                jpackageArgs.add("--mac-package-name")
                jpackageArgs.add("EventPhotoOrganizer")
                jpackageArgs.add("--mac-package-identifier")
                jpackageArgs.add("git.campones76.eventphotoorganizer")
                jpackageArgs.add("--copyright")
                jpackageArgs.add("Copyright © 2025 Gabe Fernando")
            }
            os.isWindows -> {
                val ico = resourcesDir.resolve("app-icon.ico")
                if (ico.exists()) {
                    jpackageArgs.add("--icon")
                    jpackageArgs.add(ico.absolutePath)
                }
                jpackageArgs.add("--win-dir-chooser")
                jpackageArgs.add("--win-menu")
                jpackageArgs.add("--win-shortcut")
                jpackageArgs.add("--copyright")
                jpackageArgs.add("Copyright © 2025 Gabe Fernando")
            }
            else -> {
                val png = resourcesDir.resolve("app-icon.png")
                if (png.exists()) {
                    jpackageArgs.add("--icon")
                    jpackageArgs.add(png.absolutePath)
                }
                jpackageArgs.add("--copyright")
                jpackageArgs.add("Copyright © 2025 Gabe Fernando")
            }
        }

        println("Creating installer with command:")
        println(jpackageArgs.joinToString(" "))

        commandLine(jpackageArgs)
    }
}

// Task to run the fat JAR for testing
tasks.register<JavaExec>("runFatJar") {
    dependsOn("fatJar")
    classpath = files(tasks.getByName<Jar>("fatJar").archiveFile)
    mainClass.set("git.campones76.EventPhotoOrganizer")
}
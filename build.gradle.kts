var isJenkins = System.getenv("JENKINS_URL") == "https://jenkins.sgpggb.de/"

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        name = "sgpggbRepo"
        url = uri("https://repo.sgpggb.de/repository/maven-releases/")
        credentials(PasswordCredentials::class)
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven {
        url = uri("https://libraries.minecraft.net")
    }

    mavenLocal { mavenContent { includeGroupAndSubgroups("de.sgpggb") } }
}

dependencies {
    api("net.md-5:bungeecord-api:1.21-R0.2")
    compileOnly("de.sgpggb:PluginUtilitiesLibBungee:4.9")
    compileOnly("de.sgpggb:RewardsBungee:0.1")
    compileOnly("de.sgpggb:SGPGGBEconomy:2.45")

}

group = "de.sgpggb"
version = "0.2"
var mcapi = "1.21"
description = "Surveys"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    //withJavadocJar()
    //withSourcesJar()
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    compileTestJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        val username = providers.systemProperty("user.name").get()
        var versionString = project.version
        inputs.property("finalversion", versionString)
        filesMatching("plugin.yml") {
            versionString = if (isJenkins) {
                val build = System.getenv("BUILD_NUMBER")
                "${project.version}-#${build} (${mcapi})"
            } else {
                "${project.version}-local-${username} (${mcapi})"
            }
            expand("finalversion" to versionString,
                "mcapi" to mcapi)
        }
    }

    jar {
        archiveFileName.set("${project.name}.jar")
    }
}

if (isJenkins) {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "sgpggbRepo"
                url = uri("https://repo.sgpggb.de/repository/maven-releases/")
                credentials(PasswordCredentials::class)
            }
        }
    }
} else {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}


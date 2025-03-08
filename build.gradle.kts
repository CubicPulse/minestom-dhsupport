plugins {
    `java-library`
    id("me.qoomon.git-versioning") version "6.4.4"
    `maven-publish`
}

group = "com.cubicpulse"
version = "1.0-SNAPSHOT"

gitVersioning.apply {
    refs {
        branch("main") {
            version = "\${describe.tag}.\${describe.distance}+\${commit}"
        }
        branch("feat/(?<feature>.+)") {
            version = "\${ref.feature}-\${commit.short}-SNAPSHOT"
        }
        branch(".+") {
            version = "\${ref}-\${commit.short}-SNAPSHOT"
        }

        tag("v(?<version>.*)") {
            version = "\${ref.version}.0+\${commit}"
        }
    }

    // optional fallback configuration in case of no matching ref configuration
    rev {
        version = "\${commit}"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:1_21_4-bb14804d42")
    
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    testImplementation("net.minestom:minestom-snapshots:1_21_4-bb14804d42")
    testImplementation("ch.qos.logback:logback-classic:1.5.16")
    testImplementation("ch.qos.logback:logback-core:1.5.16")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "minestom-dhsupport"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("Minestom-DHSupport")
                description.set("A port of the DHSupport plugin for Minestom")
                developers {
                    developer {
                        id = "bl19"
                        name = "BL19"
                        email = "bl19@bl19.dev"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/CubicPulse/minestom-dhsupport.git"
                    developerConnection = "scm:git:ssh://github.com/CubicPulse/minestom-dhsupport.git"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://repo.cubicpulse.com/nexus/repository/cubicpulse-releases/")
            credentials {
                username = System.getenv("CUBIC_NEXUS_USERNAME")
                password = System.getenv("CUBIC_NEXUS_PASSWORD")
            }
        }
    }
}
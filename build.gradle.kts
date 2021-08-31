plugins {
    java
    `maven-publish`
}

group = "com.github.yjgbg"
version = "ROLLING-SNAPSHOT"
description = "fun-valid"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("io.vavr:vavr:0.10.4")
    compileOnly("org.jetbrains:annotations:+")
    compileOnly("org.projectlombok:lombok:1.18.18")
    annotationProcessor("org.projectlombok:lombok:1.18.18")
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("this") {
        from(components["java"])
    }
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots") {
        credentials {
            username = project.ext["mavenUsername"].toString()
            password = project.ext["mavenPassword"].toString()
        }
    }
}

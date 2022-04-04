plugins {
    `java-library`
    `maven-publish`
}

group = "com.github.yjgbg"
version = "2.0-SNAPSHOT"
description = "fun-valid"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:+")
    compileOnly("org.projectlombok:lombok:1.18.+")
    annotationProcessor("org.projectlombok:lombok:1.18.+")
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

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.create("geneDocs") {
    dependsOn("javadoc")
    doLast {
        delete("docs")
        copy {
            exclude("**/*.zip")
            from("build/docs/javadoc")
            into("docs")
        }
    }
}
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
    publications.create<MavenPublication>("snapshot") {
        from(components["java"])
        pom {
            version = "${project.ext["publicationVersion"].toString()}-SNAPSHOT"
        }
    }
    publications.create<MavenPublication>("hypers") {
        from(components["java"])
        pom {
            groupId = "com.hypers.weicl"
            version = project.ext["publicationVersion"].toString()
        }
    }
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "snapshot"
        credentials {
            username = project.ext["mavenUsername"].toString()
            password = project.ext["mavenPassword"].toString()
        }
    }
    repositories.maven("https://nexus3.hypers.cc/repository/maven-releases/") {
        name = "hypers"
        credentials {
            username = project.ext["hypersMavenUsername"].toString()
            password = project.ext["hypersMavenPassword"].toString()
        }
    }
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.create("genDocs") {
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
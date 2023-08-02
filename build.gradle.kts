import com.ewerk.gradle.plugins.tasks.QuerydslCompile

plugins {
    java
    id("org.springframework.boot") version "2.7.14"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
}

group = "com.shop"
version = "0.0.1-SNAPSHOT"
val queryDslVersion = "5.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.ewerk.gradle.plugins:querydsl-plugin:1.0.10")
    }
}

apply(plugin = "com.ewerk.gradle.plugins.querydsl")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.2")
    implementation("com.querydsl:querydsl-jpa:${queryDslVersion}")
    annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

val querydslDir = "$buildDir/generated/querydsl"

querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
sourceSets.getByName("main") {
    java.srcDir(querydslDir)
}
configurations {
    named("querydsl") {
        extendsFrom(configurations.compileClasspath.get())
    }
}
tasks.withType<QuerydslCompile> {
    options.annotationProcessorPath = configurations.querydsl.get()
}

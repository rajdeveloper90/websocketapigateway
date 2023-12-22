
plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.infusion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation ("com.amazonaws:aws-java-sdk-core:1.2.1")
    implementation("software.amazon.awssdk:core:2.17.61")
    implementation("software.amazon.awssdk:bom:2.17.87")
    implementation ("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation ("com.amazonaws:aws-lambda-java-events:2.2.9")
    implementation("software.amazon.awssdk:dynamodb:2.17.101")
    implementation("software.amazon.awssdk:apigatewaymanagementapi:2.17.101")
    implementation ("software.amazon.awssdk:cognitoidentityprovider:2.17.75")
    implementation("software.amazon.awssdk:auth:2.17.101")
    implementation("software.amazon.awssdk:regions:2.17.101")
    implementation ("software.amazon.awssdk:bom:2.17.87")
    implementation ("software.amazon.awssdk:lambda:2.17.51")
    implementation ("com.amazonaws:aws-java-sdk-apigatewaymanagementapi:1.12.142")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation ("org.webjars:stomp-websocket:2.3.3")
    implementation ("org.webjars:bootstrap:5.0.2")
    implementation ("org.webjars:jquery:3.1.1-1")
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.shadowJar {
    archiveBaseName.set("websocket")
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.getByName("runtimeClasspath"))
}

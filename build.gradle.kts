import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"

    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "7.3.2"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("com.palantir.git-version") version "0.12.3"
    id("java")
    id("nu.studer.jooq") version "5.2"
}

sourceSets.main {
    java.srcDirs("src/main/kotlin", "src/generated/jooq")
}

group = "io.prhunter"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16
val ktorVersion = "1.6.2"

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.15.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("io.github.microutils:kotlin-logging:2.0.8")

    implementation("org.zalando:logbook:2.14.0")
    implementation("org.zalando:logbook-spring-boot-starter:2.14.0")

    implementation("org.springdoc:springdoc-openapi-ui:1.5.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.5.11")
    implementation("org.springdoc:springdoc-openapi-security:1.5.11")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    implementation("org.kohsuke:github-api:1.132")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")

    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation("org.bouncycastle:bcprov-jdk15on:1.69")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.69")

    implementation("com.vladmihalcea:hibernate-types-55:2.12.1")
    implementation("org.jooq:jooq:3.14.7")
    jooqGenerator("org.postgresql:postgresql:42.2.14")

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.27.1")
    testImplementation("com.marcinziolo:kotlin-wiremock:1.0.2")
    testImplementation("org.testcontainers:mockserver:1.16.0")


    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

flyway {
    url = "jdbc:postgresql://localhost:5432/prhunter"
    user = "localdev"
    password = "localdev"
}

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

jib {
    from {
        image = "openjdk:16-alpine"
    }
    to {
        image = "registry.digitalocean.com/prhunter/api:${versionDetails().gitHashFull}"
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx1500m")
    }
}

jooq {
    version.set("3.14.7")  // default (can be omitted)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("api") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(false)  // default (can be omitted)

            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/prhunter"
                    user = "localdev"
                    password = "localdev"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes.addAll(
                            arrayOf(
                                ForcedType()
                                    .withName("varchar")
                                    .withIncludeExpression(".*")
                                    .withIncludeTypes("JSONB?"),
                                ForcedType()
                                    .withName("varchar")
                                    .withIncludeExpression(".*")
                                    .withIncludeTypes("INET")
                            ).toList()
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "io.prhunter.generated"
                        directory = "src/generated/jooq"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

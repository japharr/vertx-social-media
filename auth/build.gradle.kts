import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

apply(plugin = "com.github.johnrengelman.shadow")

dependencies {
  val vertxVersion = project.extra["vertxVersion"]
  val jupiterVersion = project.extra["jupiterVersion"]
  val flywayVersion = project.extra["flywayVersion"]
  val postgresVersion = project.extra["postgresVersion"]
  val testContainersVersion = project.extra["testContainersVersion"]
  val logbackClassicVersion = project.extra["logbackClassicVersion"]

  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-config-yaml:$vertxVersion")
  implementation("io.vertx:vertx-auth-sql-client:$vertxVersion")
  implementation("io.vertx:vertx-pg-client:$vertxVersion")
  implementation("io.vertx:vertx-jdbc-client:$vertxVersion")
  implementation("io.vertx:vertx-rx-java3:$vertxVersion")
  implementation("io.vertx:vertx-service-proxy:$vertxVersion")
  implementation("io.vertx:vertx-rx-java3-gen:$vertxVersion")

  annotationProcessor("io.vertx:vertx-service-proxy:$vertxVersion")
  annotationProcessor("io.vertx:vertx-codegen:$vertxVersion:processor")
  annotationProcessor("io.vertx:vertx-rx-java3-gen:$vertxVersion")

  implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
  implementation("org.flywaydb:flyway-core:$flywayVersion")
  implementation("org.postgresql:postgresql:$postgresVersion")
  implementation("org.testcontainers:postgresql:$testContainersVersion")

  implementation(project(":common"))

  // Use JUnit Jupiter for testing.
  testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
}

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"
val mainVerticleName = "com.japharr.socialmedia.auth.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

application {
  mainClass.set(launcherClassName)
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}


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

  // Use JUnit Jupiter for testing.
  testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
}

application {
  mainClass.set("com.japharr.socialmedia.auth.App")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

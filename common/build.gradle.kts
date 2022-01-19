
dependencies {
  val vertxVersion = project.extra["vertxVersion"]
  val jupiterVersion = project.extra["jupiterVersion"]

  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-config-yaml:$vertxVersion")
  implementation("io.vertx:vertx-rx-java3:$vertxVersion")

  // Use JUnit Jupiter for testing.
  testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
}

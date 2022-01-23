
dependencies {
  val vertxVersion = project.extra["vertxVersion"]
  val jupiterVersion = project.extra["jupiterVersion"]
  val logbackClassicVersion = project.extra["logbackClassicVersion"]
  val yaviVersion = project.extra["yaviVersion"]

  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-config:$vertxVersion")
  implementation("io.vertx:vertx-config-yaml:$vertxVersion")
  implementation("io.vertx:vertx-rx-java3:$vertxVersion")
  implementation("io.vertx:vertx-pg-client:$vertxVersion")

  implementation("am.ik.yavi:yavi:$yaviVersion")
  implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")

  // Use JUnit Jupiter for testing.
  testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
}

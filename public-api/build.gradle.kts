dependencies {
    val vertxVersion = project.extra["vertxVersion"]
    val jupiterVersion = project.extra["jupiterVersion"]
    val testContainersVersion = project.extra["testContainersVersion"]
    val logbackClassicVersion = project.extra["logbackClassicVersion"]
    val yaviVersion = project.extra["yaviVersion"]

    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-config:$vertxVersion")
    implementation("io.vertx:vertx-config-yaml:$vertxVersion")
    implementation("io.vertx:vertx-rx-java3:$vertxVersion")
    implementation("io.vertx:vertx-service-discovery:$vertxVersion")

    implementation("am.ik.yavi:yavi:$yaviVersion")
    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")

    implementation(project(":common"))
    implementation(project(":auth"))
    implementation(project(":app"))

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
}

application {
    mainClass.set("com.japharr.socialmedia.publicapi.Main")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
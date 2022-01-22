plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0" apply false
}

allprojects {
  extra["vertxVersion"] = "4.2.4"
  extra["jupiterVersion"] = "5.8.2"
  extra["flywayVersion"] = "8.4.1"
  extra["postgresVersion"] = "42.3.1"
  extra["logbackClassicVersion"] = "1.2.10"
  extra["testContainersVersion"] = "1.16.2"
  extra["yaviVersion"] = "0.9.1"
}

subprojects {

  repositories {
    mavenCentral()
    maven {
      url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
  }

  apply(plugin = "java")
  apply(plugin = "application")
}

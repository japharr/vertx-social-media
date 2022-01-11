plugins {
  java
  application
}

allprojects {
  extra["vertxVersion"] = "4.2.3"
  extra["jupiterVersion"] = "5.7.2"
  extra["logbackClassicVersion"] = "1.2.10"
  extra["flywayVersion"] = "6.3.1"
  extra["postgresVersion"] = "42.2.11"
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

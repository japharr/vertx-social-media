plugins {
  java
  application
}

allprojects {
  extra["vertxVersion"] = "4.2.3"
  extra["jupiterVersion"] = "5.8.2"
  extra["flywayVersion"] = "8.4.1"
  extra["postgresVersion"] = "42.3.1"
  extra["logbackClassicVersion"] = "1.2.10"
  extra["testContainersVersion"] = "1.16.2"
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

  tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
  }
}

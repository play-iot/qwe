plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    maven { url = uri("https://maven-central-asia.storage-download.googleapis.com/maven2/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    mavenCentral()
}

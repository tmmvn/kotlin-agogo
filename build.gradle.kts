plugins {
	alias(libs.plugins.kotlin)
	application
}

group = "com.venemies"
version = "2024.5.1"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
	implementation(kotlin("reflect"))
}

tasks.test {
	useJUnitPlatform()
}

application {
	mainClass = "com.venemies.AgogoKt"
}

kotlin {
	jvmToolchain(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.venemies.AgogoKt"
	}
	from(sourceSets.main.get().output)
}

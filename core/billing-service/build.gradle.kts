plugins {
    id("org.springframework.boot")              // 버전 생략 가능
    id("io.spring.dependency-management")  
    java
}

group = "com.kingkit"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.4")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")   // ✅ 버전 통일
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
    }
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web") 
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testImplementation(project(":lib:lib-test-support"))

	compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

	testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")


	implementation("org.flywaydb:flyway-core")
	testImplementation("org.springframework.security:spring-security-test")


	testImplementation ("org.mockito:mockito-core")
    testImplementation ("org.assertj:assertj-core")

	testImplementation("org.testcontainers:testcontainers")       // GenericContainer, DockerImageName
    testImplementation("org.testcontainers:junit-jupiter") 

	implementation(project(":lib:lib-security"))
	implementation(project(":lib:lib-dto"))

	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
	testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

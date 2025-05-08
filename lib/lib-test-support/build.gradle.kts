plugins {
    `java-library`
    id("io.spring.dependency-management")
}

tasks.named<Jar>("jar") { enabled = true } 

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.4")
    }
}

dependencies {
    // 테스트 유틸이므로 implementation → api or compileOnly도 가능
    implementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage") // vintage 제외
    }
    implementation("com.fasterxml.jackson.core:jackson-databind") 
    implementation("org.assertj:assertj-core:3.24.2")
    implementation("org.mockito:mockito-core:5.12.0")
    implementation("org.springframework.security:spring-security-test:6.2.1")
    implementation(project(":lib:lib-security"))
    implementation(project(":lib:lib-dto"))
    implementation("io.github.openfeign:feign-core:13.2.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

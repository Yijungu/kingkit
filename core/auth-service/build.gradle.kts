plugins {
    id("org.springframework.boot")              // 버전 생략 가능
    id("io.spring.dependency-management")       // 한번 더 명시 → 평가 순서 문제 방지
    java
}

dependencyManagement {          // ⭐ auth-service 안에서 한 번 더!
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.2")
    }
}

dependencies {
    /* Spring */
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    /* JWT */
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    /* Lombok */
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    /* 개발 편의 */
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    /* 테스트 */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    /* ⭐ OpenFeign — 버전 명시不要, BOM이 4.1.x로 매핑 */
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    implementation(project(":lib:lib-security"))

    implementation("org.flywaydb:flyway-core")
}

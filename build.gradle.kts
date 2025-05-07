plugins {
    id("org.springframework.boot") version "3.2.4" apply false   // 모듈에서 버전 없이 참조
    id("io.spring.dependency-management") version "1.1.4"        // 반드시 apply (false X)
}

allprojects {
    group = "com.kingkit"
    version = "0.0.1-SNAPSHOT"

    repositories { mavenCentral() }
}

/* ───────── 서브모듈 공통 설정 ───────── */
subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<Test> { useJUnitPlatform() }

    if (project.file("src/main").exists()) {
        // Boot 플러그인은 애플리케이션 모듈에만
        if (project.name !in listOf("lib-security")) {
            apply(plugin = "org.springframework.boot")
            extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(17))
            }
            dependencies {
                add("implementation", "org.springframework.boot:spring-boot-starter")
            }
        }
        // ✅ 라이브러리 모듈(lib-security)은 jar 빌드 그대로 활성
    } else {
        // 진짜 테스트 전용 모듈만 jar 끔
        tasks.matching { it.name == "bootJar" || it.name == "jar" }
            .configureEach { enabled = false }
    }
}
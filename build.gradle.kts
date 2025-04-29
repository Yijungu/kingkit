// --- plugins ---
plugins {
    id("org.springframework.boot") version "3.2.4" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    // java 플러그인은 이후 subprojects 에서 apply
}

// --- 모든 모듈 공통 ---
allprojects {
    group = "com.kingkit"
    version = "0.0.1-SNAPSHOT"

    repositories { mavenCentral() }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    // **테스트는 JUnit Platform으로 실행**
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // ‘src/main’ 이 존재하는 **실제 애플리케이션 모듈**만 Spring-Boot 적용
    if (project.file("src/main").exists()) {
        apply(plugin = "org.springframework.boot")

        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        }

        // 모듈 공통 의존성(필요 시)
        dependencies {
            "implementation"("org.springframework.boot:spring-boot-starter")
        }
    } else {
        // 테스트 전용 모듈이라면 bootJar / jar 비활성
        tasks.matching { it.name == "bootJar" || it.name == "jar" }
            .configureEach { enabled = false }
    }
}

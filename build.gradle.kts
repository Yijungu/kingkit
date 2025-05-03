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
        apply(plugin = "org.springframework.boot")             // 버전은 루트에서 상속
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        }
        dependencies {
            add("implementation", "org.springframework.boot:spring-boot-starter")
        }
    } else {
        tasks.matching { it.name == "bootJar" || it.name == "jar" }
            .configureEach { enabled = false }
    }
}

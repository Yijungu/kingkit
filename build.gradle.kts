plugins {
    id("org.springframework.boot") version "3.2.4" apply false   // 모듈에서 버전 없이 참조
    id("io.spring.dependency-management") version "1.1.4"  
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

    // ✅ JaCoCo 적용 조건 (테스트 디렉토리가 있는 경우에만)
    if (project.file("src/test").exists()) {
        apply(plugin = "jacoco")

        tasks.named<Test>("test") {
            useJUnitPlatform()
            extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }

        tasks.named<JacocoReport>("jacocoTestReport") {
            dependsOn("test")

            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            classDirectories.setFrom(
            fileTree("${buildDir}/classes") {
                include("**/*.class")
            }
)
            executionData.setFrom(fileTree(buildDir).include("jacoco/test.exec"))
            sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
        }
    }

    // 기존 설정 유지
    val isLibrary = project.path.startsWith(":lib")
    if (project.file("src/main").exists()) {
        if (!isLibrary) {
            apply(plugin = "org.springframework.boot")
            extensions.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(17))
            }
            dependencies {
                add("implementation", "org.springframework.boot:spring-boot-starter")
            }
        }
    } else {
        tasks.matching { it.name == "bootJar" || it.name == "jar" }
            .configureEach { enabled = false }
    }
}

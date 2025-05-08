plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // lombok을 사용하는 경우 (DTO용)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // DTO 검증용 (선택적으로 @NotBlank 등)
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
}

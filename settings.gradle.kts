rootProject.name = "kingkit"

include(
    "core:auth-service",
    "core:user-service",
    ":lib:lib-security",
    ":lib:lib-test-support",
    ":lib:lib-dto",
)

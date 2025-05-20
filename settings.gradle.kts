rootProject.name = "kingkit"

include(
    "core:auth-service",
    "core:user-service",
    "core:billing-service",
    ":lib:lib-security",
    ":lib:lib-test-support",
    ":lib:lib-dto",
)

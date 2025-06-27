rootProject.name = "tometrics"

include("db")
include("auth")
include("common")
include("services:protos")
include("services:commonclient")
include("services:user")
include("services:servicediscovery")
include("services:socialgraph")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
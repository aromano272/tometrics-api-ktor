rootProject.name = "tometrics"

include("db")
include("auth")
include("common")
include("services:protos")
include("services:commongrpc")
include("services:commonservice")
include("services:commonservicetest")
include("services:user")
include("services:servicediscovery")
include("services:socialfeed")
include("services:socialgraph")
include("services:cronjob")
include("services:email")
include("services:garden")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
include("services:media")
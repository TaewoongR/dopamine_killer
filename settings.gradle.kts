pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "dopamine_killer"
include(":app")
include(":ui:analysis")
include(":data:local")
include(":data:repository")
include(":ui:overview")
include(":ui:installedApp")
include(":data:service")
include(":ui:service")
include(":ui:navigation")
include(":ui:initialSet")
include(":ui:myInfo")
include(":ui:record")
include(":ui:reward")
include(":data:network")
include(":domain")
include(":domain:RecordDomain")
include(":domain:AnalysisDomain")
include(":domain:coreDomain")

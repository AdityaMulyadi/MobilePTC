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
        maven {         // add this repo to use snapshots
            name = "ossrh-snapshot"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

rootProject.name = "Mobile PTC"
include(":app")
 
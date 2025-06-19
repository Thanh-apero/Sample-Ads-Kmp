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
        maven {
            url =
                uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        }
        maven { url = uri("https://android-sdk.is.com/") }
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle")
        }
        maven {
            url = uri("https://artifactory.apero.vn/artifactory/gradle-release/")
            credentials {
                username = "software-inhouse"
                password = "apero@123"
            }
        }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    }
}

include(":sampleKmpApp")

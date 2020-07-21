pluginManagement {
    repositories {
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
        maven ("https://kotlin.bintray.com/kotlinx")
        google()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if(requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
rootProject.name = "rocksdb-multiplatform"

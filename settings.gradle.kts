pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { url = java.net.URI("https://maven.aliyun.com/repository/google") }
        maven { url = java.net.URI("https://maven.aliyun.com/repository/public") }
        maven { url = java.net.URI("https://jitpack.io") }
    }

    plugins {
        //如果需要使用kotlin
        kotlin("jvm").version(extra["kotlin.version"] as String)
        //kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("edu.sc.seis.launch4j").version("2.5.3")
        id("com.github.johnrengelman.shadow").version("7.1.2")
        id("org.beryx.runtime").version("1.12.7")
        id("com.github.gmazzo.buildconfig").version("3.1.0")
    }
}


rootProject.name = "YXLogDecode"

include(":android:app", ":android:decode-core", ":desktop:decode-ui", ":desktop:decode-core")
include(":android:YXLogDecodeDemo")
include(":android:CreateXLogFile")

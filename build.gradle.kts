allprojects {
    repositories {
        mavenLocal()
        maven {
            credentials {
                username = "6247fd667e8dbc28d8a2212c"
                password = "(4e])luJ1RG8"
            }
            url = uri("https://packages.aliyun.com/maven/repository/2117453-release-bMZITa/")
        }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        maven { url = uri("https://jitpack.io/") }
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
    }
}

project(":desktop:decode-core") {
    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = "17"
            targetCompatibility = "17"

            options.encoding = "UTF-8"
            options.isDeprecation = false
        }

        withType<Jar>().configureEach {
            // manifest for all created JARs
            manifest.attributes(
                "Implementation-Vendor" to "FormDev Software GmbH",
                "Implementation-Copyright" to "Copyright (C) 2019-${java.time.LocalDate.now().year} yinxueqin. All rights reserved.",
                "Implementation-Version" to project.version
            )
        }

        withType<Javadoc>().configureEach {
            options {
                this as StandardJavadocDocletOptions

                title = "${project.name} $version"
                header = title
                isUse = true
                tags = listOf( "uiDefault", "clientProperty" )
                addStringOption( "Xdoclint:all,-missing", "-Xdoclint:all,-missing" )
                links( "https://docs.oracle.com/en/java/javase/11/docs/api/" )
            }
            isFailOnError = false
        }
    }
}

project(":desktop:decode-ui") {
    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = "17"
            targetCompatibility = "17"

            options.encoding = "UTF-8"
            options.isDeprecation = false
        }

        withType<Jar>().configureEach {
            // manifest for all created JARs
            manifest.attributes(
                "Implementation-Vendor" to "FormDev Software GmbH",
                "Implementation-Copyright" to "Copyright (C) 2019-${java.time.LocalDate.now().year} yinxueqin. All rights reserved.",
                "Implementation-Version" to project.version
            )
        }

        withType<Javadoc>().configureEach {
            options {
                this as StandardJavadocDocletOptions

                title = "${project.name} $version"
                header = title
                isUse = true
                tags = listOf( "uiDefault", "clientProperty" )
                addStringOption( "Xdoclint:all,-missing", "-Xdoclint:all,-missing" )
                links( "https://docs.oracle.com/en/java/javase/11/docs/api/" )
            }
            isFailOnError = false
        }
    }
}

plugins {
    //如果需要使用kotlin
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
}
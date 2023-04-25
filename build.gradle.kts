val releaseVersion = "1.6"
val developmentVersion = "1.6"

version = if( java.lang.Boolean.getBoolean( "release" ) ) releaseVersion else developmentVersion

allprojects {
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}

// check required Java version
if( JavaVersion.current() < JavaVersion.VERSION_1_8 )
    throw RuntimeException( "Java 8 or later required (running ${System.getProperty( "java.version" )})" )

// log version, Gradle and Java versions
println()
println( "-------------------------------------------------------------------------------" )
println( "YXLogDecode Version: ${version}" )
println( "Gradle ${gradle.gradleVersion} at ${gradle.gradleHomeDir}" )
println( "Java ${System.getProperty( "java.version" )}" )
println()

allprojects {
    tasks {
        withType<JavaCompile>().configureEach {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"

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
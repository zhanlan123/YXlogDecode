plugins {
    `java-library`
    `decode-module-info`
}

dependencies {
    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk18on
    implementation("org.bouncycastle:bcprov-jdk18on:1.71")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    compileJava {
        // generate JNI headers
        options.headerOutputDirectory.set( buildDir.resolve( "generated/jni-headers" ) )
    }

    jar {
        archiveBaseName.set( "decode" )

        doLast {
            ReorderJarEntries.reorderJarEntries( outputs.files.singleFile );
        }
    }

    named<Jar>( "sourcesJar" ) {
        archiveBaseName.set( "decode" )
    }

    named<Jar>( "javadocJar" ) {
        archiveBaseName.set( "decode" )
    }

    test {
        useJUnitPlatform()
        testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
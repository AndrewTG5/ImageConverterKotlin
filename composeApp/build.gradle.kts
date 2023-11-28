import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation("com.sksamuel.scrimage:scrimage-core:4.1.1")
            implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.1.1")
            implementation("com.sksamuel.scrimage:scrimage-webp:4.1.1")
            implementation("org.slf4j:slf4j-nop:2.0.9") // get rid of slf4j errors. can be replaced by "org.slf4j:slf4j-simple:2.0.9" if you want logging
        }
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ImageConverterKotlin"
            packageVersion = "2.0.0"
        }
    }
}

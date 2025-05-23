import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.yveskalume.compose.qrpainter"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.zxing.core)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("dev.yveskalume", "compose-qrpainter", "1.0.0-alpha01")

    pom {
        name.set("ComposeQRPainter")
        description.set("Generate QR codes in Jetpack Compose")
        inceptionYear.set("2023")
        url.set("https://github.com/yveskalume/compose-qrpainter")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("yveskalume")
                name.set("Yves Kalume")
                url.set("https://github.com/yveskalume")
            }
        }

        scm {
            url.set("https://github.com/yveskalume/compose-qrpainter")
            connection.set("scm:git:git://github.com/yveskalume/compose-qrpainter.git")
            developerConnection.set("scm:git:ssh://git@github.com/yveskalume/compose-qrpainter.git")
        }
    }
}

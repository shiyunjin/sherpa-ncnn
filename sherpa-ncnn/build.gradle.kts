plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    namespace = "com.shiyunjin.sherpancnn"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        val abis = (project.findProperty("SHERPA_NCNN_ABIS") as? String)
            ?.split(",")
            ?.map { it.trim() }
            ?: listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        ndk {
            abiFilters += abis
        }

        consumerProguardFiles("consumer-rules.pro")

        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DBUILD_SHARED_LIBS=OFF",
                    "-DSHERPA_NCNN_ENABLE_PORTAUDIO=OFF",
                    "-DSHERPA_NCNN_ENABLE_BINARY=OFF",
                    "-DSHERPA_NCNN_ENABLE_TEST=OFF",
                    "-DSHERPA_NCNN_ENABLE_C_API=OFF",
                    "-DSHERPA_NCNN_ENABLE_GENERATE_INT8_SCALE_TABLE=OFF",
                    "-DSHERPA_NCNN_ENABLE_JNI=ON",
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_EXT_MEM_ALIGNMENT=16384",
                    "-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384"
                )
                targets += listOf("sherpa-ncnn-jni")
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    externalNativeBuild {
        cmake {
            path = file("${projectDir}/../sherpa-ncnn-upstream/CMakeLists.txt")
            version = "3.22.1+"
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    lint {
        abortOnError = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.shiyunjin"
                artifactId = "sherpa-ncnn"
                version = project.findProperty("VERSION_NAME")?.toString() ?: "0.1.0"
            }
        }
    }
}

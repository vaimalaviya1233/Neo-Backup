/*
 * OAndBackupX: open-source apps backup and restore app.
 * Copyright (C) 2020  Antonios Hazim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.konan.properties.Properties

val buildNumber = rootProject.ext.get("buildNumber")
val buildVersion = rootProject.ext.get("buildVersion")

val locals = Properties()
if (rootProject.file("local.properties").exists()) {
    locals.load(rootProject.file("local.properties").inputStream())
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {

    signingConfigs {
        create("hg42test") {
            storeFile = file(locals.getProperty("keystore"))
            storePassword = locals.getProperty("keystorepass")
            keyAlias = "cert"
            keyPassword = locals.getProperty("keypass")
        }
    }

    compileSdk = 31

    defaultConfig {
        applicationId = "com.machiav3lli.backup"
        minSdk = 26
        targetSdk = 31
        versionCode = "80$buildNumber".toInt()
        versionName = "8.0.$buildVersion"

        // Tests
        testApplicationId = "${applicationId}.tests"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnit5Runner"
        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitPlatformRunner"
        //testInstrumentationRunner = "androidx.test.ext.junit.runners.AndroidJUnit5"

        // The following argument makes the Android Test Orchestrator run its
        // "pm clear" command after each test invocation. This command ensures
        // that the app's state is completely cleared between tests.
        // testInstrumentationRunnerArguments.put("clearPackageData", "true")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true"
                    )
                )
            }
        }

        println("\n---------------------------------------- version $versionCode $versionName\n\n")
    }

    buildTypes {
        named("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isMinifyEnabled = false
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_round"
            signingConfig = signingConfigs.getByName("hg42test")
        }
        named("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_round"
            signingConfig = signingConfigs.getByName("hg42test")
        }
        applicationVariants.all {
            val variant = this
            variant.outputs.all {
                val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                var name = output.name
                name = name.replace("-release", "")
                name = name.replace("-debug", "-d")
                name = name.replace("feature", "ft")
                output.outputFileName = "oabx-${name}-${buildVersion}.apk"
            }
        }
    }
    buildFeatures {
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests {
          isIncludeAndroidResources = true
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = compileOptions.sourceCompatibility.toString()
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    flavorDimensions.add("dev")
    productFlavors {
        create("hg42") {
            dimension = "dev"
            applicationIdSuffix = ".hg42"
            versionNameSuffix = "-hg42"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_vv"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_round_vv"
        }
        /*
        create("neo") {
            dimension = "dev"
            applicationIdSuffix = ".neo"
            versionNameSuffix = "-neo"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_vv"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_round_vv"
        }
        */
    }

    packagingOptions {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

    // Libs
    implementation("androidx.room:room-runtime:2.4.1")
    implementation("androidx.room:room-ktx:2.4.1")
    implementation("androidx.test.ext:junit-ktx:1.1.3")
    kapt("androidx.room:room-compiler:2.4.1")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("androidx.biometric:biometric:1.2.0-alpha04")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    val libsu = "3.2.1"
    implementation("com.github.topjohnwu.libsu:core:$libsu")
    implementation("com.github.topjohnwu.libsu:io:$libsu")
    //implementation("com.github.topjohnwu.libsu:busybox:$libsu")

    // UI
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("com.google.android.material:material:1.6.0-alpha02")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0-alpha01")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0-alpha01")
    val fastadapter = "5.6.0"
    implementation("com.mikepenz:fastadapter:$fastadapter")
    implementation("com.mikepenz:fastadapter-extensions-diff:$fastadapter")
    implementation("com.mikepenz:fastadapter-extensions-binding:$fastadapter")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Testing

    val junit4 = "4.13.2"
    val junitJupiter = "5.8.2"
    val junitPlatform = "1.8.0"
    val androidxTest = "1.4.0"
    val androidxTestExt = "1.1.3"
    val hamcrest = "2.2"
    val espresso = "3.4.0"

    // junit4

    //testImplementation("junit:junit:$junit4")
    implementation("androidx.test:rules:$androidxTest")
    androidTestImplementation("androidx.test:runner:$androidxTest")

    //testImplementation("junit:junit:$junit4")

    // To use the androidx.test.core APIs
    //androidTestImplementation("androidx.test:core:$androidxTest")
    // Kotlin extensions for androidx.test.core
    //androidTestImplementation("androidx.test:core-ktx:$androidxTest")

    // To use the JUnit Extension APIs
    androidTestImplementation("androidx.test.ext:junit:$androidxTestExt")
    // Kotlin extensions for androidx.test.ext.junit
    androidTestImplementation("androidx.test.ext:junit-ktx:$androidxTestExt")

    // Optional -- Hamcrest library
    //androidTestImplementation("org.hamcrest:hamcrest-library:$hamcrest")
    // Optional -- UI testing with Espresso
    //androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")
    //androidTestImplementation("androidx.test.espresso:espresso-contrib:$espresso")
    // Optional -- UI testing with UI Automator
    //androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    // Optional -- UI testing with Roboelectric
    //testImplementation("org.robolectric:robolectric:4.4")

    // To use the Truth Extension APIs
    //androidTestImplementation("androidx.test.ext:truth:$androidxTest")

    // To use android test orchestrator
    //androidTestUtil("androidx.test:orchestrator:$androidxTest")

    // junit5

    //testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
    //androidTestImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
    //androidTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
    // (Optional) If "Parameterized Tests" are needed
    //testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiter")
    // (Optional) If you also have JUnit 4-based tests
    //testImplementation("junit:junit:$junit4")
    //testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$junitJupiter")

    //testImplementation("org.junit.platform:junit-platform-runner:$junitPlatform")
    //androidTestImplementation("org.junit.platform:junit-platform-runner:$junitPlatform")
}

// using a task as a preBuild dependency instead of a function that takes some time insures that it runs
task("detectAndroidLocals") {
    val langsList: MutableSet<String> = HashSet()

    // in /res are (almost) all languages that have a translated string is saved. this is safer and saves some time
    fileTree("src/main/res").visit {
        if (this.file.path.endsWith("strings.xml")
            && this.file.canonicalFile.readText().contains("<string")
        ) {
            var languageCode = this.file.parentFile.name.replace("values-", "")
            languageCode = if (languageCode == "values") "en" else languageCode
            langsList.add(languageCode)
        }
    }
    val langsListString = "{${langsList.joinToString(",") { "\"${it}\"" }}}"
    android.defaultConfig.buildConfigField("String[]", "DETECTED_LOCALES", langsListString)
}
tasks.preBuild.dependsOn("detectAndroidLocals")

// tells all test tasks to use Gradle's built-in JUnit 5 support
tasks.withType<Test> {
    useJUnit()
    //useTestNG()
    //useJUnitPlatform()
}

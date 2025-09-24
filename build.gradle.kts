plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.android.library") version "8.6.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false // Align this
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}

 

buildscript {
    dependencies {

        classpath("com.google.gms:google-services:4.4.3")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57.1")
    }
}

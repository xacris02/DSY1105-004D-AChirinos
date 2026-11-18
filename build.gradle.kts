plugins {
    // AGP 8.2.2 es la última versión compatible con Gradle 8.3
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
    // KSP: procesador de anotaciones de Room (versión alineada con Kotlin 1.9.20)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false


}

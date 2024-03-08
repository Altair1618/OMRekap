plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false

}

subprojects {
    afterEvaluate {
        project.apply("../spotless.gradle")
    }
}

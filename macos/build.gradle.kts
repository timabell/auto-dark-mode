plugins {
    java
    kotlin("jvm")
    id("com.google.devtools.ksp")
    // id("dev.nokee.jni-library")
    // id("dev.nokee.objective-cpp-language")
    // `uber-jni-jar`
    `use-prebuilt-binaries`
    // `apple-m1-toolchain`
}

prebuiltBinaries {
    alwaysUsePrebuiltArtifact = true
    resourcePath = "com/github/weisj/darkmode/${project.name}"
    variants =
        listOf(
            StubJniLibrary(
                operatingSystem = "macos",
                architecture = "x86-64",
            ),
            StubJniLibrary(
                operatingSystem = "macos",
                architecture = "arm64",
            ),
        )
}

dependencies {
    implementation(libs.autoservice.annotations)
    implementation(projects.autoDarkModeBase)
    implementation(libs.darklaf.nativeUtils)
    compileOnly(kotlin("stdlib"))
    ksp(libs.autoservice.processor)
}

// if (!props.bool("macOSciModeFix", default = false)) {
//    apply(plugin = "com.google.devtools.ksp")
//    dependencies {
//        "ksp"(libs.autoservice.processor)
//    }
// }

/*library {
    dependencies {
        jvmImplementation(projects.autoDarkModeBase)
        jvmLibImplementation(libs.darklaf.nativeUtils)
        nativeLibImplementation(libs.macos.appKit)
    }
    targetMachines.addAll(machines.macOS.x86_64, machines.macOS.architecture("arm64"))
    variants.configureEach {
        resourcePath.set("com/github/weisj/darkmode/${project.name}")
        sharedLibrary {
            val isArm = targetMachine.architectureString == "arm64"
            val minOs = if (isArm) "11" else "10.10"
            compileTasks.configureEach {
                compilerArgs.addAll("-mmacosx-version-min=$minOs")
                // Build type not modeled yet, assuming release
                optimizedBinary()
            }
            linkTask.configure {
                linkerArgs.addAll(
                    "-lobjc",
                    "-mmacosx-version-min=$minOs",
                    // "-framework", "AppKit",
                )
            }
        }
    }
}*/

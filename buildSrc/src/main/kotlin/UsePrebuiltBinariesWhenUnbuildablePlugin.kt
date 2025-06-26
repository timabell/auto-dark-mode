// import dev.nokee.platform.jni.JavaNativeInterfaceLibrary
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.jvm.tasks.Jar
// import dev.nokee.platform.jni.JniLibrary
import java.io.File

/*
fun JniLibrary.canBuildSharedLibrary(): Boolean {
    // FIXME: This shouldn't be necessary
    if (!DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX
        && targetMachine.variantName == "macos-arm64"
    ) {
        return false
    }
    return sharedLibrary.isBuildable
}
*/
fun canBuildSharedLibrary() = false

data class StubJniLibrary(
    val operatingSystem: String,
    val architecture: String,
) {
    val variantName: String = "${operatingSystem}-${architecture}"
}

class UsePrebuiltBinariesWhenUnbuildablePlugin : Plugin<Project> {

    private lateinit var prebuiltExtension: PrebuiltBinariesExtension

    fun prebuiltBinaries(action: Action<PrebuiltBinariesExtension>) {
        action.execute(prebuiltExtension)
    }

    override fun apply(target: Project) {
        prebuiltExtension = target.extensions.create("prebuiltBinaries", PrebuiltBinariesExtension::class.java)
        // val library = target.extensions.getByType(JavaNativeInterfaceLibrary::class.java)
        target.afterEvaluate {
            prebuiltExtension.variants.forEach {
                if (prebuiltExtension.alwaysUsePrebuiltArtifact || !canBuildSharedLibrary()) {
                    configure(target, it)
                }
            }
        }
    }

    private fun libraryFileNameFor(name : String, osFamily: String): String = when(osFamily) {
        "windows" -> "$name.dll"
        "linux" -> "lib$name.so"
        "macos" -> "lib$name.dylib"
        else -> throw GradleException("Unknown operating system family '${osFamily}'.")
    }

    private fun configure(project: Project, library: StubJniLibrary) {
        useGithubLibrary(project, library)
    }

    private fun useGithubLibrary(project: Project, library: StubJniLibrary) {
        val prebuiltBinariesTask = project.tasks.register(
            "downloadPrebuiltBinary${library.variantName}",
            DownloadPrebuiltBinariesTask::class.java,
            library.variantName,
            prebuiltExtension
        )
        project.tasks.named("jar", Jar::class.java) {
            dependsOn(prebuiltBinariesTask)
            from(prebuiltBinariesTask.map { it.getPrebuiltBinaryFile() }) {
                into(prebuiltExtension.resourcePath) // Specify the path inside the JAR
                renameLibrary(project, library)
            }
        }
        /*
        library.sharedLibrary.compileTasks.configureEach { enabled = false }
        library.sharedLibrary.linkTask.configure { enabled = false }
        library.nativeRuntimeFiles.setFrom(prebuiltBinariesTask.map { it.getPrebuiltBinaryFile() })
        library.nativeRuntimeFiles.from(CallableAction {
            project.logger.warn(
                "${project.name}: Using pre-build library from github for targetMachine $variantName."
            )
        })
        */
    }

    private fun CopySpec.renameLibrary(project: Project, library: StubJniLibrary) {
        rename {
            libraryFileNameFor("${project.name}-${library.architecture}", library.operatingSystem)
        }
    }
}

open class PrebuiltBinariesExtension {
    var variants: List<StubJniLibrary> = listOf()
    var resourcePath: String = ""

    internal var githubArtifactSpec: GithubArtifactSpec? = null
    var prebuiltLibrariesFolder: String = "pre-build-libraries"
    var alwaysUsePrebuiltArtifact: Boolean = false
    var failIfLibraryIsMissing: Boolean = true

    fun github(user: String, repository: String, workflow: String, action: Action<GithubArtifactSpec>) {
        githubArtifactSpec = GithubArtifactSpec(user, repository, workflow).also { action.execute(it) }
    }
}

data class GithubArtifactSpec(
    var user: String,
    var repository: String?,
    var workflow: String,
    var manualDownloadUrl: String = "",
    var accessToken: String? = null,
    var timeout: Int = 0,
    var branches: List<String> = listOf("master")
)

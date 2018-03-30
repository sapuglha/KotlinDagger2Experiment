#!/usr/bin/env kotlinc -script

import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.*
import kotlin.experimental.and

fun clone(newRoot: String, packageName: String) {

    println("Cloning project to $newRoot/")

    val root = File("").absoluteFile
    root
            .walkTopDown()
            .map {
                it.relativeTo(root)
            }
            .filter {
                when {
                    emptyDirectory(it) -> false
                    it.name == ".gitignore" -> true
                    it.name == "build.gradle" -> true
                    it.path.startsWith(".git")
                            || it.path.startsWith(".idea")
                            || it.path.startsWith(".gradle")
                            || it.path.startsWith("build")
                            || it.path.startsWith("app/build")
                            || it.path.startsWith("data/build")
                            || it.path.startsWith("domain/build")
                            || it.name.matches(Regex(".*\\.iml")) -> false
                    else -> {
                        true
                    }
                }
            }
            .forEach {
                // Figure out where to copy it
                val fullPath = it.path
                var newPath = fullPath
                if (fullPath.contains("/src/")) {
                    // Need to map source directory package names
                    val newPackageDirs = packageName.replace(".", "/")
                    if (newPath.contains("com/robotsandpencils/kotlindaggerexperiement")) {
                        newPath = newPath.replace("com/robotsandpencils/kotlindaggerexperiement", newPackageDirs)
                    }
                    if (newPath.contains("com/robotsandpencils/kotlinexperiment")) {
                        newPath = newPath.replace("com/robotsandpencils/kotlinexperiment", newPackageDirs)
                    }
                }

                val newFile = Paths.get(newRoot, newPath).toFile()

                if (!newFile.exists()) {
                    newFile.parentFile.mkdirs()
                }

                // Get the file contents
                if (it.extension in arrayOf("kt", "java", "xml", "gradle")) {
                    copyAndMapPackages(it, newFile, packageName)
                } else if (it.isFile) {
                    it.copyTo(newFile, true, 4096)
                }

                println("Creating ${newFile.path}")
            }
}

fun emptyDirectory(dir: File): Boolean {
    return if (dir.isDirectory) {
        // Answer true if this is a directory with only directories inside
        // or false if there is a file inside
        dir.walkBottomUp().maxDepth(1).firstOrNull { it.isFile } == null
    } else {
        false
    }
}

fun copyAndMapPackages(from: File, to: File, newPackage: String) {
    val contents = from.readLines(Charset.forName("utf-8")).asSequence()

    val newAppName = newPackage.splitToSequence(delimiters = *arrayOf(".")).last().capitalize()

    val newContents = contents.map {
        it.replace("com.robotsandpencils.kotlindaggerexperiement", newPackage)
                .replace("com.robotsandpencils.kotlinexperiment", newPackage)
                .replace("""<string name="app_name">KotlinDaggerExperiement</string>""",
                        """<string name="app_name">$newAppName</string>""")
    }

    to.printWriter(Charset.forName("utf-8")).use { out ->
        newContents.forEach {
            out.println(it)
        }
    }
}

/*****************
 * Main Program
 */
try {
    clone(args[0], args[1])
} catch (ex: Throwable) {
    println("Fatal: ${ex.message}")
    println("usage: ./clone.kts [newRootDirectory] [packageName]")
}

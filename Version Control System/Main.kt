package svcs

import java.io.File
import java.io.FileNotFoundException
import java.security.MessageDigest

val workingDirectory: String = System.getProperty("user.dir")
val separator: String = File.separator

val vcsDir = File("${workingDirectory}${separator}vcs")
val commitsDir = File("${vcsDir}${separator}commits")

val configFile = File("${vcsDir}${separator}config.txt")
val indexFile = File("${vcsDir}${separator}index.txt")
val logFile = File("${vcsDir}${separator}log.txt")

fun main(args: Array<String>) {

    if (args.isEmpty()) printHelp()


    for (arg in args) {
        when (arg) {
            "config" -> {
                config(args)
                return
            }
            "add" -> {
                add(args)
                return
            }
            "log" -> {
                log()
                return
            }
            "commit" -> {
                commit(args)
                return
            }
            "checkout" -> {
                checkout(args)
                return
            }
            "--help" -> printHelp()
            else -> println("'$arg' is not a SVCS command.")
        }
    }
}

private fun log() {
    try {
        logFile.readLines()
    } catch (e: FileNotFoundException) {
        println("No commits yet.")
        return
    }

    val logFileContent = logFile.readText().split("\n\r")
    if (logFileContent.isEmpty()) {
        println("No commits yet.")
    } else {
        for (i in logFileContent.lastIndex -1  downTo 0){
            println(logFileContent[i])
            if (i != 0) println()
        }
    }
}

private fun commit(args: Array<String>) {
    if (args.size == 1) {
        println("Message was not passed.")
        return
    }
    //if (!vcsDir.exists() || !commitDir.exists() || commitDir.listFiles().isNullOrEmpty()) println("Nothing to commit.")
    if (!commitsDir.exists()) commitsDir.mkdir()

    if (!logFile.exists()) {
        logFile.createNewFile()
    }

    val trackedFiles = File("${vcsDir}${separator}index.txt").readLines()

    var commitIdString = ""

    for (fileName in trackedFiles) {
        val file = File("${workingDirectory}${separator}${fileName}")
        val hash = getHashOfFile(file)
        commitIdString += hash
    }

    // Get hash of all files' hashes
    val md = MessageDigest.getInstance("SHA-256")
    md.update(commitIdString.toByteArray())
    val digest = md.digest()

    var commitId = ""
    for (byte in digest) {
        commitId += "%02x".format(byte)
    }

    val commitIdDir = File("${commitsDir}${separator}${commitId}")
    try {
        commitIdDir.mkdir()
    } catch (e: FileAlreadyExistsException) {
        println("Nothing to commit.")
    }

    var nothingToCommit = true
    val lastCommitId = extractLastCommitId()

    if (lastCommitId == "") {
        copyAllFiles(trackedFiles, commitIdDir)
        printCommitMessage(commitId, args)
        return
    } else {
        // Check if any of the files changed
        for (fileName in trackedFiles) {
            val currentFile = File("${workingDirectory}${separator}${fileName}")

            val lastCommitFile = File("${commitsDir}${separator}${lastCommitId}${separator}${fileName}")

            val currentFileHash = getHashOfFile(currentFile)
            val lastCommitFileHash = getHashOfFile(lastCommitFile)

            if (lastCommitFileHash != currentFileHash) {
                nothingToCommit = false
            }
        }
    }

    if (nothingToCommit) {
        println("Nothing to commit.")
        return
    } else {
        copyAllFiles(trackedFiles, commitIdDir)
    }

    // Record info in the log
    printCommitMessage(commitId, args)
}

private fun printCommitMessage(commitId: String, args: Array<String>) {
    val logFile = File("${vcsDir}${separator}log.txt")
    if (!logFile.exists()) logFile.createNewFile()
    val message = """
        commit $commitId
        Author: ${configFile.readText().removePrefix("username = ")}
        ${args[1]}
    """.trimIndent()

    logFile.appendText(message)
    logFile.appendText("\n\r")
    println("Changes are committed.")
}

private fun copyAllFiles(
    trackedFiles: List<String>,
    commitIdDir: File,
) {
    for (fileName in trackedFiles) {
        val currentFile = File("${workingDirectory}${separator}${fileName}")
        val destination = File("${commitIdDir}${separator}${fileName}")
        currentFile.copyTo(destination)
    }
}

private fun extractLastCommitId(): String {
    val logFileContent = logFile.readLines()
    val commits = mutableListOf<String>()
    if (logFileContent.isEmpty()) return ""

    for (i in 0..logFileContent.lastIndex step 4) {
        val commitID = logFileContent[i].removePrefix("commit ")
        commits.add(commitID)
    }
    return commits[commits.lastIndex]
}

private fun config(args: Array<String>) {
    var newUsername = ""
    try {
        newUsername = args[1].trim()
    } catch (_: Exception) { }

    if (vcsDir.exists()) {
        if (newUsername == "" && configFile.exists()) {
            val username = configFile.readText().removePrefix("username = ")
            println("The username is $username.")
        } else {
            configFile.writeText("username = $newUsername")
            println("The username is $newUsername.")
        }
    } else {
        if (newUsername == "") {
            println("Please, tell me who you are.")
        } else {
            vcsDir.mkdir()
            configFile.writeText("username = $newUsername")
            println("The username is $newUsername.")
        }
    }
}

private fun add(args: Array<String>) {

    var fileToTrackName = ""

    try {
        fileToTrackName = args[1].trim()
    } catch (_: IndexOutOfBoundsException) {}

    val fileToTrack = File("${workingDirectory}${separator}${fileToTrackName}")

    if (vcsDir.exists()) {
        if (!fileToTrack.exists()) {
            println("Can't find '${fileToTrackName}'.")
        } else if (indexFile.exists() && fileToTrackName == "") {
            println("Tracked files:")
            for (line in indexFile.readLines()) println(line)
        } else if (indexFile.exists() && fileToTrackName.isNotEmpty()) {
            indexFile.appendText("$fileToTrackName\n")
            println("The file '${fileToTrackName}' is tracked.")
        } else if (!indexFile.exists() && fileToTrackName.isNotEmpty()) {
            indexFile.writeText("$fileToTrackName\n")
            println("The file '${fileToTrackName}' is tracked.")
        } else if (!indexFile.exists() && fileToTrackName == "") {
            println("Add a file to the index.")
        }

    } else {
        if (fileToTrackName == "") {
            println("Add a file to the index.")
        } else if (!fileToTrack.exists()) {
            println("Can't find '${fileToTrackName}'.")
        } else {
            vcsDir.mkdir()
            indexFile.writeText("$fileToTrackName\n")
            println("The file '${fileToTrackName}' is tracked.")
        }
    }
}

private fun checkout(args: Array<String>) {
    if (args.size == 1) {
        println("Commit id was not passed.")
        return
    }

    val logFileContent = logFile.readLines()
    val commits = mutableListOf<String>()

    for (i in 0..logFileContent.lastIndex step 4) {
        val commitID = logFileContent[i].removePrefix("commit ")
        commits.add(commitID)
    }

    val wantedCommitId = args[args.lastIndex]
    var commitIdForCheckout = ""

    for (commitId in commits) {
        if (commitId == wantedCommitId) commitIdForCheckout = commitId
    }

    if (commitIdForCheckout == "") {
        println("Commit does not exist.")
        return
    }

    val commitDir = File("${commitsDir}${separator}${commitIdForCheckout}")

    commitDir.copyRecursively(File("./"), overwrite = true)

    println("Switched to commit ${commitIdForCheckout}.")
}

private fun printHelp() {
    println("""
            These are SVCS commands:
            config     Get and set a username.
            add        Add a file to the index.
            log        Show commit logs.
            commit     Save changes.
            checkout   Restore a file.
        """.trimIndent())
}

private fun getHashOfFile(file: File): String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(file.readBytes())
    val digest = md.digest()
    var hash = ""
    for (byte in digest) {
        hash += "%02x".format(byte)
    }
    return hash
}

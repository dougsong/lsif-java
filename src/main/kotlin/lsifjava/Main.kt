package lsifjava

import java.io.File
import java.io.PrintWriter

val javaVersion: Int = getVersion()

fun main(args: Array<String>) {
    println("Running JVM ${System.getProperty("java.version")}")

    val arguments = parse(args)
    val writer = createWriter(arguments)
    val emitter = Emitter(writer)
    val indexer = ProjectIndexer(arguments, emitter)
    
    val start = System.nanoTime()
    
    try {
        indexer.index()
    } finally {
        writer.flush()
        writer.close()
    }

    displayStats(indexer, emitter, start)
}

private fun createWriter(args: Arguments): PrintWriter {
    return PrintWriter(File(args.outFile))
}

private fun displayStats(indexer: ProjectIndexer, emitter: Emitter, start: Long) {
    System.out.printf(
        "%d file(s), %d def(s), %d LSIF element(s), %d total javac error(s)\n",
        indexer.numFiles,
        indexer.numDefinitions,
        emitter.numElements(),
        indexer.numJavacErrors
    )
    System.out.printf("Processed in %.0fms", (System.nanoTime() - start) / 1e6)
}

fun getVersion(): Int {
    var version = System.getProperty("java.version")
    if (version.startsWith("1.")) {
        version = version.substring(2, 3)
    } else {
        val dot = version.indexOf(".")
        if (dot != -1) {
            version = version.substring(0, dot)
        }
    }
    return version.toInt()
}
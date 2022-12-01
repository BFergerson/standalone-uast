package spp.processor

import org.jetbrains.uast.UFile
import org.jetbrains.uast.toUElement
import java.io.File

object Main {

    @JvmStatic
    fun main(args2: Array<String>) {
//        val args = arrayOf("./testData/TestClass.java") //works
        val args = arrayOf("./testData/TestClass.kt") //fails

        val env = InsightEnvironment()
        args.map { File(it) }.forEach { env.addSourceFile(it) }

        val uastFile = env.getPsiFile(File(args.first()))!!.toUElement() as UFile
        val classes = uastFile.classes
        println(classes)
    }
}

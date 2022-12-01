package spp.processor

import org.jetbrains.uast.UFile
import org.jetbrains.uast.toUElement
import java.io.File

object Main {

    @JvmStatic
    fun main(args2: Array<String>) {
        val args = arrayOf("/tmp/project/TestClass.kt")

        val env = InsightEnvironment()
        args.map { File(it) }.forEach {
            env.addSourceFile(it)
        }

        val psiFile = env.getPsiFile(File(args.first()))!!
        val uastFile = psiFile.toUElement() as? UFile
        val methods = uastFile!!.classes.flatMap { it.methods.toList() }
        println(methods)
    }
}

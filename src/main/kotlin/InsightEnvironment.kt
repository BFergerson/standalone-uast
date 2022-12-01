package spp.processor

import com.intellij.core.CoreJavaFileManager
import com.intellij.core.JavaCoreProjectEnvironment
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.module.EmptyModuleManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.impl.DirectoryIndex
import com.intellij.openapi.roots.impl.DirectoryIndexImpl
import com.intellij.openapi.roots.impl.ProjectFileIndexImpl
import com.intellij.openapi.roots.impl.ProjectRootManagerImpl
import com.intellij.psi.PsiElementFinder
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.psi.impl.PsiNameHelperImpl
import com.intellij.psi.impl.file.impl.JavaFileManager
import org.jetbrains.kotlin.asJava.KotlinAsJavaSupport
import org.jetbrains.kotlin.idea.caches.resolve.IDEKotlinAsJavaSupport
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCommonCompilerArgumentsHolder
import org.jetbrains.kotlin.idea.compiler.configuration.KotlinCompilerSettings
import org.jetbrains.kotlin.idea.project.KotlinLibraryToSourceAnalysisStateComponent
import org.jetbrains.uast.kotlin.KotlinUastResolveProviderService
import org.jetbrains.uast.kotlin.internal.IdeaKotlinUastResolveProviderService
import java.io.File

class InsightEnvironment {

    private val disposable = Disposable {}
    private val applicationEnvironment = InsightApplicationEnvironment(disposable)
    private val projectEnvironment = JavaCoreProjectEnvironment(disposable, applicationEnvironment)

    init {
        val project = projectEnvironment.project
        val javaFileManager = project.getComponent(JavaFileManager::class.java)!!
        val coreJavaFileManager = javaFileManager as CoreJavaFileManager
        project.registerService(CoreJavaFileManager::class.java, coreJavaFileManager)
        project.registerService(PsiNameHelper::class.java, PsiNameHelperImpl.getInstance())

        project.extensionArea.registerExtensionPoint(
            PsiElementFinder.EP.name,
            PsiElementFinder::class.java.name,
            ExtensionPoint.Kind.INTERFACE
        )

        projectEnvironment.registerProjectComponent(
            ProjectRootManager::class.java,
            ProjectRootManagerImpl(projectEnvironment.project),
        )
        project.registerService(DirectoryIndex::class.java, DirectoryIndexImpl(projectEnvironment.project))

        project.registerService(ProjectFileIndex::class.java, ProjectFileIndexImpl(project))
        projectEnvironment.registerProjectComponent(ModuleManager::class.java, EmptyModuleManager(project))

        project.registerService(KotlinUastResolveProviderService::class.java, IdeaKotlinUastResolveProviderService())
        project.registerService(KotlinCommonCompilerArgumentsHolder::class.java)
        project.registerService(KotlinCompilerSettings::class.java)
        project.registerService(KotlinLibraryToSourceAnalysisStateComponent::class.java)

//        project.registerService(KotlinAsJavaSupport::class.java, IDEKotlinAsJavaSupport(project))
    }

    fun addSourceFile(sourceFile: File) {
        val root = applicationEnvironment.localFileSystem.findFileByIoFile(sourceFile)!!
        projectEnvironment.addSourcesToClasspath(root)
    }

    fun getPsiFile(file: File): PsiFile? {
        return PsiManager.getInstance(projectEnvironment.project).findFile(
            applicationEnvironment.localFileSystem.findFileByIoFile(file)!!
        )
    }
}

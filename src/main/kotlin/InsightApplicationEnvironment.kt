package spp.processor

import com.intellij.DynamicBundle
import com.intellij.codeInsight.ContainerProvider
import com.intellij.codeInsight.runner.JavaMainMethodProvider
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.lang.MetaLanguage
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.Extensions
import com.intellij.psi.FileContextProvider
import com.intellij.psi.augment.PsiAugmentProvider
import com.intellij.psi.impl.JavaClassSupersImpl
import com.intellij.psi.impl.smartPointers.SmartPointerAnchorProvider
import com.intellij.psi.meta.MetaDataContributor
import com.intellij.psi.util.JavaClassSupers
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.platform.DefaultIdeTargetPlatformKindProvider
import org.jetbrains.kotlin.platform.impl.IdeaDefaultIdeTargetPlatformKindProvider
import org.jetbrains.uast.UastLanguagePlugin
import org.jetbrains.uast.java.JavaUastLanguagePlugin
import org.jetbrains.uast.kotlin.KotlinUastLanguagePlugin

class InsightApplicationEnvironment(parentDisposable: Disposable) : JavaCoreApplicationEnvironment(parentDisposable) {
    init {
        myApplication.registerService(JavaClassSupers::class.java, JavaClassSupersImpl())

        val rootArea = Extensions.getRootArea()
        registerExtensionPoint(rootArea, FileContextProvider.EP_NAME, FileContextProvider::class.java)
        registerExtensionPoint(rootArea, MetaDataContributor.EP_NAME, MetaDataContributor::class.java)
        registerExtensionPoint(rootArea, PsiAugmentProvider.EP_NAME, PsiAugmentProvider::class.java)
        registerExtensionPoint(rootArea, JavaMainMethodProvider.EP_NAME, JavaMainMethodProvider::class.java)
        registerExtensionPoint(rootArea, ContainerProvider.EP_NAME, ContainerProvider::class.java)
        registerExtensionPoint(rootArea, UastLanguagePlugin.extensionPointName, UastLanguagePlugin::class.java)
        registerExtensionPoint(rootArea, MetaLanguage.EP_NAME, MetaLanguage::class.java)

        addExtension(UastLanguagePlugin.extensionPointName, JavaUastLanguagePlugin())
        addExtension(UastLanguagePlugin.extensionPointName, KotlinUastLanguagePlugin())

        registerFileType(KotlinFileType.INSTANCE, "kt")
        registerParserDefinition(KotlinParserDefinition())

        registerApplicationExtensionPoint(
            DynamicBundle.LanguageBundleEP.EP_NAME,
            DynamicBundle.LanguageBundleEP::class.java
        )
        registerApplicationExtensionPoint(SmartPointerAnchorProvider.EP_NAME, SmartPointerAnchorProvider::class.java)

        val instance = IdeaDefaultIdeTargetPlatformKindProvider::class.java.let {
            it.getDeclaredConstructor().let {
                it.isAccessible = true
                it.newInstance()
            }
        }
        registerApplicationService(DefaultIdeTargetPlatformKindProvider::class.java, instance)
    }
}

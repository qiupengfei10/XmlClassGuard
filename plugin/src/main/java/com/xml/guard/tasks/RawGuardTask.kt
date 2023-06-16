package com.xml.guard.tasks

import com.google.gson.Gson
import com.xml.guard.entensions.GuardExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import com.xml.guard.utils.allDependencyAndroidProjects
import com.xml.guard.utils.getSuffix
import com.xml.guard.utils.javaDirs
import com.xml.guard.utils.removeSuffix
import com.xml.guard.utils.replaceWords
import com.xml.guard.utils.resDirs
import org.gradle.api.Project
import java.io.File

open class RawGuardTask @Inject constructor(
    guardExtension: GuardExtension
) : DefaultTask() {

    init {
        group = "guard"
    }

    private var mapping = hashMapOf<String, String>()

    @TaskAction
    fun execute() {
        val androidProjects = allDependencyAndroidProjects()
        //1、遍历res下的xml文件，找到自定义的类(View/Fragment/四大组件等)，并将混淆结果同步到xml文件内
        androidProjects.forEach { project ->
            val rawDirs = project.resDirs().flatMapTo(ArrayList()) { dir ->
                dir.listFiles { _, name ->
                    //过滤res目录下的raw目录
                    name == "raw"
                }?.toList() ?: emptyList()
            }
            rawDirs.forEach {
                it.listFiles()?.forEach { file ->
                    if (file.name.getSuffix() == ".json") {
                        mapping[file.name.removeSuffix()] = ""
                        var obsName = getObsName()
                        val obfuscatePath = "${file.parent}${File.separator}${obsName}"
                        //替换原始类路径
                        val newFile = File(
                            file.absolutePath.replace(
                                file.absolutePath,
                                "${obfuscatePath}${file.absolutePath.getSuffix()}"
                            )
                        )
                        mapping[file.name.removeSuffix()] = obsName
                        if (!newFile.exists()) newFile.parentFile.mkdirs()
                        newFile.writeText(file.readText())
                        file.delete()
                    }
                }
            }
            println(Gson().toJson(mapping))
            if (mapping.isNotEmpty()) {
                androidProjects.forEach {
                    replaceJavaText(it, mapping)
                    replaceXml(it, mapping)
                }
            }
        }
    }

    private fun getObsName(): String {
        var result = getRandomString(10)
        while (mapping.values.contains(result)) {
            result = getRandomString(10)
        }
        return result
    }

    private fun replaceJavaText(project: Project, mapping: Map<String, String>) {
        val javaDirs = project.javaDirs()
        //遍历所有Java\Kt文件，替换混淆后的类的引用，import及new对象的地方
        project.files(javaDirs).asFileTree.forEach { javaFile ->
            var replaceText = javaFile.readText()
            mapping.forEach {
                replaceText = replaceText(javaFile, replaceText, it.key, it.value)
            }
            mapping.forEach {
                replaceText = replaceTextAgain(javaFile, replaceText, it.key, it.value)
            }
            javaFile.writeText(replaceText)
        }
    }

    private fun replaceXml(project: Project, mapping: Map<String, String>) {
        val xmlDirs = project.resDirs().flatMapTo(ArrayList()) { dir ->
            dir.listFiles { _, name ->
                //过滤res目录下的raw目录
                name.startsWith("layout")
            }?.toList() ?: emptyList()
        }
        //遍历所有Java\Kt文件，替换混淆后的类的引用，import及new对象的地方
        project.files(xmlDirs).asFileTree.forEach { javaFile ->
            var replaceText = javaFile.readText()
            mapping.forEach {
                replaceText = replaceText(javaFile, replaceText, it.key, it.value)
            }
            mapping.forEach {
                replaceText = replaceTextAgain(javaFile, replaceText, it.key, it.value)
            }
            javaFile.writeText(replaceText)
        }
    }

    private fun replaceText(
        rawFile: File,
        rawText: String,
        rawPath: String,
        obfuscatePath: String,
    ): String {
        var replaceText = rawText

        replaceText = replaceText.replaceWords("(R.raw.${rawPath})", "(R.raw.${obfuscatePath})")
            .replaceWords("\"@raw/${rawPath}\"", "\"@raw/${obfuscatePath}\"")
        return replaceText
    }

    private fun replaceTextAgain(
        rawFile: File,
        rawText: String,
        rawPath: String,
        obfuscatePath: String,
    ): String {
        var replaceText = rawText

        replaceText = replaceText.replaceWords("R.raw.${rawPath}", "R.raw.${obfuscatePath}")
            .replaceWords("@raw/${rawPath}", "@raw/${obfuscatePath}")
        return replaceText
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('a'..'z')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }
}
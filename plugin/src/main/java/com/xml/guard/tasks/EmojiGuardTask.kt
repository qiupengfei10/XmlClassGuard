package com.xml.guard.tasks

import com.google.gson.Gson
import com.xml.guard.entensions.GuardExtension
import com.xml.guard.model.MappingParser
import com.xml.guard.utils.allDependencyAndroidProjects
import com.xml.guard.utils.assetsDirs
import com.xml.guard.utils.findClassByLayoutXml
import com.xml.guard.utils.findClassByManifest
import com.xml.guard.utils.findClassByNavigationXml
import com.xml.guard.utils.findLocationProject
import com.xml.guard.utils.findPackage
import com.xml.guard.utils.getDirPath
import com.xml.guard.utils.getSuffix
import com.xml.guard.utils.javaDirs
import com.xml.guard.utils.manifestFile
import com.xml.guard.utils.removeSuffix
import com.xml.guard.utils.replaceWords
import com.xml.guard.utils.resDirs
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * User: ljx
 * Date: 2022/2/25
 * Time: 19:06
 */
open class EmojiGuardTask @Inject constructor(
    guardExtension: GuardExtension
) : DefaultTask() {

    init {
        group = "guard"
    }

    private var jsonMapping = hashMapOf<String, String>()

    private var emojiPath = guardExtension.emojiFile?.name ?: "dynamic_emoji"

    @TaskAction
    fun execute() {
        val androidProjects = allDependencyAndroidProjects()
        //1、遍历res下的xml文件，找到自定义的类(View/Fragment/四大组件等)，并将混淆结果同步到xml文件内
        androidProjects.forEach { project ->
            val emojiDirs = project.assetsDirs().flatMapTo(ArrayList()) { dir ->
                dir.listFiles { _, name ->
                    //过滤res目录下的layout、navigation目录
                    name == emojiPath
                }?.toList() ?: emptyList()
            }
            emojiDirs.forEach {
                it.listFiles().forEach { file ->
                    var osbName = getObsName()
                    val obfuscatePath = "${file.parent}${File.separator}${osbName}"
//                    val obfuscateRelativePath = obfuscatePath.replace(".", File.separator)
//                    val rawRelativePath = file.absolutePath.replace(".", File.separator)
                    //替换原始类路径
                    val newFile = File(
                        file.absolutePath.replace(
                            file.absolutePath, "${obfuscatePath}${file.absolutePath.getSuffix()}"
                        )
                    )
                    jsonMapping[file.name.removeSuffix()] = osbName
                    println(" ${file.absolutePath.getSuffix()} ${obfuscatePath}  ${newFile.absolutePath}")
                    if (!newFile.exists()) newFile.parentFile.mkdirs()
                    newFile.writeText(file.readText())
                    file.delete()
                }
            }
        }
        println("混淆结果:${Gson().toJson(jsonMapping)}")
    }

    private fun getObsName(): String {
        var result = getRandomString(10)
        while (jsonMapping.values.contains(result)) {
            result = getRandomString(10)
        }
        return result
    }

    fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

}
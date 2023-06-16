package com.xml.guard.tasks

import com.google.gson.Gson
import com.xml.guard.entensions.GuardExtension
import com.xml.guard.utils.allDependencyAndroidProjects
import com.xml.guard.utils.manifestFile
import com.xml.guard.utils.resDirs
import groovy.util.Node
import groovy.xml.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.Locale
import javax.inject.Inject

open class CheckStringTask @Inject constructor(
    guardExtension: GuardExtension
) : DefaultTask() {
    init {
        group = "guard"
    }

    @TaskAction
    fun execute() {
        val androidProjects = allDependencyAndroidProjects()
        //1、遍历res下的xml文件，找到自定义的类(View/Fragment/四大组件等)，并将混淆结果同步到xml文件内
        androidProjects.forEach { project ->
            val xmlDirs = project.resDirs().flatMapTo(ArrayList()) { dir ->
                dir.listFiles { _, name ->
                    //过滤res目录下的layout、navigation目录
                    name.startsWith("values")
                }?.toList() ?: emptyList()
            }
            project.files(xmlDirs).asFileTree.forEach { xmlFile ->
                if (xmlFile.name.startsWith("strings")) {
                    var xmlText = xmlFile.readText()
                    val stringMap = hashMapOf<String, String>()
                    val rootNode = XmlParser(false, false).parseText(xmlText)
                    for (children in rootNode.children()) {
                        val childNode = children as? Node ?: continue
                        var keyString = childNode.attribute("name").toString()
                        var valueString = children.value().toString()
                        try {
                            String.format(Locale.US, valueString, 1, 2, 3)
                        } catch (e: Exception) {
                            println("发生错误:文件:${xmlFile.parentFile.name} ${keyString}")
                        }

//                    val childName = childNode.name()
//                    if ("fragment" == childName) {
//                        val classPath = childNode.attribute("android:name").toString()
//                        classPaths.add(classPath)
//                    }
                    }
                }

            }
        }
    }
}
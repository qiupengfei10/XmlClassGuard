package com.xml.guard

import com.android.build.gradle.AppExtension
import com.google.gson.Gson
import com.xml.guard.entensions.GuardExtension
import com.xml.guard.model.aabResGuard
import com.xml.guard.model.andResGuard
import com.xml.guard.tasks.CheckStringTask
import com.xml.guard.tasks.EmojiGuardTask
import com.xml.guard.tasks.FindConstraintReferencedIdsTask
import com.xml.guard.tasks.MoveDirTask
import com.xml.guard.tasks.PackageChangeTask
import com.xml.guard.tasks.RawGuardTask
import com.xml.guard.tasks.XmlClassGuardTask
import com.xml.guard.utils.AgpVersion
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * User: ljx
 * Date: 2022/2/25
 * Time: 19:03
 */
class XmlClassGuardPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        checkApplicationPlugin(project)
        println("XmlClassGuard version is $version, agpVersion=${AgpVersion.agpVersion}")
        val guardExtension = project.extensions.create("xmlClassGuard", GuardExtension::class.java)
//        println("XmlClassGuard guardExtension:${Gson().toJson(guardExtension)}")
        project.tasks.create("xmlClassGuard", XmlClassGuardTask::class.java, guardExtension)
        project.tasks.create("packageChange", PackageChangeTask::class.java, guardExtension)
        project.tasks.create("moveDir", MoveDirTask::class.java, guardExtension)
        project.tasks.create("checkString", CheckStringTask::class.java, guardExtension)
        project.tasks.create("emojiGuard", EmojiGuardTask::class.java, guardExtension)
        project.tasks.create("rawGuard", RawGuardTask::class.java, guardExtension)

        val android = project.extensions.getByName("android") as AppExtension
        project.afterEvaluate {
            android.applicationVariants.all { variant ->
                val variantName = variant.name.capitalize()
                if (guardExtension.findAndConstraintReferencedIds) {
                    createAndFindConstraintReferencedIds(project, variantName)
                }
                if (guardExtension.findAabConstraintReferencedIds) {
                    createAabFindConstraintReferencedIds(project, variantName)
                }
            }
        }
    }

    private fun createAndFindConstraintReferencedIds(
        project: Project,
        variantName: String
    ) {
        val andResGuardTaskName = "resguard$variantName"
        val andResGuardTask = project.tasks.findByName(andResGuardTaskName)
            ?: throw GradleException("AndResGuard plugin required")
        val findConstraintReferencedIdsTaskName = "andFindConstraintReferencedIds"
        val findConstraintReferencedIdsTask =
            project.tasks.findByName(findConstraintReferencedIdsTaskName)
                ?: project.tasks.create(
                    findConstraintReferencedIdsTaskName,
                    FindConstraintReferencedIdsTask::class.java,
                    andResGuard
                )
        andResGuardTask.dependsOn(findConstraintReferencedIdsTask)
    }

    private fun createAabFindConstraintReferencedIds(
        project: Project,
        variantName: String
    ) {
        val aabResGuardTaskName = "aabresguard$variantName"
        val aabResGuardTask = project.tasks.findByName(aabResGuardTaskName)
            ?: throw GradleException("AabResGuard plugin required")
        val findConstraintReferencedIdsTaskName = "aabFindConstraintReferencedIds"
        val findConstraintReferencedIdsTask =
            project.tasks.findByName(findConstraintReferencedIdsTaskName)
                ?: project.tasks.create(
                    findConstraintReferencedIdsTaskName,
                    FindConstraintReferencedIdsTask::class.java,
                    aabResGuard
                )
        aabResGuardTask.dependsOn(findConstraintReferencedIdsTask)
    }

    private fun checkApplicationPlugin(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("Android Application plugin required")
        }
    }
}
package im.wangchao.build.task

import com.meituan.android.walle.ChannelWriter
import im.wangchao.build.extension.BuildExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
/**
 * <p>Description  : ClearChannel.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/11.</p>
 * <p>Time         : 下午2:29.</p>
 */
class CleanChannel extends DefaultTask{
    @Input public Project targetProject

    CleanChannel(){
        setDescription("Clean Apk channel info.")
        setGroup("MV2Build")
    }

    @TaskAction void cleanChannel(){
        BuildExtension buildExtension = BuildExtension.getBuildExtension(project)

        def apkFile = buildExtension.clearChannelApkFile

        if (apkFile == null || !apkFile.exists()){
            throw new GradleException("${apkFile} is not existed.")
        }

        ChannelWriter.remove(apkFile)
    }
}

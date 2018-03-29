package im.wangchao.build.task

import com.meituan.android.walle.ChannelInfo
import com.meituan.android.walle.ChannelReader
import im.wangchao.build.extension.BuildExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * <p>Description  : PrintChannelInfo.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2018/3/29.</p>
 * <p>Time         : 下午1:41.</p>
 */
class PrintChannelInfo extends DefaultTask{
    @Input public Project targetProject

    PrintChannelInfo(){
        setDescription("Print Apk channel info.")
        setGroup("MV2Build")
    }

    @TaskAction printChannelInfo(){
        BuildExtension buildExtension = BuildExtension.getBuildExtension(project)

        def apkFile = buildExtension.printChannelApkFile
        if (apkFile == null || !apkFile.exists()){
            throw new GradleException("${apkFile} is not existed.")
        }

        ChannelInfo channelInfo = ChannelReader.get(apkFile)
        if (channelInfo == null){
            targetProject.logger.error("[${apkFile.getName()}] 未写入 Channel Info.")
        } else {
            targetProject.logger.error(
                    "================ [${apkFile.getName()}] ================\n" +
                    "channel info: \n" +
                    "    channel -> ${channelInfo.channel}\n" +
                    "    extraInfo -> ${channelInfo.extraInfo}\n" +
                    "========================================================")
        }

    }
}

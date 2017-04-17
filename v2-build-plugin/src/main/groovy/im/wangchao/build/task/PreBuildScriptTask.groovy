package im.wangchao.build.task

import im.wangchao.build.extension.BuildExtension
import im.wangchao.build.utils.CommandUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
/**
 * <p>Description  : ExecutePreBuildScript.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/13.</p>
 * <p>Time         : 下午3:06.</p>
 */
class PreBuildScriptTask extends DefaultTask{
    @Input public Project targetProject

    PreBuildScriptTask(){
        setDescription("exec all pre-build script.")
        setGroup("MV2Build")
    }

    @TaskAction void exec(){
        BuildExtension buildExtension = BuildExtension.getBuildExtension(project)

        def preBuildScript = buildExtension.preBuildScript

        if (preBuildScript == null || preBuildScript.length == 0){
            targetProject.logger.error("preBuildScript is null or length == 0.")
            return
        }

        preBuildScript.each { scriptPatch ->
            try {
                CommandUtils.exec(scriptPatch)
            } catch(Exception e){
                throw new GradleException("执行脚本${scriptPatch}失败,原因如下:\n${e.getMessage()}")
            }
        }

    }
}

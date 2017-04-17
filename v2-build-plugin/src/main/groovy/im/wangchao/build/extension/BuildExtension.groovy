package im.wangchao.build.extension

import org.gradle.api.Project

/**
 * <p>Description  : BuildExtension.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/1.</p>
 * <p>Time         : 下午4:21.</p>
 */
class BuildExtension {

    /**
     * build 配置文件
     */
    File configFile

    /**
     * 需要清除渠道的 APK。如果配置该项，那么会有 clearChannel Task 提供使用，
     * 该 Task 会请空指定 APK 的渠道信息。
     */
    File clearChannelApkFile

    /**
     * 在打包前，需要最先执行的脚本，执行顺序由数组顺序决定。
     * 数组的值为需要执行脚本的绝对路径。
     */
    String[] preBuildScript

    BuildExtension(){

    }

    public static BuildExtension getBuildExtension(Project project){
        BuildExtension buildExtension = project.getExtensions().findByType(BuildExtension)
        if (buildExtension == null){
            buildExtension = new BuildExtension()
        }

        return buildExtension
    }
}

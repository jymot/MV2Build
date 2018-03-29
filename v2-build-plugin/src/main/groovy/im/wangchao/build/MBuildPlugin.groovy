package im.wangchao.build

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SigningConfig
import im.wangchao.build.extension.ChannelExtension
import im.wangchao.build.extension.BuildExtension
import im.wangchao.build.task.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>Description  : MBuildPlugin。
 *
 *                   执行 assembleReleaseChannel 任务打包时，task 执行顺序如下：
 *                    preBuildScript ->  modifyChannelManifest -> assemble -> assembleReleaseChannel
 *
 *                   </p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/1.</p>
 * <p>Time         : 下午2:56.</p>
 */
class MBuildPlugin implements Plugin<Project>{
    private static final String DEFAULT_BUILD_TYPE = "release"

    @Override void apply(Project project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw new GradleException("Plugin requires the 'com.android.application' plugin to be configured.", null)
        }

        project.extensions.create('mV2Build', BuildExtension)
        /**
         * Multi-Channel 相关配置
         */
        project.mV2Build.extensions.create('channel', ChannelExtension)

        project.afterEvaluate {

            String multiChannelBuildType = project.mV2Build.channel.buildType
            if (multiChannelBuildType == null || multiChannelBuildType.length() == 0){
                multiChannelBuildType = DEFAULT_BUILD_TYPE
            }

            /**
             * 1. Pre-Build Script Task
             */
            PreBuildScriptTask preBuildScriptTask = project.tasks.create(Constants.TASK_PRE_BUILD, PreBuildScriptTask)
            preBuildScriptTask.targetProject = project

            project.android.applicationVariants.all { variant ->
                def variantName = variant.name.capitalize()

                if (!isV2SignatureSchemeEnabled(variant)) {
                    project.logger.error("MBuildPlugin requires 'APK Signature Scheme v2 Enabled' for ${variant.name}.")
                }

                /**
                 * 2.onlyWriteChannel
                 */
                if (project.mV2Build.channel != null
                        && project.mV2Build.channel.writeChannelApkFile != null
                        && project.mV2Build.channel.writeChannelApkFile instanceof File
                        && variantName != null
                        && variantName.equalsIgnoreCase("release")){
                    OnlyWriteChannel writeChannel = project.tasks.create(Constants.TASK_ONLY_WRITE_CHANNEL, OnlyWriteChannel)
                    writeChannel.targetProject = project
                    writeChannel.variant = variant
                }

                /**
                 * 3.cleanChannelInfo
                 */
                if (project.mV2Build.clearChannelApkFile != null
                        && project.mV2Build.clearChannelApkFile instanceof File
                        && variantName != null
                        && variantName.equalsIgnoreCase("release")){
                    CleanChannel clearChannel = project.tasks.create(Constants.TASK_CLEAN_CHANNEL_INFO, CleanChannel)
                    clearChannel.targetProject = project
                }

                /**
                 * 4.printChannelInfo
                 */
                if (project.mV2Build.printChannelApkFile != null
                        && project.mV2Build.printChannelApkFile instanceof File
                        && variantName != null
                        && variantName.equalsIgnoreCase("release")){
                    PrintChannelInfo printChannelInfo = project.tasks.create(Constants.TASK_PRINT_CHANNEL_INFO, PrintChannelInfo)
                    printChannelInfo.targetProject = project
                }

                if (variantName != null){
                    /**
                     * 创建 Multi Channel 相关 Task
                     */
                    if (variantName.equalsIgnoreCase(multiChannelBuildType)){
                        project.logger.error("=======>>> assembleReleaseChannel dependsOn ${multiChannelBuildType} =======")

                        /**
                         * 5. create assembleReleaseChannel
                         */
                        AssembleReleaseChannel assembleReleaseChannel = project.tasks.create(Constants.TASK_ASSEMBLE_RELEASE_CHANNEL, AssembleReleaseChannel)
                        assembleReleaseChannel.targetProject = project
                        assembleReleaseChannel.variant = variant
                        assembleReleaseChannel.dependsOn variant.assemble
                        assembleReleaseChannel.dependsOn preBuildScriptTask
                        variant.assemble.mustRunAfter preBuildScriptTask

                        /**
                         * 6. 修改 Channel 中 AndroidManifest.xml 文件 Task
                         */
                        ModifyManifestTask channelManifestTask = project.tasks.create(Constants.TASK_MODIFY_CHANNEL_MANIFEST, ModifyManifestTask)
                        // 默认打包时，优先执行 multiServerManifestTask
                        channelManifestTask.targetProject = project
                        assembleReleaseChannel.dependsOn channelManifestTask
                        variant.assemble.mustRunAfter channelManifestTask
                        // channelManifestTask 任务必须在 preBuildScriptTask 后执行
                        channelManifestTask.mustRunAfter preBuildScriptTask

                    }


                }
            }
        }

    }

    private static SigningConfig getSigningConfig(BaseVariant variant) {
        return variant.buildType.signingConfig == null ? variant.mergedFlavor.signingConfig : variant.buildType.signingConfig
    }

    private static boolean isV2SignatureSchemeEnabled(BaseVariant variant) throws GradleException {
        def signingConfig = getSigningConfig(variant)
        if (signingConfig == null || !signingConfig.isSigningReady()) {
            return false
        }

        // check whether APK Signature Scheme v2 is enabled.
        return signingConfig.hasProperty("v2SigningEnabled") && signingConfig.v2SigningEnabled
    }

    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     *         The result is a positive integer if str1 is _numerically_ greater than str2.
     *         The result is zero if the strings are _numerically_ equal.
     */
    private static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("-")[0].split("\\.")
        String[] vals2 = str2.split("-")[0].split("\\.")
        int i = 0
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++
        }

        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]))
            return Integer.signum(diff)
        }

        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else {
            return Integer.signum(vals1.length - vals2.length)
        }
    }
}

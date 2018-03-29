package im.wangchao.build.task

import com.android.build.gradle.api.BaseVariant
import com.google.gson.Gson
import com.meituan.android.walle.ChannelWriter
import groovy.text.SimpleTemplateEngine
import im.wangchao.build.MBuildConfig
import im.wangchao.build.extension.BuildExtension
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat
/**
 * <p>Description  : OnlyWriteChannel.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/11.</p>
 * <p>Time         : 上午8:43.</p>
 */
class OnlyWriteChannel extends DefaultTask {
    @Input public BaseVariant variant
    @Input public Project targetProject

    OnlyWriteChannel() {
        setDescription("Write channel to Apk.")
        setGroup("MV2Build")
    }

    @TaskAction void writeChannel() {
        BuildExtension buildExtension = BuildExtension.getBuildExtension(project)

        long startTime = System.currentTimeMillis()

        def channelExtension = buildExtension.channel
        if (channelExtension == null){
            throw new GradleException("channel配置为null")
        }

        def apkFile = channelExtension.writeChannelApkFile

        if (apkFile == null || !apkFile.exists()) {
            throw new GradleException("${apkFile} is not existed.")
        }

        Utils.checkV2Signature(apkFile)

        File outputFolder = apkFile.parentFile
        if (channelExtension.outputDir instanceof File) {
            outputFolder = channelExtension.outputDir
            if (!outputFolder.parentFile.exists()) {
                outputFolder.parentFile.mkdirs()
            }
        }

        def nameVariantMap = [
                'appName'    : targetProject.name,
                'projectName': targetProject.rootProject.name,
                'buildType'  : variant.buildType.name,
                'versionName': variant.versionName,
                'versionCode': variant.versionCode,
                'packageName': variant.applicationId,
                'flavorName' : variant.flavorName
        ]


        File configFile = buildExtension.configFile
        if (configFile == null || !configFile.exists()) {
            throw new GradleException("打包配置文件不存在，请查看配置中 configFile 是否正确。")
        }

        MBuildConfig config = new Gson().fromJson(new FileReader(configFile), MBuildConfig.class)

        def channel = config.getChannel()
        if (channel == null) {
            throw new GradleException("configFile 配置文件中，未配置 channel 信息")
        }

        def commonExtraInfo = new HashMap<String, String>()
        // 如果公钥不为空，那么打包时会注入完整性校验信息
        def pk = buildExtension.publicKey
        if (Utils.checkPublicKeyEnable(pk)){
            commonExtraInfo.put(Utils.INTEGRITY_KEY, Utils.getIntegrityInfo(apkFile, outputFolder, pk, targetProject))
        }

        def increment = channel.isIncrement()
        def list = new ArrayList<>()
        if (increment){
            int incrementCount = channel.getIncrementCount()

            for (int i = 0; i < incrementCount; i++){
                MBuildConfig.ChannelListInfo info = new MBuildConfig.ChannelListInfo()
                info.channel = "${i}"
                info.alias = "${i}"
                list.add(info)
            }
        } else {
            def tempChannelList = channel.getList()
            if (tempChannelList == null || tempChannelList.size() == 0){
                throw new GradleException("configFile 配置文件 channel 中 list 配置为空")
            }
            // channel 中的 list
            list.addAll(tempChannelList)
        }

        list.each { channelItem ->
            def innerExtraInfo = new HashMap<String, String>(commonExtraInfo)
            if (channelItem.extraInfo != null){
                // 如果每一项单独配置了额外的信息，那么需要添加到 extraInfo 中
                innerExtraInfo.putAll(channelItem.extraInfo)
            }
            innerExtraInfo.put(Utils.ALIAS_KEY, channelItem.alias)

            generateChannelApk(apkFile, outputFolder, channelItem.channel, innerExtraInfo, channelItem.alias, nameVariantMap)
        }

        targetProject.logger.lifecycle("APK Signature Scheme v2 Multi Channel takes about " + (
                System.currentTimeMillis() - startTime) + " milliseconds")
    }

    def generateChannelApk(File apkFile, File outputFolder, channel, extraInfo, channelName, nameVariantMap) {
        BuildExtension extension = BuildExtension.getBuildExtension(project)

        def buildTime = new SimpleDateFormat('yyyyMMdd-HHmmss').format(new Date())

        String fileName = apkFile.getName()
        if (fileName.endsWith(Utils.DOT_APK)) {
            fileName = fileName.substring(0, fileName.lastIndexOf(Utils.DOT_APK))
        }

        String apkFileName = "${fileName}-${channelName}${Utils.DOT_APK}"

        File channelApkFile = new File(outputFolder, apkFileName)
        FileUtils.copyFile(apkFile, channelApkFile)

        ChannelWriter.put(channelApkFile, channel, extraInfo)

        nameVariantMap.put("buildTime", buildTime)
        nameVariantMap.put('channel', channelName)
        nameVariantMap.put('fileSHA1', Utils.getFileHash(channelApkFile))
        if (extension.channel != null
                && extension.channel.apkFileNameFormat != null
                && extension.channel.apkFileNameFormat.length() > 0){
            def newApkFileName = new SimpleTemplateEngine().createTemplate(extension.channel.apkFileNameFormat).make(nameVariantMap).toString()
            if (!newApkFileName.contentEquals(apkFileName)) {
                FileUtils.copyFile(channelApkFile, new File(outputFolder, newApkFileName))
                channelApkFile.delete();
            }
        }
    }
}

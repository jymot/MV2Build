package im.wangchao.build.extension

/**
 * <p>Description  : ChannelExtension.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/14.</p>
 * <p>Time         : 上午10:39.</p>
 */
class ChannelExtension {

    /**
     * Multi-channel Apk 导出目录
     */
    File outputDir

    /**
     * file name template string
     *
     * Available vars:
     * 1. projectName
     * 2. appName
     * 3. packageName
     * 4. buildType
     * 5. channel
     * 6. versionName
     * 7. versionCode
     * 8. buildTime
     * 9. fileSHA1
     * 10. flavorName
     *
     * default value: '${appName}-${buildType}-${channel}.apk'
     *
     */
    String apkFileNameFormat

    /**
     * 需要写入渠道的 APK。如果配置该项，那么会有 onlyWriteChannel Task 提供使用，
     * 该 Task 只是将已经打好的包根据配置文件，写入渠道。
     */
    File writeChannelApkFile

    /**
     * AndroidManifest.xml 文件
     */
    File manifestFile

    /**
     * Build Type 默认 release，可以自己设定不同的 Build Type
     */
    String buildType = "release"
}

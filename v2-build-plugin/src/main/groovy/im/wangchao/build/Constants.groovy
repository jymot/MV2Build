package im.wangchao.build

/**
 * <p>Description  : Constants.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 2018/3/29.</p>
 * <p>Time         : 下午1:49.</p>
 */
class Constants {

    // 执行脚本任务
    public static final String TASK_PRE_BUILD = "preBuildScript"
    // 仅写入 channel info 任务
    public static final String TASK_ONLY_WRITE_CHANNEL = "onlyWriteChannel"
    // 清空 channel info 任务
    public static final String TASK_CLEAN_CHANNEL_INFO = "cleanChannelInfo"
    // 打印 channel info 任务
    public static final String TASK_PRINT_CHANNEL_INFO = "printChannelInfo"
    // 打包
    public static final String TASK_ASSEMBLE_RELEASE_CHANNEL = "assembleReleaseChannel"
    // 修改 AndroidManifest 内容任务
    public static final String TASK_MODIFY_CHANNEL_MANIFEST = "modifyChannelManifest"
}

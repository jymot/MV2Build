package im.wangchao.buildhelper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.ChannelReader;

import java.io.File;
import java.util.Map;

/**
 * <p>Description  : ApkInfoReader.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/5.</p>
 * <p>Time         : 下午3:41.</p>
 */
public class ApkInfoReader {
    private ApkInfoReader(){}

    /**
     * get alias
     *
     * @param context context
     * @return alias
     */
    public static String getAlias(final Context context){
        return getAlias(context, null);
    }

    /**
     * get alias
     *
     * @param context context
     * @param defaultAlias default alias , if not fount
     * @return alias
     */
    public static String getAlias(final Context context, final String defaultAlias){
        final ChannelInfo channelInfo = getChannelInfo(context);
        if (channelInfo == null) {
            return defaultAlias;
        }

        return channelInfo.getExtraInfo().get("alias");
    }


    /**
     * get channel
     *
     * @param context context
     * @return channel, null if not fount
     */
    public static String getChannel(final Context context) {
        return getChannel(context, null);
    }

    /**
     * get channel or default
     *
     * @param context context
     * @param defaultChannel default channel
     * @return channel, default if not fount
     */
    public static String getChannel(final Context context, final String defaultChannel) {
        final ChannelInfo channelInfo = getChannelInfo(context);
        if (channelInfo == null) {
            return defaultChannel;
        }
        return channelInfo.getChannel();
    }

    /**
     * get channel info (include channle and extraInfo)
     *
     * @param context context
     * @return channel info
     */
    public static ChannelInfo getChannelInfo(final Context context) {
        final String apkPath = getApkPath(context);
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        return ChannelReader.get(new File(apkPath));
    }

    /**
     * get value by key
     *
     * @param context context
     * @param key     the key you store
     * @return value
     */
    public static String get(final Context context, final String key) {
        final Map<String, String> channelMap = getChannelInfoMap(context);
        if (channelMap == null) {
            return null;
        }
        return channelMap.get(key);
    }

    /**
     * get all channl info with map
     *
     * @param context context
     * @return map
     */
    public static Map<String, String> getChannelInfoMap(final Context context) {
        final String apkPath = getApkPath(context);
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        return ChannelReader.getMap(new File(apkPath));
    }

    private static String getApkPath(final Context context) {
        String apkPath = null;
        try {
            final ApplicationInfo applicationInfo = context.getApplicationInfo();
            if (applicationInfo == null) {
                return null;
            }
            apkPath = applicationInfo.sourceDir;
        } catch (Throwable e) {
            // Silent
        }
        return apkPath;
    }
}

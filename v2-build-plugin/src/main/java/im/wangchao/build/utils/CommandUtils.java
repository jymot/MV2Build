package im.wangchao.build.utils;

import java.io.InputStream;

/**
 * <p>Description  : CommandUtils.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/13.</p>
 * <p>Time         : 下午3:00.</p>
 */
public class CommandUtils {
    private CommandUtils() {}

    public static String exec(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        InputStream errorStream = process.getErrorStream();
        byte[] buffer = new byte[1024];
        int readBytes;
        StringBuilder stringBuilder = new StringBuilder();
        while ((readBytes = errorStream.read(buffer)) > 0) {
            stringBuilder.append(new String(buffer, 0, readBytes));
        }
        return stringBuilder.toString();
    }
}

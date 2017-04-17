package im.wangchao.build.task

import com.android.apksigner.core.ApkVerifier
import com.android.apksigner.core.internal.util.ByteBufferDataSource
import com.android.apksigner.core.util.DataSource
import com.google.common.base.Charsets
import com.google.common.hash.HashCode
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import com.google.common.io.Files
import org.apache.commons.io.IOUtils
import org.gradle.api.GradleException

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
/**
 * <p>Description  : Utils.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/5.</p>
 * <p>Time         : 上午10:57.</p>
 */
class Utils {
    public static final String DEFAULT_CHANNEL = "100"
    public static final String DOT_APK = ".apk"
    public static final String URL_KEY = "url"
    public static final String ALIAS_KEY = "alias"
    public static final String DEBUG_KEY = "debug"
    public static final String INTEGRITY_KEY = "integrity"

    public static void checkV2Signature(File apkFile) {
        FileInputStream fIn
        FileChannel fChan
        try {
            fIn = new FileInputStream(apkFile)
            fChan = fIn.getChannel()
            long fSize = fChan.size()
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fSize)
            fChan.read(byteBuffer)
            byteBuffer.rewind()

            DataSource dataSource = new ByteBufferDataSource(byteBuffer)

            ApkVerifier apkVerifier = new ApkVerifier()
            ApkVerifier.Result result = apkVerifier.verify(dataSource, 0)
            if (!result.verified || !result.verifiedUsingV2Scheme) {
                throw new GradleException("${apkFile} has no v2 signature in Apk Signing Block!")
            }
        } catch (IOException ignore) {
            ignore.printStackTrace()
        } finally {
            IOUtils.closeQuietly(fChan)
            IOUtils.closeQuietly(fIn)
        }
    }

    public static String getFileHash(File file) throws IOException {
        HashCode hashCode
        HashFunction hashFunction = Hashing.sha1()
        if (file.isDirectory()) {
            hashCode = hashFunction.hashString(file.getPath(), Charsets.UTF_16LE)
        } else {
            hashCode = Files.hash(file, hashFunction)
        }
        return hashCode.toString()
    }

}

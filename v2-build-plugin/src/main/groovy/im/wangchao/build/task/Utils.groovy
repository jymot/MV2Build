package im.wangchao.build.task

import com.android.apksigner.core.ApkVerifier
import com.android.apksigner.core.internal.util.ByteBufferDataSource
import com.android.apksigner.core.util.DataSource
import com.google.common.base.Charsets
import com.google.common.hash.HashCode
import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import com.google.common.io.Files
import im.wangchao.build.utils.FileUtils
import im.wangchao.build.utils.ZipUtils
import im.wangchao.build.utils.encrypt.DigestUtils
import im.wangchao.build.utils.encrypt.RSAEncryptUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.PublicKey

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


    public static boolean checkPublicKeyEnable(publicKey){
        if (publicKey == null){
            return false
        }

        if (publicKey instanceof String && !publicKey.isEmpty()){
            return true
        }

        if (publicKey instanceof File && publicKey.exists()){
            return true
        }

        return false
    }

    public static String getIntegrityInfo(File apk, File outputFolder, pk, Project project){
        String integrityFileName = "integrity.zip"

        File integrityFile = new File(outputFolder, integrityFileName)
        FileUtils.copyFile(apk, integrityFile)

        File tempCheckFile = new File(outputFolder, "temp${System.currentTimeMillis()}")
        String tempCheckPath = tempCheckFile.getCanonicalPath()
        ZipUtils.unZip(integrityFile, tempCheckPath)

        File manifestFile = new File("${tempCheckPath}/META-INF/MANIFEST.MF")
        String digest = DigestUtils.md5(manifestFile)

        // 删除 zip 文件
        FileUtils.deleteFile(integrityFile)
        // 删除临时目录
        FileUtils.deleteFile(tempCheckFile)

        String pkValue
        if (pk instanceof String){
            pkValue = pk
        } else if (pk instanceof File){
            pkValue = FileUtils.readUtf8(pk)
        }

        if (pkValue == null){
            return ""
        }
        pkValue = publicKeyFormat(pkValue)
        project.logger.error("pkValue: ${pkValue}")

        // 加密 MANIFEST.MF 文件摘要
        PublicKey pKey = RSAEncryptUtils.loadPublicKey(pkValue)
        byte[] encryptData = RSAEncryptUtils.encryptData(digest.getBytes("UTF-8"), pKey)

        if (encryptData == null){
            return ""
        }
        String resultData = new String(Base64.encode(encryptData, Base64.NO_WRAP))

        project.logger.error("encrypt data: ${resultData}")

        return resultData
    }

    private static String publicKeyFormat(String pk){
        return pk.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----","")
                .replace("\n","").replace("\r", "").replace("\t","").trim()
    }
}

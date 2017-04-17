package im.wangchao.build.task

import com.google.gson.Gson
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import im.wangchao.build.MBuildConfig
import im.wangchao.build.extension.BuildExtension
import im.wangchao.build.utils.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.text.SimpleDateFormat

/**
 * <p>Description  : ModifyManifestTask.</p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/6.</p>
 * <p>Time         : 下午2:15.</p>
 */
class ModifyManifestTask extends DefaultTask{
    @Input public Project targetProject

    ModifyManifestTask() {
        setDescription("modify manifest meta-data value")
        setGroup("MV2Build")
    }

    /*
        <manifest package="com.lakala.lakalabuildapp"
          xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <meta-data
            android:name="Test"
            android:value="${BUILD_TIME_VALUE}" />

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>

    </manifest>
     */
    @TaskAction void modify() {
        BuildExtension buildExtension = BuildExtension.getBuildExtension(project)

        File configFile = buildExtension.configFile
        if (configFile == null || !configFile.exists()){
            throw new GradleException("打包配置文件不存在，请查看配置中 configFile 是否正确。")
        }

        MBuildConfig config = new Gson().fromJson(new FileReader(configFile), MBuildConfig.class)
        def metaList = config.getManifest()

        if (metaList == null || metaList.size() == 0){
            // 如果没有配置，或者配置列表长度为0，那么不修改文件。
            return
        }

        File manifestFile = buildExtension.channel?.manifestFile

        if (manifestFile == null || !manifestFile.exists()){
            targetProject.logger.error("manifestFile is null / not exist, please check it.")
            return
        }

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance()
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(manifestFile)
            NodeList list = document.getElementsByTagName("meta-data")

            boolean needChangeXml = false
            metaList.each { item ->
                String key = item.key
                list.each { node ->
                    Element element = (Element)node
                    String name = element.getAttribute("android:name")
                    if (StringUtils.equals(name, key)){
                        String itemValue = item.value

                        if (itemValue != null && !itemValue.isEmpty()){
                            if (itemValue.contains("{date}")){
                                def buildTime = new SimpleDateFormat('yyyyMMddHHmm').format(new Date())
                                itemValue = itemValue.replace("{date}", buildTime)
                            }

                            if (!needChangeXml){
                                needChangeXml = true
                            }
                            element.setAttribute("android:value", itemValue)
                        }

                    }
                }
            }

            if (!needChangeXml){
                return
            }

            document.setXmlStandalone(true)

            TransformerFactory tff = TransformerFactory.newInstance()

            Transformer transformer = tff.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes")
            //通过Transformer类的方法 transform(Source xmlSource, Result outputTarget)
            //将 XML Source 转换为 Result。
            StreamResult result = new StreamResult()

            result.setOutputStream(new FileOutputStream(manifestFile))

            transformer.transform(new DOMSource(document), result)

        } catch (Exception e){
            throw new GradleException("修改AndroidManifest.xml失败，原因：${e.getMessage()}")
        }


    }

    /*
      def xml = new XmlSlurper()
        xml.setKeepIgnorableWhitespace(false)

        GPathResult manifest = xml.parse(manifestFile).declareNamespace("android": "http://schemas.android.com/apk/res/android")
        def metadata = manifest.application.'meta-data'

        metaList.each { item ->
            def meta = metadata.find { meta ->
                return metadata.@'android:name'.toString() == item.key
            }

            if (meta.size() > 0){
                // 替换数据
                String itemValue = item.value
                if (itemValue != null && !itemValue.isEmpty()){
                    if (itemValue.contains("YYYYMMDD")){
                        def buildTime = new SimpleDateFormat('yyyyMMdd').format(new Date())
                        itemValue = itemValue.replace("YYYYMMDD", buildTime)
                    }

                    meta.replaceNode {
                        'meta-data'('android:name': meta."@android:name", "android:value": itemValue) {}
                    }
                }

            }
        }

        // save
        XmlUtil.serialize(manifest, new FileWriter(manifestFile))
//        serializeXml(manifest, manifestFile)
     */

    /**
     * write xml to file
     * @param xml xml
     * @param file file
     */
    static void serializeXml(xml, file) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml },
                new FileWriter(file))
    }
}

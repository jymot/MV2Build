# MV2Build
打包使用 V2 签名，使用前请确保设置 V2。

### Gradle:
Add MV2Build as a dependency in your main build.gradle in the root of your project:
```gradle
buildscript {
    dependencies {
        classpath 'im.wangchao:v2-build-plugin:0.1.2'
    }
}
```

```gradle
compile 'im.wangchao:build-helper:0.1.2'
```

### 如何使用
在主工程`build.gradle`文件中添加如下代码：
```gradle
apply plugin: 'im.wangchao.build'

mV2Build{

    /**
     * 渠道配置文件
     */
    configFile = new File("${project.getProjectDir()}/mV2Build/config.json")


    /**
    * 需要清除渠道的 APK。如果配置该项，那么会有 clearChannel Task 提供使用，
    * 该 Task 会请空指定 APK 的渠道信息。
    */
    clearChannelApkFile = new File("${project.buildDir}/outputs/channel/app-release-0.apk")

    /**
     * 在打包前，需要最先执行的脚本，执行顺序由数组顺序决定。
     * 数组的值为需要执行脚本的绝对路径。
     * 注意脚本中，如果使用相对路径，那么该相对路径是相对于项目根目录的，也就是${project.getRootDir()}
     */
    preBuildScript = []

    /**
     * Multi-Channel
     */
    channel {
        /**
         * Multi-Channel 输出目录
         */
        outputDir = new File("${project.buildDir}/outputs/channel")
        /**
         * 执行 assembleReleaseChannel Task 输出 APK 的文件格式，可以不设置，默认为${appName}-release-${channel}
         */
//    apkFileNameFormat = '${appName}-release-${channel}-test.apk'
        /**
         * 需要写入渠道的 APK。如果配置该项，那么会有 onlyWriteChannel Task 提供使用，
         * 该 Task 只是将已经打好的包根据配置文件，写入渠道。
         */
        writeChannelApkFile = new File("${project.buildDir}/outputs/channel/app-release-0.apk")
        /**
         * AndroidManifest.xml 文件
         */
        manifestFile = new File("${project.getProjectDir()}/src/main/AndroidManifest.xml")
        /**
         * 依赖的变种类型，可以不设置，默认为 release
         */
//        buildType = "release"
    }

}
```

然后需要创建一个渠道包的配置文件，也就是上面要用到的渠道配置文件，比如创建一个`config.json`:
```json
{
  "manifest": [
    {
      "key": "Test",
      "value": "V{date}"
    }
  ],
  "channel": {
    "increment": false,
    "incrementCount": 10,
    "list":[
      {
        "alias": "测试渠道",
        "channel": "test",
        "extraInfo": {}
      }
    ]
  }
}
```

 1. **manifest**
    配置该配置，会修改**lklBuild**中配置的`manifestFile`文件中的`<meta-data>`节点，依据配置的`key-value`在`<meta-data>`节点中查找`android:name`等于`key`的节点，并将其`android:value`设置为`value`。注意，如果`value`中包含`{date}`，会被替换为`yyyyMMdd-HHmmss`的时间格式化。
 2. **channel**
    该配置用于**assembleReleaseChannel**任务，该任务主要用于打发布的渠道包。

    * `properties` 同上，和**multiServer**中的配置含义相同
    * `increment` 是否打递增渠道包，也就是说渠道号从0开始递增
    * `incrementCount` 递增渠道包的数量
    * `list` 如果不打递增渠道包，那么打特殊渠道包，每一个item为一个渠道包
     * `alias` 渠道别名
     * `channel` 渠道号
     * `extraInfo` 额外的配置

### 如何获取渠道信息
获取渠道信息：
```java
String channel = ApkInfoReader.getChannel(getApplicationContext());
```
获取别名：
```java
String alias = ApkInfoReader.getAlias(getApplicationContext());
```
获取`extraInfo`中的信息:
```java
 ChannelInfo info = ApkInfoReader.getChannelInfo(App.instance().getApplicationContext());

String result = info.getExtraInfo().get("自定义的key");

```
### 打包
可以直接执行如下命令：
```shell
# 渠道发布包
gradle assembleReleaseChannel
```
如果不想执行命令，那么在**Android Studio**界面右侧，点击`Gradle`，然后选择你的主工程，然后找到`mv2build`目录，执行相应的任务就可以了。

### 参考致谢
 * [walle](https://github.com/Meituan-Dianping/walle)

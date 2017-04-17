package im.wangchao.build;

import java.util.List;
import java.util.Map;

/**
 * <p>Description  : LKLBuildConfig.
 *                  {
 *                      "manifest": [
 *                          {
 *                              "key":"meta key",
 *                              "value": "meta value"
 *                          }
 *                      ],
 *                      "channel": {
 *                          "increment": false,  如果为 true , 那么会打索引从0开始递增的渠道(alias和channel相等都为索引)，一共打 incrementCount 个。
 *                          "incrementCount": 0,
 *                          "list":[
 *                              {
 *                                  "alias": "渠道别名，如360，应用宝等",
 *                                  "channel": "对应的渠道号",
 *                                  "extraInfo": {}
 *                              }
 *                          ]
 *                      }
 *                  }
 *                  </p>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 17/4/1.</p>
 * <p>Time         : 下午5:07.</p>
 */
public class MBuildConfig {
    private List<ManifestItem> manifest;
    private Channel channel;

    public List<ManifestItem> getManifest() {
        return manifest;
    }

    public void setManifest(List<ManifestItem> manifest) {
        this.manifest = manifest;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static class ManifestItem {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Channel {
        private boolean increment;
        private int incrementCount;
        private List<ChannelListInfo> list;

        public boolean isIncrement() {
            return increment;
        }

        public void setIncrement(boolean increment) {
            this.increment = increment;
        }

        public int getIncrementCount() {
            return incrementCount;
        }

        public void setIncrementCount(int incrementCount) {
            this.incrementCount = incrementCount;
        }

        public List<ChannelListInfo> getList() {
            return list;
        }

        public void setList(List<ChannelListInfo> list) {
            this.list = list;
        }

    }

    public static class ChannelListInfo{
        private String channel;
        private String alias;
        private Map<String, String> extraInfo;

        public Map<String, String> getExtraInfo() {
            return extraInfo;
        }

        public void setExtraInfo(Map<String, String> extraInfo) {
            this.extraInfo = extraInfo;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }
}

package map.baidu.ar.topimage;

import com.google.gson.annotations.SerializedName;

import map.baidu.ar.utils.INoProGuard;

/**
 * Created by zhujingsi on 2017/6/6.
 */

public class HeadImage implements INoProGuard {
    // 内容
    private String content;
    // 图片url
    @SerializedName("imgurl")
    private String imgUrl;
    // 链接rul
    private String link;
    // 类型
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package map.baidu.ar.model;


import map.baidu.ar.utils.INoProGuard;

public class BubbleB implements INoProGuard {
    // 文本
    private String content;
    // 泡泡名称
    private String title;
    // 泡泡颜色
    private String color;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
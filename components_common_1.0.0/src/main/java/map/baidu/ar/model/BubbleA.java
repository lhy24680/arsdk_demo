package map.baidu.ar.model;

import java.util.ArrayList;

import map.baidu.ar.utils.INoProGuard;


public class BubbleA implements INoProGuard {
    // 气泡颜色
    private int color;
    // 文本
    private String content;
    // 小图标
    private String image;
    // 泡泡名称
    private String title;
    // 星级，100为满级
    private int starLevel;
    // 气泡名称，PM要求去掉括号里的内容
    private String name;
    // pio标记
    private String tag;
    // 楼层信息
    private String floor;
    // 右上角tag（是否是新店／新品）
    private ArrayList<String> specialTag;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public ArrayList<String> getSpecialTag() {
        return specialTag;
    }

    public void setSpecialTag(ArrayList<String> specialTag) {
        this.specialTag = specialTag;
    }
}
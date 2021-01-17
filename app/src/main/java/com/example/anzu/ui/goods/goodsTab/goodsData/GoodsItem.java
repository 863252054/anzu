package com.example.anzu.ui.goods.goodsTab.goodsData;

public class GoodsItem {
    private String imageSource;
    private String title;
    private String typeName;
    private String priceNum;
    private String saveCount;

    public GoodsItem(String imageSource, String title, String typeName, String priceNum, String saveCount) {
        this.imageSource = imageSource;
        this.title = title;
        this.typeName = typeName;
        this.priceNum = priceNum;
        this.saveCount = saveCount;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPriceNum() {
        return priceNum;
    }

    public void setPriceNum(String priceNum) {
        this.priceNum = priceNum;
    }

    public String getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(String saveCount) {
        this.saveCount = saveCount;
    }
}

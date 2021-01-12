package com.example.anzu.ui.goods.goodsTab.goodsData;

public class GoodsItem {
    private int ImageId;
    private String title;
    private String typeName;
    private String priceNum;
    private String saveCount;

    public GoodsItem(int imageId, String title, String typeName, String priceNum, String saveCount) {
        ImageId = imageId;
        this.title = title;
        this.typeName = typeName;
        this.priceNum = priceNum;
        this.saveCount = saveCount;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
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

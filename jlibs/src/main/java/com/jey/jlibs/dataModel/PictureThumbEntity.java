package com.jey.jlibs.dataModel;

import java.io.Serializable;

/**
 * 查看图片Bean类
 */
public class PictureThumbEntity implements Serializable {

    private static final long pictureThumbSerialVersionUID = 6109859576148289150L;

    /**
     * 点击的第几张图片，从0开始
     */
    private boolean isClickPosition = false;
    /**
     * 在Adapter中的Position
     */
    private int adapterPosition;
    /**
     * 图片左边
     */
    private int left;
    /**
     * 图片右边
     */
    private int top;
    /**
     * view的宽度
     */
    private int viewWidth;
    /**
     * view的高度
     */
    private int viewHeight;
    /**
     * 当前项的图片url
     */
    private String url;

    public boolean isClickPosition() {
        return isClickPosition;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    public void setAdapterPosition(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public void setClickPosition(boolean clickPosition) {
        isClickPosition = clickPosition;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PictureThumbEntity{" +
                "isClickPosition=" + isClickPosition +
                ", adapterPosition=" + adapterPosition +
                ", left=" + left +
                ", top=" + top +
                ", viewWidth=" + viewWidth +
                ", viewHeight=" + viewHeight +
                ", url='" + url + '\'' +
                '}';
    }
}

package com.jey.jlibs.utils.PhotoSelectUtil;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.util.Date;

public class ImageItem implements Parcelable {
    public String imageId;
    public String thumbnailPath;// 缩略图
    public String imagePath;
    private Bitmap bitmap;// 位图
    public boolean isSelected = false;

    private Date picDate;

    public ImageItem() {
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    protected ImageItem(Parcel in) {
        imageId = in.readString();
        thumbnailPath = in.readString();
        imagePath = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageId);
        dest.writeString(thumbnailPath);
        dest.writeString(imagePath);
        dest.writeParcelable(bitmap, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public Bitmap getBitmap() {
        if (bitmap == null) {
            try {
                bitmap = Bimp.revisionImageSize(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public Date getPicDate() {
        return picDate;
    }

    public void setPicDate(Date picDate) {
        this.picDate = picDate;
    }
}
package com.jey.jlibs.utils.PhotoSelectUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class AlbumHelper {
    final String TAG = getClass().getSimpleName();
    ContentResolver cr;

    HashMap<String, String> thumbnailList = new HashMap<>();

    List<HashMap<String, String>> albumList = new ArrayList<>();
    HashMap<String, ImageBucket> bucketList = new HashMap<>();

    private static AlbumHelper instance;

    public static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    public void init(Context context) {
        cr = context.getContentResolver();
    }


    void getAlbum() {
        String[] projection = {Albums._ID, Albums.ALBUM, Albums.ALBUM_ART,
                Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS};
        Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
        getAlbumColumnData(cursor);

    }

    private void getAlbumColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            String album;
            String albumArt;
            String albumKey;
            String artist;
            int numOfSongs;

            int _idColumn = cur.getColumnIndex(Albums._ID);
            int albumColumn = cur.getColumnIndex(Albums.ALBUM);
            int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
            int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
            int artistColumn = cur.getColumnIndex(Albums.ARTIST);
            int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                album = cur.getString(albumColumn);
                albumArt = cur.getString(albumArtColumn);
                albumKey = cur.getString(albumKeyColumn);
                artist = cur.getString(artistColumn);
                numOfSongs = cur.getInt(numOfSongsColumn);

                // Do something with the values.
                Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
                        + "albumKey: " + albumKey + " artist: " + artist
                        + " numOfSongs: " + numOfSongs + "---");
                HashMap<String, String> hash = new HashMap<String, String>();
                hash.put("_id", _id + "");
                hash.put("album", album);
                hash.put("albumArt", albumArt);
                hash.put("albumKey", albumKey);
                hash.put("artist", artist);
                hash.put("numOfSongs", numOfSongs + "");
                albumList.add(hash);

            } while (cur.moveToNext());

        }
    }

    boolean hasBuildImagesBucketList = false;

    void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();
        getThumbnail();
        getImageList();
        for (Entry<String, ImageBucket> entry : bucketList.entrySet()) {
            ImageBucket bucket = entry.getValue();
            Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", " + bucket.count + " ---------- ");
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
                Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath + ", " + image.thumbnailPath);
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
    }

    /**
     * 获取缩略图
     */
    private void getThumbnail() {
        String[] projection = {Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA};
//		"image_id DESC"
        Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        getThumbnailColumnData(cursor);
    }

    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            do {
                int _idColumn = cur.getColumnIndex(Thumbnails._ID);
                int _count = cur.getColumnIndex(Thumbnails._COUNT);
                int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
                int thymb_data = cur.getColumnIndex(Thumbnails.THUMB_DATA);
                int kind = cur.getColumnIndex(Thumbnails.KIND);
                int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                // Do something with the values.
                Log.i(TAG, _id + " image_id:" + image_id + " path:" + image_path + "---");
                // HashMap<String, String> hash = new HashMap<String, String>();
                // hash.put("image_id", image_id + "");
                // hash.put("path", image_path);
                // thumbnailList.add(hash);
                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }

    private void getImageList() {
        String columns[] = new String[]{Media._ID, Media.BUCKET_ID,
                Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE,
                Media.SIZE, Media.BUCKET_DISPLAY_NAME, Media.DATE_TAKEN, Media.DATE_MODIFIED};
        Cursor cur = cr.query(Media.EXTERNAL_CONTENT_URI, columns, null, null, "date_modified DESC");
        getImageColumnData(cur);
    }


    private void getImageColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
            int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);//每个文件夹的名称
            int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);//每个文件夹的ID
            int picDateTaken = cur.getColumnIndexOrThrow(Media.DATE_TAKEN);
            int totalNum = cur.getCount();
            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                long lPicDate = cur.getLong(picDateTaken);
                Date picDate = new Date(lPicDate);

                Log.i(TAG, _id + ", bucketId: " + bucketId + " name:" + name + " path:" + path
                        + " title: " + title + " size: " + size + " bucket: "
                        + bucketName + " PicDate: " + cur.getString(picDateTaken) + " picDate: " + picDate);

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {//如果文件夹列表里面还没有该文件夹
                    bucket = new ImageBucket();
                    bucket.bucketName = bucketName;
                    bucket.imageList = new ArrayList<>();
                    bucketList.put(bucketId, bucket);//把当前文件夹加入文件夹列表
                }
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);
                imageItem.setPicDate(picDate);
                bucket.count++;
                bucket.imageList.add(imageItem);
            } while (cur.moveToNext());
        }
    }

    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || !hasBuildImagesBucketList) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<>();
        for (Entry<String, ImageBucket> entry : bucketList.entrySet()) {
            tmpList.add(entry.getValue());
        }
        bucketList.clear();
        return tmpList;
    }

    String getOriginalImagePath(String image_id) {
        String path = null;
        Log.i(TAG, "---(^o^)----" + image_id);
        String[] projection = {Media._ID, Media.DATA};
        Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
                Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));

        }
        return path;
    }

}

package me.jockio.csdn.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zhangsj-fnst on 2016/7/5/0005.
 */
public class ImageFileCache {

    /**
     * 从本地读取图片
     *
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url) {
        String fileName;//把图片的url当做文件名
        try {
            fileName = convertUrlToFileName(url);
            File file = new File(Tools.getDirectory(), fileName);

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            String fileName = convertUrlToFileName(url);//把图片的url当做文件名
            File file = new File(Tools.getDirectory(), fileName);

            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将url装换为文件名
     * @param url
     * @return
     */
    private String convertUrlToFileName(String url){
        String[] names = url.split("/");
        return names[names.length - 1].replace(".jpg", "") + ".cache";
    }
}
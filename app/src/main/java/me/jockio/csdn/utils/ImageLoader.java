package me.jockio.csdn.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import me.jockio.csdn.R;

import static me.jockio.csdn.R.id.imageView;


/**
 * Created by zhangsj-fnst on 2016/7/5/0005.
 */
public class ImageLoader {

    private static ImageLoader instance;

    private ImageMemoryCache mMemoryCache;
    private ImageFileCache mFileCache;
    private ImageHttpCache mHttpCache;

    private ImageLoader(Context context) {
        mMemoryCache = new ImageMemoryCache();
        mFileCache = new ImageFileCache();
        mHttpCache = new ImageHttpCache(mFileCache, mMemoryCache);
    }

    /**
     * 使用单例，保证整个应用中只有一个线程池和一份内存缓存和文件缓存
     */
    public static ImageLoader getInstance(Context context) {
        if (instance == null)
            instance = new ImageLoader(context);
        return instance;
    }

    public void displayImage(String url, ImageView imageView) {
        imageView.setImageResource(R.mipmap.csdn);
        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCache.getBitmapFromMemory(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从内存获取图片啦.....");
            return;
        }

        //本地缓存
        bitmap = mFileCache.getBitmapFromLocal(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCache.setBitmapToMemory(url, bitmap);
            return;
        }
        //网络缓存
        mHttpCache.getBitmapFromNet(imageView, url);
    }
}

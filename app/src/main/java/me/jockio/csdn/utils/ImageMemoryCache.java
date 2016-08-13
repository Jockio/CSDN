package me.jockio.csdn.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by zhangsj-fnst on 2016/7/5/0005.
 */
public class ImageMemoryCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageMemoryCache(){
        ////得到手机最大允许内存的1/8,即超过指定内存,则开始回收
        long max = Runtime.getRuntime().maxMemory() / 8;
        //传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
        mMemoryCache = new LruCache<String, Bitmap>((int) max){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };
    }

    /**
     * 从内存中读图片
     * @param url
     */
    public Bitmap getBitmapFromMemory(String url) {
        //1.强引用方法
        Bitmap bitmap = mMemoryCache.get(url);
        return bitmap;
        /*2.弱引用方法
            SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
            if (bitmapSoftReference != null) {
                Bitmap bitmap = bitmapSoftReference.get();
                return bitmap;
            }
        */
    }

    /**
     * 往内存中写图片
     * @param url
     * @param bitmap
     */
    public void setBitmapToMemory(String url, Bitmap bitmap) {
        //1.强引用方法
        mMemoryCache.put(url,bitmap);
        /*2.弱引用方法
            mMemoryCache.put(url, new SoftReference<>(bitmap));
        */
    }

}

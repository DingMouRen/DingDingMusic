package com.dingmouren.dingdingmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.dingmouren.dingdingmusic.R;
import com.yancy.gallerypick.inter.ImageLoader;
import com.yancy.gallerypick.widget.GalleryImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dingmouren on 2017/1/4.
 *
 */

public class MyGlideImageLoader implements ImageLoader {
    private static Context mContext;
    public static void displayImage(String url, ImageView img){
        mContext = img.getContext();
        if (NetworkUtil.isWifiConnected(mContext)) {
            loadNormal(url, img);
        } else {
            loadCache(url, img);
        }

    }
    private static void loadNormal(String url, ImageView img) {
        Glide.with(mContext).load(url)/*.placeholder(R.mipmap.loading)*/.dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
    }
    private static void loadCache(String url, ImageView img) {
        Glide.with(mContext).using(new StreamModelLoader<String>() {

            @Override
            public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
                return new DataFetcher<InputStream>() {
                    @Override
                    public InputStream loadData(Priority priority) throws Exception {
                        throw new IOException();
                    }

                    @Override
                    public void cleanup() {

                    }

                    @Override
                    public String getId() {
                        return model;
                    }

                    @Override
                    public void cancel() {

                    }
                };
            }
        }).load(url).placeholder(R.mipmap.icon_header).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);

    }

    /**
     * 图片选择器的图片加载方法
     * @param activity
     * @param context
     * @param path
     * @param galleryImageView
     * @param width
     * @param height
     */
    @Override
    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context).load(path).placeholder(android.R.drawable.ic_menu_gallery).centerCrop().into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}

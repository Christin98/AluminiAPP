
package com.project.major.alumniapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.StorageReference;
import com.project.major.alumniapp.R;

public class ImageUtils {
    public static final String TAG = ImageUtils.class.getSimpleName();
//
//    public static String generateImageTitle(UploadImagePrefix prefix, String parentId) {
//        if (parentId != null) {
//            return prefix.toString() + parentId;
//        }
//
//        return prefix.toString() + new Date().getTime();
//    }

//    public static String generatePostImageTitle(String parentId) {
//        return generateImageTitle(UploadImagePrefix.POST, parentId) + "_" + new Date().getTime();
//    }

    public static void loadImage(GlideRequests glideRequests, String url, ImageView imageView) {
        loadImage(glideRequests, url, imageView, DiskCacheStrategy.ALL);
    }

    public static void loadImage(GlideRequests glideRequests, String url, ImageView imageView, DiskCacheStrategy diskCacheStrategy) {
        glideRequests.load(url)
                .diskCacheStrategy(diskCacheStrategy)
                .error(R.drawable.ic_stub)
                .into(imageView);
    }

    public static void loadImage(GlideRequests glideRequests, String url, ImageView imageView,
                                 RequestListener<Drawable> listener) {
        glideRequests.load(url)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, String url, ImageView imageView,
                                           int width, int height) {
        glideRequests.load(url)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, StorageReference imageStorageRef, ImageView imageView,
                                           int width, int height) {
        glideRequests.load(imageStorageRef)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, StorageReference imageStorageRef, ImageView imageView,
                                           int width, int height, RequestListener<Drawable> listener) {
        glideRequests.load(imageStorageRef)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView);
    }

    public static void loadMediumImageCenterCrop(GlideRequests glideRequests,
                                                 StorageReference imageStorageRefMedium,
                                                 StorageReference imageStorageRefOriginal,
                                                 ImageView imageView,
                                                 int width,
                                                 int height,
                                                 RequestListener<Drawable> listener) {

        glideRequests.load(imageStorageRefMedium)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .error(glideRequests.load(imageStorageRefOriginal))
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, String url, ImageView imageView,
                                           int width, int height, RequestListener<Drawable> listener) {
        glideRequests.load(url)
                .centerCrop()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, StorageReference imageStorageRef, ImageView imageView,
                                           RequestListener<Drawable> listener) {
        glideRequests.load(imageStorageRef)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView);
    }

    public static void loadImageCenterCrop(GlideRequests glideRequests, String url, ImageView imageView,
                                           RequestListener<Drawable> listener) {
        glideRequests.load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_stub)
                .listener(listener)
                .into(imageView);
    }


    @Nullable
    public static Bitmap loadBitmap(GlideRequests glideRequests, String url, int width, int height) {
        try {
            return glideRequests.asBitmap()
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .submit(width, height)
                    .get();
        } catch (Exception e) {
            Log.e(TAG, "getBitmapfromUrl", e);
            return null;
        }
    }

    public static void loadImageWithSimpleTarget(GlideRequests glideRequests, String url, SimpleTarget<Bitmap> simpleTarget) {
        glideRequests.asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(simpleTarget);
    }

    public static void loadImageWithSimpleTarget(GlideRequests glideRequests, StorageReference imageStorageRef, SimpleTarget<Bitmap> simpleTarget) {
        glideRequests.asBitmap()
                .load(imageStorageRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(simpleTarget);
    }

    public static void loadLocalImage(GlideRequests glideRequests, Uri uri, ImageView imageView,
                                      RequestListener<Drawable> listener) {
        glideRequests.load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .fitCenter()
                .listener(listener)
                .into(imageView);
    }

    public static void loadImageWithTransition(Context mContext, String imageUrl, ImageView image, final ProgressBar progressBar) {
        Glide.with(mContext)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if(progressBar!=null)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(progressBar!=null)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(image);
    }


    public static void loadImageWithOutTransition(Context mContext, String imageUrl, ImageView image) {
        Glide.with(mContext)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profle_user)
                .error(R.drawable.profle_user))
                .into(image);
    }
}

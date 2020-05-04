package com.project.major.alumniapp

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.danikula.videocache.HttpProxyCacheServer
import com.mikepenz.iconics.Iconics.init
import com.project.major.alumniapp.utils.CacheUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import timber.log.Timber
import timber.log.Timber.DebugTree
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.IOException
import java.util.*


 public class AApplication : Application() {
    private var proxy: HttpProxyCacheServer? = null

    companion object {
        var instance: Application? = null
            private set

        @JvmStatic
        fun getProxy(context: Context): HttpProxyCacheServer {
            val application = context.applicationContext as AApplication
            return (if (application.proxy == null) application.newProxy().also { application.proxy = it } else application.proxy!!)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        init(this)
        if (instance == null) {
            instance = this
            Album.initialize(AlbumConfig.newBuilder(this)
                    .setAlbumLoader(MediaLoader())
                    .setLocale(Locale.getDefault())
                    .build()
            )
        }

        val picasso = Picasso.Builder(this).addRequestHandler(
                AssetVideoRequestHandler()).build()
        Picasso.setSingletonInstance(picasso)
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this)
                .cacheDirectory(CacheUtils.getVideoCacheDir(this))
                .maxCacheFilesCount(40)
                .maxCacheSize(1024 * 1024 * 1024.toLong())
                .build()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
//        if (level >= TRIM_MEMORY_BACKGROUND) ToroExo.with(this).cleanUp()
    }

    private class AssetVideoRequestHandler : RequestHandler() {
        val SCHEME = "videoframe"
        override fun canHandleRequest(data: Request): Boolean {
            return SCHEME.equals(data.uri.getScheme())
        }

        @Throws(IOException::class)
        override fun load(request: Request, networkPolicy: Int): Result? {
            // ExoPlayer accepts uris in the form "asset:///path/to/video.mp4",
            // but AssetManager only needs the relative path "path/to/video.mp4"

            val bitmap: Bitmap? = null
            val fmmr = FFmpegMediaMetadataRetriever()
            try {
                fmmr.setDataSource(request.uri.path)
                var b = fmmr.frameAtTime
                if (b != null) {
                    val b2 = fmmr.getFrameAtTime(4000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (b2 != null) {
                        b = b2
                    }
                }
                if (b != null) {
                    Timber.i("Thumbnail", "Extracted frame")
                    return Result(b, Picasso.LoadedFrom.DISK)
                } else {
                    Timber.e("Thumbnail", "Failed to extract frame")
                }
            } catch (ex :IllegalArgumentException ) {
                ex.printStackTrace()
            } finally {
                fmmr.release()
            }
            return Result(bitmap!!, Picasso.LoadedFrom.DISK)
        }
    }
}
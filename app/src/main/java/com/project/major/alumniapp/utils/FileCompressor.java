package com.project.major.alumniapp.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;

public class FileCompressor {

    private Context context;

    public FileCompressor(Context context) {
        this.context = context;
    }

    public Uri imageCompressor(String path1) {
        File file = null;
        File f = new File(Environment.getExternalStorageDirectory() + "/alumni_app/compressed/images");
        if (!f.exists()) {
            boolean mkdir = f.mkdirs();
            if (mkdir) {
                file = new File(SiliCompressor.with(context).compress(path1, f));
            } else {
                Log.e("Compressor", "File not created");
            }
        } else {
            file = new File(SiliCompressor.with(context).compress(path1, f));
        }
        Log.e("Compressor", file.getPath());
        return Uri.fromFile(file);
    }

    public Uri videoCompressor(String path1) {
        File file = null;
        File f = new File(Environment.getExternalStorageDirectory() + "/alumni_app/compressed/videos");
        if (!f.exists()) {
            boolean mkdir = f.mkdirs();
            if (mkdir) {
                try {
                    file = new File(SiliCompressor.with(context).compressVideo(path1, f.getPath()));
                } catch (URISyntaxException u) {
                    u.printStackTrace();
                }
            } else {
                Log.e("Compressor", "File not created");
            }
        } else {
            try {
                file = new File(SiliCompressor.with(context).compressVideo(path1, f.getPath()));
            } catch (URISyntaxException u) {
                u.printStackTrace();
            }
        }
        Log.e("Compressor", file.getPath());
        return Uri.fromFile(file);
    }
}
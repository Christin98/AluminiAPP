package com.project.major.alumniapp.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class FileCompressor {
    private Context context;

    public FileCompressor(Context context){
        this.context = context;
    }

    public File compressImage(String imageUrl){
        File imageFile = new File(imageUrl);
        try {
            return new Compressor(context).compressToFile(imageFile);
        } catch (IOException e){
            e.printStackTrace();
            return new File(imageUrl);
        }
    }
}

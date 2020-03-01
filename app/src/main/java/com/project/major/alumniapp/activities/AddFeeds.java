/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 1                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.Feeds;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.SquareImageView;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class AddFeeds extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/AlumniAPP/Feeds/Pics";
    private int GALLERY = 1, CAMERA = 2;

    private DatabaseReference mDatabase;
    StorageReference feedrefrence;
    EditText caption, texturl;
    Button uploadFeed;
    SquareImageView feedimage;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeds);

        requestMultiplePermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference("alumni_app").getRef();
        feedrefrence = FirebaseStorage.getInstance().getReference("alumni_app").child("feed_pics");
        feedimage = findViewById(R.id.feedImage);
        uploadFeed = findViewById(R.id.uploadFeed);
        caption = findViewById(R.id.caption);
        texturl = findViewById(R.id.text_url);

        feedimage.setOnClickListener(v -> showPictureDialog());

        uploadFeed.setOnClickListener(v -> {
            if (imgPath != null){
                upload(imgPath);
            } else {
                upload();
            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

//    void pickIMage()
//    {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityForResult(Intent.createChooser(intent,"Select Image"),1002);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

//        if(requestCode==1002){
//            try {
//                uri=data.getData();
//                Bitmap bm= MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
//                profile_image.setImageBitmap(bm);
//            } catch (IOException e) {
//                e.printStackTrace();
//                TastyToast.makeText(AddEvent.this, ""+e, TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
//            }
//        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    TastyToast.makeText(AddFeeds.this, "Image Saved!", TastyToast.LENGTH_SHORT,TastyToast.INFO).show();
                    feedimage.setImageBitmap(bitmap);
                    imgPath = path;

                } catch (IOException e) {
                    e.printStackTrace();
                    TastyToast.makeText(AddFeeds.this, "Failed!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            feedimage.setImageBitmap(thumbnail);
            imgPath = saveImage(thumbnail);
            TastyToast.makeText(AddFeeds.this, "Image Saved!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        }
        //the end  onActivityResult
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File pic_Dirc = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!pic_Dirc.exists()) {
            boolean mkdir = pic_Dirc.mkdirs();
            if (!mkdir){
                Log.d("Add Feed","mkdir failed");
            }
        }

        try {
            File f = new File(pic_Dirc, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("Add Feed", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            return "";
        }
    }

    public void upload(String path)
    {
        Log.d("Alumni app",path);
        StorageReference storageReference;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();
        final String email = user.getEmail();
        final String captio = caption.getText().toString();
        final String txturl = texturl.getText().toString();
        final String id = mDatabase.push().getKey();
        FileCompressor compressor = new FileCompressor(getApplicationContext());
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        String date_added = Long.toString(System.currentTimeMillis());
        UploadTask uploadTask;
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        storageReference = feedrefrence.child("alumniapp"+id);
        uploadTask = storageReference.putFile(Uri.fromFile(compressor.compressImage(path)),metadata);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String  url = uri1.toString();
            Feeds uploadFeeds = new Feeds("alumniapp"+id,email, user_id, date_added, captio, txturl,url);
            mDatabase.child("Feeds").child(id).setValue(uploadFeeds).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                TastyToast.makeText(getApplicationContext(), "Post Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
            });
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            TastyToast.makeText(this, "failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        });
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentprogress = (int) progress;
            progressDialog.setProgress(currentprogress);
        });
    }

    public void upload(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();
        final String email = user.getEmail();
        final String captio = caption.getText().toString();
        final String txturl = texturl.getText().toString();
        final String id = mDatabase.push().getKey();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Adding Event ....");
        progressDialog.show();
        String date_added = Long.toString(System.currentTimeMillis());

        final Feeds uploadPost = new Feeds(id, email, user_id, date_added, captio, txturl, null);
        mDatabase.child("Feeds").child(id).setValue(uploadPost).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            TastyToast.makeText(this, "Event Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
        });
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            TastyToast.makeText(getApplicationContext(), "All permissions are granted by user!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+getPackageName())),0);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> TastyToast.makeText(getApplicationContext(), "Some Error! ", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show())
                .onSameThread()
                .check();
    }
}

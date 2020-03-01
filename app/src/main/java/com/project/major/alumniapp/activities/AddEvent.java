/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 1                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.project.major.alumniapp.models.UploadEvent;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.SquareImageView;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AddEvent extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/AlumniAPP/Events/Pics";
    private int GALLERY = 1, CAMERA = 2;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
    private DatabaseReference mDatabase;
    StorageReference eventrefrance;
    EditText event_name, desc, loc;
    Button uploadEvent;
    SquareImageView profile_image;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        requestMultiplePermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference("alumni_app").getRef();
        eventrefrance = FirebaseStorage.getInstance().getReference("alumni_app").child("event_pics");
        profile_image= findViewById(R.id.profile_image);
        uploadEvent = findViewById(R.id.uploadEvent);
        event_name = findViewById(R.id.event_name);
        desc = findViewById(R.id.event_details);
        loc = findViewById(R.id.event_location);

        profile_image.setOnClickListener(v -> showPictureDialog());

        uploadEvent.setOnClickListener(v -> {
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
                    TastyToast.makeText(AddEvent.this, "Image Saved!", TastyToast.LENGTH_SHORT,TastyToast.INFO).show();
                    profile_image.setImageBitmap(bitmap);
                    imgPath = path;

                } catch (IOException e) {
                    e.printStackTrace();
                    TastyToast.makeText(AddEvent.this, "Failed!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profile_image.setImageBitmap(thumbnail);
            imgPath = saveImage(thumbnail);
            TastyToast.makeText(AddEvent.this, "Image Saved!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
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
                Log.d("Add Event","mkdir failed");
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
            Log.d("Add Event", "File Saved::---&gt;" + f.getAbsolutePath());

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
        final String eventName = event_name.getText().toString();
        final String description = desc.getText().toString();
        final String location = loc.getText().toString();
        final String id = mDatabase.push().getKey();
        FileCompressor compressor = new FileCompressor(getApplicationContext());
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        String date_added = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        UploadTask uploadTask;
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        storageReference = eventrefrance.child("alumniapp"+id);
        uploadTask = storageReference.putFile(Uri.fromFile(compressor.compressImage(path)),metadata);

        uploadTask.addOnSuccessListener(taskSnapshot -> {

            storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
              String  url = uri1.toString();
              UploadEvent uploadEvent = new UploadEvent("alumniapp"+id, eventName, description, location, email, date_added,user_id ,url);
                mDatabase.child("Events").child(id).setValue(uploadEvent).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), "Post Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                });
            });




        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            TastyToast.makeText(this, "failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        });
    }

    public void upload(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();
        final String email = user.getEmail();
        final String eventName = event_name.getText().toString();
        final String description = desc.getText().toString();
        final String location = loc.getText().toString();
        final String id = mDatabase.push().getKey();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Adding Event ....");
        progressDialog.show();
        String date_added = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS", Locale.ENGLISH).format(Calendar.getInstance().getTime());

        final UploadEvent uploadPost = new UploadEvent(id, eventName, description, location, email, date_added, user_id);
        mDatabase.child("Events").child(id).setValue(uploadPost).addOnSuccessListener(aVoid -> {
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

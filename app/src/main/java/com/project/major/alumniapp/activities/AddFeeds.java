
package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.project.major.alumniapp.models.NotificationModel;
import com.project.major.alumniapp.models.Photo;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.models.Video;
import com.project.major.alumniapp.utils.Fcm;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.SquareImageView;
import com.project.major.alumniapp.utils.StringManipulation;
import com.sdsmdg.tastytoast.TastyToast;
import com.vincent.videocompressor.VideoCompress;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class AddFeeds extends AppCompatActivity {

    private DatabaseReference mDatabase;
    StorageReference feedrefrence;
    EditText caption;
    Button uploadFeed;
    SquareImageView feedimage;
    Toolbar mToolbar;
    List<AlbumFile> mAlbumFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeds);

        mToolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(mToolbar);

        requestMultiplePermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference("alumni_app");
        feedrefrence = FirebaseStorage.getInstance().getReference("alumni_app");
        feedimage = findViewById(R.id.feedImage);
        uploadFeed = findViewById(R.id.uploadFeed);
        caption = findViewById(R.id.caption);

        File f = new File(Environment.getExternalStorageDirectory() + "/alumni_app/compressed/videos");
        if (!f.exists())
            f.mkdirs();

        feedimage.setOnClickListener(v -> selectAlbum());

        uploadFeed.setOnClickListener(v -> {
            if (mAlbumFiles.get(0) != null){
                upload(mAlbumFiles.get(0));
            } else {
                upload();
            }
        });
    }

    private void selectAlbum() {
        Album.album(this)
                .singleChoice()
                .columnCount(2)
                .camera(true)
                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(30000)
                .cameraVideoLimitBytes(20000000)
                .widget(
                        Widget.newDarkBuilder(this)
                                .title(mToolbar.getTitle().toString())
                                .build()
                )
                .onResult(result -> {
                    mAlbumFiles = result;
                    Album.getAlbumConfig().
                            getAlbumLoader().
                            load(feedimage, mAlbumFiles.get(0));
                })
                .onCancel(result -> Toast.makeText(AddFeeds.this, R.string.canceled, Toast.LENGTH_LONG).show())
                .start();
    }


    public void upload(AlbumFile albumFile)
    {
        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
            StorageReference storageReference;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String user_id = user.getUid();
            final String profileImg = getProfileImg(user_id);
            final String name = user.getDisplayName();
            final String captio = caption.getText().toString();
            final String tags = StringManipulation.getTags(captio);
            final String id = mDatabase.push().getKey();
            FileCompressor compressor = new FileCompressor(getApplicationContext());
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            String date_added = Long.toString(System.currentTimeMillis());
            UploadTask uploadTask;
            StorageMetadata metadata = new StorageMetadata.Builder().setContentType(albumFile.getMimeType()).build();
            storageReference = feedrefrence.child("feed_pics").child("IMG_alumniapp" + id);
            uploadTask = storageReference.putFile(compressor.imageCompressor(albumFile.getPath()), metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String url = uri1.toString();
                Photo photo = new Photo(id, name, user_id, date_added, captio, tags, url, profileImg);
//            new Fcm(this,"new_post", "Alumni APP", user.getDisplayName() + " added a post", url).init()
                NotificationModel notificationModel = new NotificationModel("post", user.getDisplayName(), date_added, id);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user_id);
                reference.push().setValue(notificationModel);
                mDatabase.child("Feeds").child("photos").child(id).setValue(photo).addOnSuccessListener(aVoid -> {
//                    new Fcm(this, "new_post", "Alumni APP", user.getDisplayName() + " added a post", url).init();
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), "Post Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    finish();
                });
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                TastyToast.makeText(this, "failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            });
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                progressDialog.setProgress(currentprogress);
                progressDialog.setMax(100);
                progressDialog.setMessage("Uploading.... " + currentprogress + "%");
            });
        } else if (albumFile.getMediaType() == AlbumFile.TYPE_VIDEO){
            ProgressDialog progressdialog = new ProgressDialog(AddFeeds.this);
            progressdialog.setIndeterminate(false);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressdialog.setCancelable(true);
            progressdialog.setMax(100);

            String desPath = Environment.getExternalStorageDirectory().toString() + "/alumni_app/compressed/videos/" + "VID_" +System.currentTimeMillis()+".mp4";
            VideoCompress.compressVideoMedium(albumFile.getPath(), desPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    progressdialog.setMessage("Compressing...");
                    progressdialog.show();
                }

                @Override
                public void onSuccess() {
                    StorageReference storageReference;
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String user_id = user.getUid();
                    final String profileImg = getProfileImg(user_id);
                    final String name = user.getDisplayName();
                    final String captio = caption.getText().toString();
                    final String tags = StringManipulation.getTags(captio);
                    final String id = mDatabase.push().getKey();
                    File mFile;
                    final ProgressDialog progressDialog=new ProgressDialog(AddFeeds.this);
                    progressDialog.setCancelable(true);
                    progressDialog.setMessage("Uploading....");
                    progressDialog.show();
                    mFile = new File(desPath);
                    Uri uri = Uri.fromFile(mFile);
                    String date_added = Long.toString(System.currentTimeMillis());
                    UploadTask uploadTask;
                    StorageMetadata metadata = new StorageMetadata.Builder().setContentType(albumFile.getMimeType()).build();
                    storageReference = feedrefrence.child("feed_videos").child("VID_alumniapp"+id);
                    uploadTask = storageReference.putFile(uri,metadata);
                    uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String url = uri1.toString();
                        Video video = new Video(id, name, user_id, date_added, captio, tags, url, profileImg );
//                        new Fcm(AddFeeds.this,"new_event", "Alumni APP", user.getDisplayName() + " added a event",  url).init();
                        NotificationModel notificationModel = new NotificationModel("post", user.getDisplayName(), date_added, id);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user_id);
                        reference.push().setValue(notificationModel);
                        mDatabase.child("Feeds").child("videos").child(id).setValue(video).addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            TastyToast.makeText(getApplicationContext(), "Post Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                            finish();
                        });
                    })).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        TastyToast.makeText(AddFeeds.this, "Failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                    });
                    uploadTask.addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        int currentprogress = (int) progress;
                        progressDialog.setProgress(currentprogress);
                        progressDialog.setMax(100);
                        progressDialog.setMessage("Uploading.... " + currentprogress +"%");
                    });
                }

                @Override
                public void onFail() {
                    TastyToast.makeText(AddFeeds.this,"Compression Failed", TastyToast.LENGTH_LONG, TastyToast.ERROR).show();
                }

                @Override
                public void onProgress(float percent) {
                    progressdialog.setProgress((int) percent);
                }
            });


        }
    }

    public void upload(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        final String profile_img = getProfileImg(user_id);
        final String name = user.getDisplayName();
        final String captio = caption.getText().toString();
        final String tags = StringManipulation.getTags(captio);
        final String id = mDatabase.push().getKey();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Adding Event ....");
        progressDialog.show();
        String date_added = Long.toString(System.currentTimeMillis());

        Feeds uploadPost = new Feeds(id, name, user_id, date_added, captio, tags, profile_img);
//        new Fcm(this,"new_post", "Alumni APP", user.getDisplayName() + " added a post",  null).init();
        NotificationModel notificationModel = new NotificationModel("post", user.getDisplayName(), date_added,id);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user_id);
        reference.push().setValue(notificationModel);
        mDatabase.child("Feeds").child("textfeeds").child(id).setValue(uploadPost).addOnSuccessListener(aVoid -> {
//            new Fcm(this,"new_post", "Alumni APP", user.getDisplayName() + " added a post",  null).init();
            progressDialog.dismiss();
            TastyToast.makeText(this, "Event Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
            finish();
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

    private String getProfileImg(String uid) {
        String[] profileImg = {null};
        FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(uid).addValueEventListener(new ValueEventListener() {
            String profile = null;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue(User.class)).getUser_image() == null) {
                    profile = null;
                } else {
                    profile = Objects.requireNonNull(dataSnapshot.getValue(User.class)).getUser_image();
                    profileImg[0] = profile;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return profileImg[0];
    }
}

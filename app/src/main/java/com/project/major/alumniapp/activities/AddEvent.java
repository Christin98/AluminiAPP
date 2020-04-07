package com.project.major.alumniapp.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
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
import com.project.major.alumniapp.models.Event;
import com.project.major.alumniapp.models.NotificationModel;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.Fcm;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.SquareImageView;
import com.project.major.alumniapp.utils.StringManipulation;
import com.sdsmdg.tastytoast.TastyToast;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddEvent extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/AlumniAPP/Events/Pics";
//    private int GALLERY = 1, CAMERA = 2;
////    private static String[] PERMISSIONS_STORAGE = {
////            Manifest.permission.READ_EXTERNAL_STORAGE,
////            Manifest.permission.WRITE_EXTERNAL_STORAGE
////    };
    private DatabaseReference mDatabase;
    StorageReference eventrefrancep;
    StorageReference eventrefrancev;
    EditText event_name, desc, loc;
    Button uploadEvent;
    SquareImageView profile_image;

    Toolbar mToolbar;

    List<AlbumFile> mAlbumFiles;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    @BindView(R.id.event_timing)
    EditText event_time;

    @BindView(R.id.event_date)
    EditText event_date;

    File mFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        ButterKnife.bind(this);

        requestMultiplePermissions();

        mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference("alumni_app").getRef();
        eventrefrancep = FirebaseStorage.getInstance().getReference("alumni_app").child("event_pics");
        eventrefrancev = FirebaseStorage.getInstance().getReference("alumni_app").child("event_videos");

        profile_image= findViewById(R.id.profile_image);
        uploadEvent = findViewById(R.id.event);
        event_name = findViewById(R.id.event_name);
        desc = findViewById(R.id.event_details);
        loc = findViewById(R.id.event_location);

        event_time.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(AddEvent.this, (view, hourOfDay, minute) -> event_time.setText(hourOfDay + ":" + minute), hour, min, false);
            timePickerDialog.show();
        });


        event_date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(AddEvent.this, (view, year1, month1, dayOfMonth) -> event_date.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1),year, month, day);
            datePickerDialog.show();
        });

        profile_image.setOnClickListener(v -> selectAlbum());



        uploadEvent.setOnClickListener(v -> {
            if (mAlbumFiles != null) {
                upload(mAlbumFiles.get(0));
            } else {
                upload();
            }
        });


    }

    private void selectAlbum() {
        Album.image(this)
                .singleChoice()
                .columnCount(2)
                .camera(true)
                .widget(
                        Widget.newDarkBuilder(this)
                                .title(mToolbar.getTitle().toString())
                                .build()
                )
                .onResult(result -> {
                    mAlbumFiles = result;
                    Album.getAlbumConfig().
                            getAlbumLoader().
                            load(profile_image, mAlbumFiles.get(0));
                })
                .onCancel(result -> Toast.makeText(AddEvent.this, R.string.canceled, Toast.LENGTH_LONG).show())
                .start();
    }


    public void upload(AlbumFile albumFile)
    {
        Log.d("Alumni app",albumFile.getMimeType());
        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
            StorageReference storageReference;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String user_id = user.getUid();
            final String profileImg = getProfileImg(user_id);
            final String user_name = user.getDisplayName();
            final String eventName = event_name.getText().toString();
            final String description = desc.getText().toString();
            final String location = loc.getText().toString();
            final String time = event_time.getText().toString();
            final String date = event_date.getText().toString();
            final String id = mDatabase.push().getKey();
            final String tags = StringManipulation.getTags(description);
            FileCompressor compressor = new FileCompressor(getApplicationContext());
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            String date_added = Long.toString(System.currentTimeMillis());
            UploadTask uploadTask;
            StorageMetadata metadata = new StorageMetadata.Builder().setContentType(albumFile.getMimeType()).build();
            storageReference = eventrefrancep.child("IMG_alumniapp"+id);
            Log.e("ALBUMFIle", albumFile.getPath());
            uploadTask = storageReference.putFile(compressor.imageCompressor(albumFile.getPath()),metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                Event photo = new Event(id, eventName, description, location, time, date, url, tags, user_id, user_name, date_added, profileImg);
//                new Fcm(this,"new_event", "Alumni APP", user.getDisplayName() + " added a event",  url).init();
                NotificationModel notificationModel = new NotificationModel("post", user.getDisplayName(), date_added, id);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user_id);
                reference.push().setValue(notificationModel);
                mDatabase.child("Events").child("photos").child(id).setValue(photo).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    TastyToast.makeText(getApplicationContext(), "Post Added ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    finish();
                });
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                TastyToast.makeText(this, "Failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            });
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                progressDialog.setProgress(currentprogress);
                progressDialog.setMax(100);
                progressDialog.setMessage("Uploading.... " + currentprogress +"%");
            });
        }

    }


    public void upload(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        final String profileImg = getProfileImg(user_id);
        final String user_name = user.getDisplayName();
        final String eventName = event_name.getText().toString();
        final String description = desc.getText().toString();
        final String location = loc.getText().toString();
        final String time = event_time.getText().toString();
        final String date = event_date.getText().toString();
        final String tags = StringManipulation.getTags(description);
        final String id = mDatabase.push().getKey();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Adding Event ....");
        progressDialog.show();
        String date_added = Long.toString(System.currentTimeMillis());
        final Event uploadPost = new Event(id, eventName, description, location, time, date,null, tags, user_id, user_name, date_added, profileImg);
//        new Fcm(this,"new_event", "Alumni APP", user.getDisplayName() + " added a event",  null).init();
        NotificationModel notificationModel = new NotificationModel("post", user.getDisplayName(), date_added,id);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("notification").child(user_id);
        reference.push().setValue(notificationModel);
        mDatabase.child("Events").child("textevents").child(id).setValue(uploadPost).addOnSuccessListener(aVoid -> {
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


package com.project.major.alumniapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
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
import com.project.major.alumniapp.models.Jobs;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.SquareImageView;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddJob extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/AlumniAPP/Jobs/Pics";
    private int GALLERY = 1, CAMERA = 2;

    private DatabaseReference mDatabase;
    StorageReference jobrefrence;
    EditText comapnyname_et;
    EditText jobprofile_et;
    EditText jobdescription_et;
    EditText lastdate_et;
    EditText joblocation_et;
    EditText apply;
    Spinner exp_spinner;
    Button uploadJob;
    SquareImageView companyLogo;
    String imgPath;
    String item;
    Calendar calendar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        requestMultiplePermissions();

        mDatabase = FirebaseDatabase.getInstance().getReference("alumni_app");
        jobrefrence = FirebaseStorage.getInstance().getReference("alumni_app").child("job_pics");
        companyLogo = findViewById(R.id.company_logo);
        uploadJob = findViewById(R.id.uploadJob);
        comapnyname_et = findViewById(R.id.companyname_et);
        jobprofile_et = findViewById(R.id.jobprofile_et);
        jobdescription_et = findViewById(R.id.job_description_et);
        lastdate_et = findViewById(R.id.last_date_et);
        joblocation_et = findViewById(R.id.job_location_et);
        exp_spinner = findViewById(R.id.exp_spinner);
        apply = findViewById(R.id.apply_link);
        auth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            lastdate_et.setText(sdf.format(calendar.getTime()));
        };

        lastdate_et.setOnClickListener(v -> new DatePickerDialog(AddJob.this, dateSetListener, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        companyLogo.setOnClickListener(v -> showPictureDialog());

        uploadJob.setOnClickListener(v -> {
            if (imgPath != null){
                upload(imgPath);
            } else {
                upload();
            }
        });

        List<String> experience = new ArrayList<>();
        experience.add("0 - 1 Year");
        experience.add("2 - 3 Years");
        experience.add("4 - 5 Years");
        experience.add("6 - 7 Years");
        experience.add("8 - 9 Years");
        experience.add("10 - 11 Years");
        experience.add( "11+ Years");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, experience);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exp_spinner.setAdapter(arrayAdapter);
        exp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                item = "0 - 1 Year";
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
                    TastyToast.makeText(AddJob.this, "Image Saved!", TastyToast.LENGTH_SHORT,TastyToast.INFO).show();
                    companyLogo.setImageBitmap(bitmap);
                    imgPath = path;

                } catch (IOException e) {
                    e.printStackTrace();
                    TastyToast.makeText(AddJob.this, "Failed!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            companyLogo.setImageBitmap(thumbnail);
            imgPath = saveImage(thumbnail);
            TastyToast.makeText(AddJob.this, "Image Saved!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
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
        final String comapnyName = comapnyname_et.getText().toString();
        final String jobProfile = jobprofile_et.getText().toString();
        final String jobdescription = jobdescription_et.getText().toString();
        final String lastdate = lastdate_et.getText().toString();
        final String location = joblocation_et.getText().toString();
        final String userId = auth.getCurrentUser().getUid();
        final String applyLink = apply.getText().toString();
        final String id = mDatabase.push().getKey();
        FileCompressor compressor = new FileCompressor(getApplicationContext());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        UploadTask uploadTask;
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        storageReference = jobrefrence.child("alumniapp"+id);
        uploadTask = storageReference.putFile(compressor.imageCompressor(path),metadata);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String  url = uri1.toString();
            Jobs uploadJobs = new Jobs(id, url, comapnyName, jobProfile, jobdescription, lastdate, item, location, userId, applyLink);
            mDatabase.child("Jobs").child(id).setValue(uploadJobs).addOnSuccessListener(aVoid -> {
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
        });
    }

    public void upload(){
        final String comapnyName = comapnyname_et.getText().toString();
        final String jobProfile = jobprofile_et.getText().toString();
        final String jobdescription = jobdescription_et.getText().toString();
        final String lastdate = lastdate_et.getText().toString();
        final String location = joblocation_et.getText().toString();
        final String applyLink = apply.getText().toString();
        final String userId = auth.getCurrentUser().getUid();
        final String id = mDatabase.push().getKey();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Adding Event ....");
        progressDialog.show();

        final Jobs uploadPost = new Jobs(id, null, comapnyName, jobProfile, jobdescription, lastdate, item, location, userId, applyLink);
        mDatabase.child("Jobs").child(id).setValue(uploadPost).addOnSuccessListener(aVoid -> {
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
}


package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EditJobActivity extends AppCompatActivity {

    EditText comapnyname_et;
    EditText jobprofile_et;
    EditText jobdescription_et;
    EditText lastdate_et;
    EditText joblocation_et;
    EditText applyL;
    Spinner exp_spinner;
    Button update;
    Button cancel;
    LoadingDialog loadingDialog;
    Calendar calendar;
    String item;
    String id;
    String cn;
    String jp;
    String jd;
    String jl;
    String ld;
    String ex;
    String apply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        comapnyname_et = findViewById(R.id.ucompanyname_et);
        jobprofile_et = findViewById(R.id.ujobprofile_et);
        jobdescription_et = findViewById(R.id.ujob_description_et);
        lastdate_et = findViewById(R.id.ulast_date_et);
        joblocation_et = findViewById(R.id.ujob_location_et);
        applyL = findViewById(R.id.uapply_link);
        exp_spinner = findViewById(R.id.uexp_spinner);
        update = findViewById(R.id.updateJob);
        cancel = findViewById(R.id.canceljob);
        loadingDialog = new LoadingDialog(EditJobActivity.this);

        id = getIntent().getStringExtra("id");
        cn = getIntent().getStringExtra("cn");
        jp = getIntent().getStringExtra("jp");
        jd = getIntent().getStringExtra("jd");
        ld = getIntent().getStringExtra("ld");
        jl = getIntent().getStringExtra("jl");
        ex = getIntent().getStringExtra("ex");
        apply = getIntent().getStringExtra("apply");

        comapnyname_et.setText(cn);
        jobprofile_et.setText(jp);
        jobdescription_et.setText(jd);
        lastdate_et.setText(ld);
        joblocation_et.setText(jl);
        applyL.setText(apply);

        calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "dd/MM/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            lastdate_et.setText(sdf.format(calendar.getTime()));
        };

        lastdate_et.setOnClickListener(v -> new DatePickerDialog(EditJobActivity.this, dateSetListener, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());


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

        if (experience.get(0).equals(ex)) {
            exp_spinner.setSelection(0);
        } else if (experience.get(1).equals(ex)){
            exp_spinner.setSelection(1);
        } else if (experience.get(2).equals(ex)){
            exp_spinner.setSelection(2);
        } else if (experience.get(3).equals(ex)){
            exp_spinner.setSelection(3);
        } else if (experience.get(4).equals(ex)){
            exp_spinner.setSelection(4);
        } else if (experience.get(5).equals(ex)){
            exp_spinner.setSelection(5);
        } else if (experience.get(6).equals(ex)){
            exp_spinner.setSelection(6);
        }

        update.setOnClickListener(v -> {
            update(id);
        });

        cancel.setOnClickListener(v -> finish());
    }

    private void update(String id) {
        loadingDialog.showLoading();
        String cn = comapnyname_et.getText().toString();
        String jp = jobprofile_et.getText().toString();
        String jd = jobdescription_et.getText().toString();
        String ld = lastdate_et.getText().toString();
        String jl = joblocation_et.getText().toString();
        String ap = applyL.getText().toString();
        String ex = item;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Jobs").child(id);
        reference.child("companyName").setValue(cn);
        reference.child("jobProfile").setValue(jp);
        reference.child("jobDescription").setValue(jd);
        reference.child("lastDate").setValue(ld);
        reference.child("location").setValue(jl);
        reference.child("applyLink").setValue(ap);
        reference.child("experience").setValue(ex).addOnCompleteListener(task -> {
            loadingDialog.hideLoading();
            TastyToast.makeText(EditJobActivity.this, "Job Edited", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            finish();
        });
    }
}
